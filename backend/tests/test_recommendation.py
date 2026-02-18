"""
Basic unit tests for the recommendation engine and auth service.
Run: pytest tests/
"""
import pytest
from app.services.recommendation import generate_recommendation
from app.services.auth import hash_password, verify_password


# Auth tests
def test_password_hashing():
    password = "SuperSecret123"
    hashed = hash_password(password)

    assert hashed != password
    assert verify_password(password, hashed)

    assert not verify_password("WrongPass", hashed)


# Recommendation engine tests
class MockDog:
    def __init__(self, name, allergies=None, health_conditions=None):
        self.name = name
        self.allergies = allergies or []
        self.health_conditions = health_conditions or []


class MockProduct:
    def __init__(self, name, brand, ingredients, eco_score=None):
        self.name = name
        self.brand = brand
        self.ingredients = ingredients
        self.eco_score = eco_score


def test_no_warnings_safe_product():
    dog = MockDog("Buddy")
    product = MockProduct("Safe Chicken Food", "GoodBrand", ["chicken", "rice", "oatmeal"], "A")
    rec = generate_recommendation(dog, product)

    assert "No known harmful ingredients" in rec
    assert "Eco Rating A" in rec


def test_xylitol_detected():
    dog = MockDog("Max")
    product = MockProduct("Dangerous Treat", "BadBrand", ["wheat flour", "xylitol", "sugar"])
    rec = generate_recommendation(dog, product)

    assert "Xylitol" in rec or "DANGER" in rec


def test_allergen_alert():
    dog = MockDog("Bella", allergies=["chicken"])
    product = MockProduct("Chicken Meal", "Brand", ["chicken", "rice", "peas"])
    rec = generate_recommendation(dog, product)

    assert "ALLERGEN ALERT" in rec


def test_health_condition_warning():
    dog = MockDog("Rex", health_conditions=["diabetes"])
    product = MockProduct("Sweet Treat", "Brand", ["wheat", "corn syrup", "molasses"])
    rec = generate_recommendation(dog, product)

    assert "diabetes" in rec or "HEALTH WARNING" in rec


def test_multiple_warnings():
    dog = MockDog("Charlie", allergies=["grapes"], health_conditions=["kidney disease"])
    product = MockProduct("Mixed Product", "Brand", ["grapes", "sodium chloride", "phosphorus"])
    rec = generate_recommendation(dog, product)

    assert "DANGER" in rec or "WARNING" in rec