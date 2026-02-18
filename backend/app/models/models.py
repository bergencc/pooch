import uuid
from datetime import datetime, timezone
from sqlalchemy import (
    Column, String, Integer, DECIMAL, TIMESTAMP, Text, ForeignKey,
    Enum as SAEnum, ARRAY, JSON
)
from sqlalchemy.dialects.postgresql import UUID, JSONB
from sqlalchemy.orm import relationship
from app.database import Base


def utcnow():
    return datetime.now(timezone.utc)


class User(Base):
    __tablename__ = "users"

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    email = Column(String(255), unique=True, nullable=False)
    password_hash = Column(String(255), nullable=False)
    name = Column(String(255))
    is_admin = Column(SAEnum("user", "admin", name="user_role"), default="user", nullable=False)
    created_at = Column(TIMESTAMP(timezone=True), default=utcnow)
    updated_at = Column(TIMESTAMP(timezone=True), default=utcnow, onupdate=utcnow)

    dogs = relationship("Dog", back_populates="owner", cascade="all, delete-orphan")


class Dog(Base):
    __tablename__ = "dogs"

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(UUID(as_uuid=True), ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    name = Column(String(255), nullable=False)
    breed = Column(String(255))
    age = Column(Integer)
    weight = Column(DECIMAL(5, 2))
    activity_level = Column(SAEnum("low", "medium", "high", name="activity_level_enum"))
    allergies = Column(ARRAY(Text), default=list)
    health_conditions = Column(ARRAY(Text), default=list)
    photo_url = Column(String(500))
    created_at = Column(TIMESTAMP(timezone=True), default=utcnow)
    updated_at = Column(TIMESTAMP(timezone=True), default=utcnow, onupdate=utcnow)

    owner = relationship("User", back_populates="dogs")
    scan_history = relationship("ScanHistory", back_populates="dog", cascade="all, delete-orphan")


class Product(Base):
    __tablename__ = "products"

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    barcode = Column(String(50), unique=True, nullable=False)
    name = Column(String(255), nullable=False)
    brand = Column(String(255))
    product_type = Column(SAEnum("food", "treat", "medication", "supplement", "toy", "other", name="product_type_enum"))
    ingredients = Column(ARRAY(Text), default=list)
    nutrition_info = Column(JSONB)
    eco_score = Column(String(1))  # A-F
    photo_url = Column(String(500))
    created_at = Column(TIMESTAMP(timezone=True), default=utcnow)
    updated_at = Column(TIMESTAMP(timezone=True), default=utcnow, onupdate=utcnow)

    scan_history = relationship("ScanHistory", back_populates="product")


class ScanHistory(Base):
    __tablename__ = "scan_history"

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    dog_id = Column(UUID(as_uuid=True), ForeignKey("dogs.id", ondelete="CASCADE"), nullable=False)
    product_id = Column(UUID(as_uuid=True), ForeignKey("products.id", ondelete="SET NULL"), nullable=True)
    recommendation = Column(Text)
    created_at = Column(TIMESTAMP(timezone=True), default=utcnow)

    dog = relationship("Dog", back_populates="scan_history")
    product = relationship("Product", back_populates="scan_history")
