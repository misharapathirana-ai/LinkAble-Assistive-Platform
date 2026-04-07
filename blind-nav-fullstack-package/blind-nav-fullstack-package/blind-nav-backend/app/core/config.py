import os
from dotenv import load_dotenv

load_dotenv()

MODEL_PATH = os.getenv("MODEL_PATH", "yolo11n.pt")
CONFIDENCE_THRESHOLD = float(os.getenv("CONFIDENCE_THRESHOLD", "0.45"))
IMAGE_SIZE = int(os.getenv("IMAGE_SIZE", "640"))
MAX_OBSTACLES = int(os.getenv("MAX_OBSTACLES", "5"))
