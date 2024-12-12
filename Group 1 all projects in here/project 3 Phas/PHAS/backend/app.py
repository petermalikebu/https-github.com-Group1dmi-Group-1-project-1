import os
import json
import random
import pandas as pd
import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt
from datetime import datetime, timezone
from flask import Flask, render_template, request, jsonify, redirect, url_for, session
from flask_login import LoginManager, login_required, UserMixin
from werkzeug.security import generate_password_hash
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import accuracy_score
import joblib
from backend.models import db, User, HealthData  # Ensure to import your HealthData model
from werkzeug.security import check_password_hash


# Flask App Setup
def create_app():
    app = Flask(__name__, template_folder='../frontend/templates', static_folder='../frontend/static')
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///C:/Users/PETER/PythonProject/PHAS/backend/database.db'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    app.secret_key = 'your_secret_key'  # Set a secret key for session management

    db.init_app(app)

    # Initialize LoginManager
    login_manager = LoginManager()
    login_manager.init_app(app)
    login_manager.login_view = 'login'  # Set the login view

    @login_manager.user_loader
    def load_user(user_id):
        return User.query.get(int(user_id))

    # Load model and scaler data
    model_path = os.path.join('backend', 'models', 'trained_health_model.pkl')
    scaler_path = os.path.join('backend', 'models', 'scaler.pkl')

    model = None
    scaler = None

    # Check if model and scaler are saved, load them; else, train them
    if os.path.exists(model_path) and os.path.exists(scaler_path):
        model = joblib.load(model_path)
        scaler = joblib.load(scaler_path)
        print("Model and scaler loaded successfully.")
    else:
        # If not available, train the model
        model_data_path = os.path.join(os.getcwd(), 'backend', 'models', 'trained_health_model.csv')
        if os.path.exists(model_data_path):
            model_data = pd.read_csv(model_data_path)
            print("Model data loaded successfully.")

            # Prepare data for model training
            scaler = MinMaxScaler()
            X = scaler.fit_transform(model_data[['HeartRate', 'GlucoseLevel', 'BloodPressure']])
            y = model_data['RiskCategory']

            # Train a Predictive Model
            X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
            model = RandomForestClassifier(n_estimators=100, random_state=42)
            model.fit(X_train, y_train)

            accuracy = accuracy_score(y_test, model.predict(X_test))
            print(f"Model Training Complete. Accuracy: {accuracy * 100:.2f}%")

            # Save the trained model and scaler for future use
            joblib.dump(model, model_path)
            joblib.dump(scaler, scaler_path)
            print("Model and scaler saved successfully.")
        else:
            print(f"Error: Model data file does not exist at {model_data_path}")

    # Define routes
    @app.route('/')
    def home():
        return render_template('base.html')

    @app.route('/register', methods=['GET', 'POST'])
    def register():
        if request.method == 'POST':
            data = request.get_json() if request.is_json else request.form
            name = data.get('name')
            email = data.get('email')
            password = data.get('password')
            role = data.get('role')

            existing_user = User.query.filter_by(email=email).first()
            if existing_user:
                return jsonify(message="User already exists!"), 400

            hashed_password = generate_password_hash(password)
            new_user = User(name=name, email=email, password_hash=hashed_password,
                            role=role)  # Use password_hash instead of password
            db.session.add(new_user)
            db.session.commit()

            return jsonify(message="User registered successfully!"), 201

        return render_template('register.html')

    @app.route('/login', methods=['GET', 'POST'])
    def login():
        """Login route."""
        if request.method == 'POST':
            # Handle the login form submission
            data = request.get_json()
            email = data.get('email')
            password = data.get('password')

            # Assuming you're using a user model that has the `email`, `password_hash`, and `role`
            user = User.query.filter_by(email=email).first()

            if user and check_password_hash(user.password_hash, password):
                # Set the user session
                session['user_id'] = user.id
                session['role'] = user.role

                # Redirect based on user role
                if user.role == 'admin':
                    return jsonify({'redirect_url': url_for('admin_dashboard')})
                elif user.role == 'healthcare_provider':
                    return jsonify({'redirect_url': url_for('healthcare_provider_dashboard')})
                elif user.role == 'patient':
                    return jsonify({'redirect_url': url_for('patient_dashboard')})
                else:
                    return jsonify({'message': 'Unknown role'}), 400
            else:
                return jsonify({'message': 'Invalid credentials'}), 401

        # Render the login page for GET request
        return render_template('login.html')

    @app.route('/dashboard')
    @login_required
    def dashboard():
        """User  dashboard."""
        role = session.get('role')
        if role == 'admin':
            return redirect(url_for('admin_dashboard'))
        elif role == 'healthcare_provider':
            return redirect(url_for('healthcare_provider_dashboard'))
        elif role == 'patient':
            return redirect(url_for('patient_dashboard'))
        return redirect(url_for('home'))

    @app.route('/admin_dashboard')
    @login_required
    def admin_dashboard():
        """Admin dashboard view."""
        users = User.query.all()  # Fetch all users from the database
        return render_template('admin_dashboard.html', users=users)

    @app.route('/healthcare_provider_dashboard')
    @login_required
    def healthcare_provider_dashboard():
        """Healthcare provider dashboard view."""
        patients = User.query.filter_by(role='patient').all()  # Fetch patients assigned to this provider
        return render_template('healthcare_provider_dashboard.html', patients=patients)

    @app.route('/patient_dashboard')
    @login_required
    def patient_dashboard():
        """Patient dashboard view."""
        health_data = HealthData.query.filter_by(user_id=session['user_id']).all()  # Fetch patient's health data
        return render_template('patient_dashboard.html', health_data=health_data)

    @app.route('/upload_health_data', methods=['POST'])
    @login_required
    def upload_health_data():
        """Handle IoT device data uploads and update IoT visualization."""
        data = request.get_json()
        user_id = session.get('user_id')
        if not data:
            return jsonify({"error": "No data received"}), 400

        health_data = HealthData(
            user_id=user_id,
            heart_rate=data.get('heart_rate'),
            glucose_level=data.get('glucose_level'),
            bp_level=data.get('bp_level'),
            timestamp=datetime.utcnow()
        )

        # Insert new data into SQL database
        db.session.add(health_data)
        db.session.commit()

        # Update the iot_data.json file
        iot_file_path = os.path.join('backend', 'iot_data.json')
        if os.path.exists(iot_file_path):
            with open(iot_file_path, 'r') as f:
                existing_data = json.load(f)
        else:
            existing_data = []

        existing_data.append({
            "user_id": user_id,
            "heart_rate": health_data.heart_rate,
            "glucose_level": health_data.glucose_level,
            "bp_level": health_data.bp_level,
            "timestamp": health_data.timestamp.isoformat()
        })

        with open(iot_file_path, 'w') as f:
            json.dump(existing_data, f)

        # Regenerate the IoT data visualization
        generate_visualization()

        return jsonify({"message": "Data uploaded and visualization updated successfully!"}), 200

    @app.route('/predict_health', methods=['POST'])
    @login_required
    def predict_health():
        """Predict health risk from user-provided data."""
        data = request.get_json()
        heart_rate = data.get('heart_rate')
        glucose_level = data.get('glucose_level')
        bp_level = data.get('bp_level')

        if not all([heart_rate, glucose_level, bp_level]):
            return jsonify({"error": "Missing required fields"}), 400

        prediction = predict_health_risk(heart_rate, glucose_level, bp_level)
        risk_message = "High Risk" if prediction else "Low Risk"
        return jsonify({"prediction": risk_message}), 200

    @app.route('/view_iot_visualization')
    @login_required
    def view_iot_visualization():
        """View IoT data visualizations."""
        return render_template('iot_visualization.html', image_url='static/images/iot_visualization.png')

    @app.route('/logout')
    def logout():
        """Logout user."""
        session.clear()
        return redirect(url_for('home'))

    return app


