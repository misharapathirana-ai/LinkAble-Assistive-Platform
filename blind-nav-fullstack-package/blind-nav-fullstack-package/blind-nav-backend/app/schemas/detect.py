from pydantic import BaseModel
from typing import Literal

Direction = Literal["left", "center", "right"]
DistanceCategory = Literal["near", "medium", "far"]

class DetectionItem(BaseModel):
    object_name: str
    confidence: float
    direction: Direction
    distance_category: DistanceCategory
    bbox: list[float]
    guidance: str

class DetectResponse(BaseModel):
    success: bool
    message: str
    detections: list[DetectionItem]
