from pydantic import BaseModel, EmailStr, field_validator
from typing import Optional, List, Dict, Any
from datetime import datetime
from uuid import UUID
from enum import Enum


# Enums
class ActivityLevel(str, Enum):
    low = "low"
    medium = "medium"
    high = "high"


class ProductType(str, Enum):
    food = "food"
    treat = "treat"
    medication = "medication"
    supplement = "supplement"
    toy = "toy"
    other = "other"


class EcoScore(str, Enum):
    A = "A"
    B = "B"
    C = "C"
    D = "D"
    E = "E"
    F = "F"


# Auth
class UserRegister(BaseModel):
    email: EmailStr
    password: str
    name: Optional[str] = None

    @field_validator("password")
    @classmethod
    def password_min_length(cls, v: str) -> str:
        if len(v) < 8:
            raise ValueError("Password must be at least 8 characters")
        return v


class UserLogin(BaseModel):
    email: EmailStr
    password: str


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    expires_in: int


class UserOut(BaseModel):
    id: UUID
    email: str
    name: Optional[str]
    is_admin: str
    created_at: datetime

    model_config = {"from_attributes": True}


# Dogs
class DogCreate(BaseModel):
    name: str
    breed: Optional[str] = None
    age: Optional[int] = None
    weight: Optional[float] = None
    activity_level: Optional[ActivityLevel] = None
    allergies: Optional[List[str]] = []
    health_conditions: Optional[List[str]] = []


class DogUpdate(BaseModel):
    name: Optional[str] = None
    breed: Optional[str] = None
    age: Optional[int] = None
    weight: Optional[float] = None
    activity_level: Optional[ActivityLevel] = None
    allergies: Optional[List[str]] = None
    health_conditions: Optional[List[str]] = None


class DogOut(BaseModel):
    id: UUID
    user_id: UUID
    name: str
    breed: Optional[str]
    age: Optional[int]
    weight: Optional[float]
    activity_level: Optional[str]
    allergies: List[str]
    health_conditions: List[str]
    photo_url: Optional[str]
    created_at: datetime
    updated_at: datetime

    model_config = {"from_attributes": True}


# Products
class ProductCreate(BaseModel):
    barcode: str
    name: str
    brand: Optional[str] = None
    product_type: Optional[ProductType] = None
    ingredients: Optional[List[str]] = []
    nutrition_info: Optional[Dict[str, Any]] = None
    eco_score: Optional[EcoScore] = None


class ProductUpdate(BaseModel):
    name: Optional[str] = None
    brand: Optional[str] = None
    product_type: Optional[ProductType] = None
    ingredients: Optional[List[str]] = None
    nutrition_info: Optional[Dict[str, Any]] = None
    eco_score: Optional[EcoScore] = None


class ProductOut(BaseModel):
    id: UUID
    barcode: str
    name: str
    brand: Optional[str]
    product_type: Optional[str]
    ingredients: List[str]
    nutrition_info: Optional[Dict[str, Any]]
    eco_score: Optional[str]
    photo_url: Optional[str]
    created_at: datetime
    updated_at: datetime

    model_config = {"from_attributes": True}


class ProductListResponse(BaseModel):
    items: List[ProductOut]
    total: int
    page: int
    per_page: int
    pages: int


# Scans
class ScanRequest(BaseModel):
    barcode: str
    dog_id: UUID


class ScanOut(BaseModel):
    id: UUID
    dog_id: UUID
    product_id: Optional[UUID]
    recommendation: Optional[str]
    created_at: datetime
    product: Optional[ProductOut] = None

    model_config = {"from_attributes": True}


class ScanHistoryResponse(BaseModel):
    items: List[ScanOut]
    total: int
