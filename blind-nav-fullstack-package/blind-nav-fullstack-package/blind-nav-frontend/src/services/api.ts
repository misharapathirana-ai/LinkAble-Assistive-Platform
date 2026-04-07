const API_BASE_URL = 'http://YOUR_PC_IP:8000';

export type BackendDetectionItem = {
  object_name: string;
  confidence: number;
  direction: 'left' | 'center' | 'right';
  distance_category: 'near' | 'medium' | 'far';
  bbox: number[];
  guidance: string;
};

export type DetectResponse = {
  success: boolean;
  message: string;
  detections: BackendDetectionItem[];
};

export async function detectFromBackend(imageUri: string): Promise<DetectResponse> {
  const formData = new FormData();

  formData.append('file', {
    uri: imageUri,
    name: 'frame.jpg',
    type: 'image/jpeg'
  } as any);

  const response = await fetch(`${API_BASE_URL}/detect`, {
    method: 'POST',
    body: formData
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`Detection failed: ${text}`);
  }

  return response.json();
}
