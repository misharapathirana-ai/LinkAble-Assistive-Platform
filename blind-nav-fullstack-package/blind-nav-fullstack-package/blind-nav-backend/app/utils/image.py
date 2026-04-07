import numpy as np
import cv2

def bytes_to_bgr_image(file_bytes: bytes):
    np_arr = np.frombuffer(file_bytes, np.uint8)
    image = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)
    if image is None:
        raise ValueError("Invalid image data")
    return image
