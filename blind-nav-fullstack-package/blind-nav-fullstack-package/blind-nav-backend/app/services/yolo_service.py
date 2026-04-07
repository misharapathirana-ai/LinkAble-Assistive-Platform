from ultralytics import YOLO
from app.core.config import MODEL_PATH, CONFIDENCE_THRESHOLD, IMAGE_SIZE, MAX_OBSTACLES
from app.services.guidance_service import (
    get_direction,
    estimate_distance_category,
    build_guidance,
    prioritize_detections,
    filter_relevant_classes,
)

class YOLODetectionService:
    def __init__(self):
        self.model = YOLO(MODEL_PATH)

    def detect(self, image):
        height, width = image.shape[:2]

        results = self.model.predict(
            source=image,
            conf=CONFIDENCE_THRESHOLD,
            imgsz=IMAGE_SIZE,
            verbose=False
        )

        raw_items = []

        for result in results:
            if result.boxes is None:
                continue

            for box in result.boxes:
                cls_id = int(box.cls[0].item())
                conf = float(box.conf[0].item())
                x1, y1, x2, y2 = box.xyxy[0].tolist()

                label = self.model.names[cls_id]
                x_center = (x1 + x2) / 2.0
                box_width = x2 - x1
                box_height = y2 - y1

                direction = get_direction(x_center, width)
                distance_category = estimate_distance_category(box_width, box_height, width, height)
                guidance = build_guidance(label, direction, distance_category)

                raw_items.append({
                    "object_name": label,
                    "confidence": round(conf, 4),
                    "direction": direction,
                    "distance_category": distance_category,
                    "bbox": [round(x1, 1), round(y1, 1), round(x2, 1), round(y2, 1)],
                    "guidance": guidance,
                })

        relevant = filter_relevant_classes(raw_items)
        prioritized = prioritize_detections(relevant, MAX_OBSTACLES)

        if not prioritized and raw_items:
            prioritized = prioritize_detections(raw_items, MAX_OBSTACLES)

        return prioritized

yolo_service = YOLODetectionService()
