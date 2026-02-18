import math
from fastapi import APIRouter, Depends, HTTPException, UploadFile, File, Query
from sqlalchemy.orm import Session
from uuid import UUID
from app.database import get_db
from app.schemas.schemas import ProductCreate, ProductUpdate, ProductOut, ProductListResponse
from app.models.models import Product, User
from app.middleware.auth_deps import require_admin
from app.services.storage import upload_image, delete_image

router = APIRouter(prefix="/admin", tags=["Admin"])


@router.get("/products", response_model=ProductListResponse)
def list_products(
    page: int = Query(1, ge=1),
    per_page: int = Query(20, ge=1, le=100),
    search: str = Query("", description="Search by name, brand, or barcode"),
    product_type: str = Query("", description="Filter by product type"),
    db: Session = Depends(get_db),
    _: User = Depends(require_admin),
):
    query = db.query(Product)

    if search:
        query = query.filter(
            Product.name.ilike(f"%{search}%")
            | Product.brand.ilike(f"%{search}%")
            | Product.barcode.ilike(f"%{search}%")
        )

    if product_type:
        query = query.filter(Product.product_type == product_type)

    total = query.count()
    products = query.order_by(Product.created_at.desc()).offset((page - 1) * per_page).limit(per_page).all()

    return {
        "items": products,
        "total": total,
        "page": page,
        "per_page": per_page,
        "pages": math.ceil(total / per_page) if total else 1,
    }


@router.post("/products", response_model=ProductOut, status_code=201)
def create_product(
    payload: ProductCreate,
    db: Session = Depends(get_db),
    _: User = Depends(require_admin),
):
    if db.query(Product).filter(Product.barcode == payload.barcode).first():
        raise HTTPException(status_code=400, detail="Product with this barcode already exists")

    product = Product(**payload.model_dump())

    db.add(product)
    db.commit()
    db.refresh(product)

    return product


@router.get("/products/{product_id}", response_model=ProductOut)
def get_product(
    product_id: UUID,
    db: Session = Depends(get_db),
    _: User = Depends(require_admin),
):
    product = db.query(Product).filter(Product.id == product_id).first()

    if not product:
        raise HTTPException(status_code=404, detail="Product not found")

    return product


@router.put("/products/{product_id}", response_model=ProductOut)
def update_product(
    product_id: UUID,
    payload: ProductUpdate,
    db: Session = Depends(get_db),
    _: User = Depends(require_admin),
):
    product = db.query(Product).filter(Product.id == product_id).first()

    if not product:
        raise HTTPException(status_code=404, detail="Product not found")

    update_data = payload.model_dump(exclude_unset=True)

    for field, value in update_data.items():
        setattr(product, field, value)

    db.commit()
    db.refresh(product)

    return product


@router.delete("/products/{product_id}", status_code=204)
def delete_product(
    product_id: UUID,
    db: Session = Depends(get_db),
    _: User = Depends(require_admin),
):
    product = db.query(Product).filter(Product.id == product_id).first()

    if not product:
        raise HTTPException(status_code=404, detail="Product not found")

    if product.photo_url:
        import asyncio
        asyncio.create_task(delete_image(product.photo_url))

    db.delete(product)
    db.commit()


@router.post("/products/{product_id}/photo", response_model=ProductOut)
async def upload_product_photo(
    product_id: UUID,
    file: UploadFile = File(...),
    db: Session = Depends(get_db),
    _: User = Depends(require_admin),
):
    product = db.query(Product).filter(Product.id == product_id).first()

    if not product:
        raise HTTPException(status_code=404, detail="Product not found")

    if product.photo_url:
        await delete_image(product.photo_url)

    product.photo_url = await upload_image(file, folder="products")

    db.commit()
    db.refresh(product)

    return product


@router.get("/stats", tags=["Admin"])
def admin_stats(
    db: Session = Depends(get_db),
    _: User = Depends(require_admin),
):
    from app.models.models import User as UserModel, Dog, ScanHistory

    return {
        "total_products": db.query(Product).count(),
        "total_users": db.query(UserModel).filter(UserModel.is_admin == "user").count(),
        "total_dogs": db.query(Dog).count(),
        "total_scans": db.query(ScanHistory).count(),
    }
