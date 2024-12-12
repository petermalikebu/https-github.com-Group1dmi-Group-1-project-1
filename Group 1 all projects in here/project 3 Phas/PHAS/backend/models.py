from flask_sqlalchemy import SQLAlchemy
from datetime import datetime  # Import datetime

db = SQLAlchemy()

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100))
    email = db.Column(db.String(100), unique=True)
    password_hash = db.Column(db.String(100))  # Rename this field from 'password'
    role = db.Column(db.String(50))



class HealthData(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    heart_rate = db.Column(db.Integer, nullable=False)
    glucose_level = db.Column(db.Integer, nullable=False)
    bp_level = db.Column(db.Integer, nullable=False)
    timestamp = db.Column(db.DateTime, default=datetime.utcnow)  # Now datetime is defined