from fastapi import APIRouter, Depends, HTTPException, UploadFile, File
from sqlalchemy.orm import Session
from typing import List
from uuid import UUID
from app.database import get_db
from app.schemas.schemas import DogCreate, DogUpdate, DogOut
from app.models.models import Dog, User
from app.middleware.auth_deps import get_current_user
from app.services.storage import upload_image, delete_image

router = APIRouter(prefix="/dogs", tags=["Dogs"])


def _get_dog_or_404(dog_id: UUID, user: User, db: Session) -> Dog:
    dog = db.query(Dog).filter(Dog.id == dog_id, Dog.user_id == user.id).first()

    if not dog:
        raise HTTPException(status_code=404, detail="Dog not found")

    return dog


@router.get("", response_model=List[DogOut])
def list_dogs(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    return db.query(Dog).filter(Dog.user_id == current_user.id).all()


@router.post("", response_model=DogOut, status_code=201)
def create_dog(
    payload: DogCreate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    dog = Dog(user_id=current_user.id, **payload.model_dump())

    db.add(dog)
    db.commit()
    db.refresh(dog)

    return dog


@router.get("/{dog_id}", response_model=DogOut)
def get_dog(
    dog_id: UUID,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    return _get_dog_or_404(dog_id, current_user, db)


@router.put("/{dog_id}", response_model=DogOut)
def update_dog(
    dog_id: UUID,
    payload: DogUpdate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    dog = _get_dog_or_404(dog_id, current_user, db)
    update_data = payload.model_dump(exclude_unset=True)

    for field, value in update_data.items():
        setattr(dog, field, value)

    db.commit()
    db.refresh(dog)

    return dog


@router.delete("/{dog_id}", status_code=204)
def delete_dog(
    dog_id: UUID,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    dog = _get_dog_or_404(dog_id, current_user, db)

    db.delete(dog)
    db.commit()


@router.post("/{dog_id}/photo", response_model=DogOut)
async def upload_dog_photo(
    dog_id: UUID,
    file: UploadFile = File(...),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    dog = _get_dog_or_404(dog_id, current_user, db)

    if dog.photo_url:
        await delete_image(dog.photo_url)

    dog.photo_url = await upload_image(file, folder="dogs")

    db.commit()
    db.refresh(dog)

    return dog
