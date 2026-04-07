from typing import List, Dict

IMPORTANT_CLASSES = {
    "person", "car", "motorcycle", "bicycle", "bus", "truck",
    "chair", "bench", "potted plant", "fire hydrant", "stop sign",
    "dog", "cat", "table", "sofa", "bed", "door", "traffic light"
}

def get_direction(x_center: float, image_width: int) -> str:
    third = image_width / 3
    if x_center < third:
        return "left"
    if x_center < 2 * third:
        return "center"
    return "right"

def estimate_distance_category(box_width: float, box_height: float, image_width: int, image_height: int) -> str:
    area_ratio = (box_width * box_height) / float(image_width * image_height)

    if area_ratio > 0.20:
        return "near"
    if area_ratio > 0.08:
        return "medium"
    return "far"

def build_guidance(label: str, direction: str, distance_category: str) -> str:
    if distance_category == "near":
        if direction == "left":
            return f"There is a {label} on the left. Move slightly right and continue carefully."
        if direction == "right":
            return f"There is a {label} on the right. Move slightly left and continue carefully."
        return f"There is a {label} ahead. Slow down and move carefully to the left or right."

    if distance_category == "medium":
        if direction == "left":
            return f"{label.capitalize()} detected on the left. Keep a little to the right."
        if direction == "right":
            return f"{label.capitalize()} detected on the right. Keep a little to the left."
        return f"{label.capitalize()} detected ahead. Continue carefully."

    return f"{label.capitalize()} detected ahead, but it is still far."

def prioritize_detections(items: List[Dict], max_items: int) -> List[Dict]:
    priority_score = {"near": 3, "medium": 2, "far": 1}
    return sorted(
        items,
        key=lambda x: (priority_score.get(x["distance_category"], 0), x["confidence"]),
        reverse=True
    )[:max_items]

def filter_relevant_classes(detections: List[Dict]) -> List[Dict]:
    return [item for item in detections if item["object_name"] in IMPORTANT_CLASSES]