# IoT Data Simulation for Visualization
def simulate_iot_data():
    """Simulate IoT device data for visualization."""
    data = []
    for _ in range(100):
        record = {
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "heart_rate": random.randint(60, 100),
            "glucose_level": random.randint(70, 120),
            "bp_level": random.randint(110, 130)
        }
        data.append(record)

    return pd.DataFrame(data)

def generate_visualization():
    """Generate IoT data visualization."""
    data = simulate_iot_data()
    plt.figure(figsize=(10, 6))
    plt.plot(data['timestamp'], data['heart_rate'], label='Heart Rate')
    plt.plot(data['timestamp'], data['glucose_level'], label='Glucose Level')
    plt.plot(data['timestamp'], data['bp_level'], label='Blood Pressure')
    plt.xlabel('Time')
    plt.ylabel('Measurements')
    plt.title('IoT Health Data Visualization')
    plt.legend()
    visualization_path = os.path.join('frontend', 'static', 'images', 'iot_visualization.png')
    plt.savefig(visualization_path, bbox_inches='tight')

def predict_health_risk(heart_rate, glucose_level, bp_level):
    """Predict health risk using trained model."""
    if model and scaler:
        scaled_data = scaler.transform([[heart_rate, glucose_level, bp_level]])
        prediction = model.predict(scaled_data)
        return prediction[0] == 1  # Assuming 1 indicates high risk
    return "Model or scaler not loaded correctly."

if __name__ == '__main__':
    app = create_app()
    app.run(debug=True)
