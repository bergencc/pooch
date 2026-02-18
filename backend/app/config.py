from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import List
import json


class Settings(BaseSettings):
    # database
    DATABASE_URL: str = ""
    REDIS_URL: str = ""

    # auth
    SECRET_KEY: str = ""
    ALGORITHM: str = ""
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 0

    # aws s3
    AWS_ACCESS_KEY_ID: str = ""
    AWS_SECRET_ACCESS_KEY: str = ""
    AWS_REGION: str = ""
    S3_BUCKET_NAME: str = ""

    # app
    DEBUG: bool = True
    CORS_ORIGINS: str = "[]"

    def get_cors_origins(self) -> List[str]:
        return json.loads(self.CORS_ORIGINS)

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")


settings = Settings()
