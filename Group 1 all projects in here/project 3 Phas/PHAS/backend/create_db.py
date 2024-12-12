import sys
import os

# Add the parent directory (PHAS) to sys.path so Python can find the 'app' module
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

# Now import from app.py and models.py
from backend.app import create_app  # Corrected import from the backend directory
from backend.models import db, User, HealthData  # Corrected import from models.py

app = create_app()

with app.app_context():
    db.create_all()  # Create all tables in the database
