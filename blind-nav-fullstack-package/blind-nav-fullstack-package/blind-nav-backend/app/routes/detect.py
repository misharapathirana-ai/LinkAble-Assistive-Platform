from fastapi import APIRouter, UploadFile, File, HTTPException
from app.schemas.detect import DetectResponse
from app.services.yolo_service import yolo_service
from app.utils.image import bytes_to_bgr_image

router = APIRouter(prefix="/detect", tags=["detect"])

@router.post("", response_model=DetectResponse)
async def detect_obstacles(file: UploadFile = File(...)):
    try:
        content = await file.read()
        image = bytes_to_bgr_image(content)
        detections = yolo_service.detect(image)

        message = detections[0]["guidance"] if detections else "Path ahead appears clear."

        return DetectResponse(
            success=True,
            message=message,
            detections=detections
        )
    except Exception as error:
        raise HTTPException(status_code=400, detail=str(error))
