"""
Image upload service supporting AWS S3.
Falls back to a local URL stub if credentials are not configured.
"""
import uuid
import boto3
from botocore.exceptions import NoCredentialsError, ClientError
from fastapi import UploadFile
from app.config import settings


def _s3_client():
    return boto3.client(
        "s3",
        aws_access_key_id=settings.AWS_ACCESS_KEY_ID,
        aws_secret_access_key=settings.AWS_SECRET_ACCESS_KEY,
        region_name=settings.AWS_REGION,
    )


async def upload_image(file: UploadFile, folder: str = "products") -> str:
    """
    Upload an image to S3 and return its public URL.
    Returns a placeholder URL if S3 is not configured.

    TODO implement properly
    """
    if not settings.AWS_ACCESS_KEY_ID or settings.AWS_ACCESS_KEY_ID == "aws-access-key":
        # Return a stub URL for development
        ext = file.filename.split(".")[-1] if "." in file.filename else "jpg"

        return f"https://placeholder.poochscan.com/{folder}/{uuid.uuid4()}.{ext}"

    try:
        s3 = _s3_client()
        ext = file.filename.split(".")[-1] if "." in file.filename else "jpg"
        key = f"{folder}/{uuid.uuid4()}.{ext}"

        contents = await file.read()

        s3.put_object(
            Bucket=settings.S3_BUCKET_NAME,
            Key=key,
            Body=contents,
            ContentType=file.content_type or "image/jpeg",
            ACL="public-read",
        )

        return f"https://{settings.S3_BUCKET_NAME}.s3.{settings.AWS_REGION}.amazonaws.com/{key}"

    except (NoCredentialsError, ClientError) as e:
        # Log and fall back gracefully
        print(f"S3 upload error: {e}")

        return f"https://placeholder.poochscan.com/{folder}/{uuid.uuid4()}.jpg"


async def delete_image(url: str) -> bool:
    """Delete an image from S3 given its URL."""
    if not url or "placeholder.poochscan.com" in url:
        return True

    try:
        s3 = _s3_client()
        # Extract key from URL
        key = "/".join(url.split("/")[3:])

        s3.delete_object(Bucket=settings.S3_BUCKET_NAME, Key=key)

        return True
    except Exception as e:
        print(f"S3 delete error: {e}")

        return False
