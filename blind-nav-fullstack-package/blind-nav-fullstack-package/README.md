# Blind Navigation App - Frontend + Backend

This package contains:

- `blind-nav-frontend/` -> Expo React Native app
- `blind-nav-backend/` -> FastAPI + YOLO backend

## Final folder layout

Keep both folders side by side:

```text
your-project/
  blind-nav-frontend/
  blind-nav-backend/
```

## 1. Frontend setup

Open a terminal:

```bash
cd blind-nav-frontend
npm install
```

### Important: set the backend IP
Open this file:

```text
src/services/api.ts
```

Replace:

```ts
const API_BASE_URL = 'http://YOUR_PC_IP:8000';
```

with your laptop Wi-Fi IPv4 address, for example:

```ts
const API_BASE_URL = 'http://192.168.8.153:8000';
```

### Run frontend

```bash
npx expo start -c --tunnel
```

Then scan the QR from Expo Go.

## 2. Backend setup

Open a second terminal:

```bash
cd blind-nav-backend
python -m venv venv
```

### Activate the virtual environment

#### PowerShell
```powershell
.\venv\Scripts\Activate.ps1
```

### Install backend packages

```bash
pip install -r requirements.txt
```

### Create `.env`
Copy `.env.example` to `.env`

#### PowerShell
```powershell
Copy-Item .env.example .env
```

### Run backend

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

## 3. Test backend first

Open in browser:

- `http://127.0.0.1:8000/health`
- `http://127.0.0.1:8000/docs`

If both open, backend is running.

## 4. What the app does

### Frontend
- app starts with automatic 3-second countdown
- camera opens automatically
- captures a photo frame every 3.5 seconds
- sends frame to backend
- receives navigation guidance
- speaks guidance out loud
- closes after 10 seconds of inactivity with voice announcement

### Backend
- receives uploaded frame
- runs YOLO object detection
- estimates object direction: left / center / right
- estimates distance level: near / medium / far
- returns guidance message

## 5. Current frontend structure

```text
blind-nav-frontend/
  App.tsx
  app.json
  babel.config.js
  package.json
  tsconfig.json
  src/
    components/
      StatusCard.tsx
    hooks/
      useInactivityTimer.ts
    navigation/
      RootNavigator.tsx
    screens/
      CountdownScreen.tsx
      NavigationScreen.tsx
    services/
      api.ts
      speechService.ts
```

## 6. Current backend structure

```text
blind-nav-backend/
  .env.example
  requirements.txt
  app/
    main.py
    core/
      config.py
    routes/
      health.py
      detect.py
    schemas/
      detect.py
    services/
      guidance_service.py
      yolo_service.py
    utils/
      image.py
```

## 7. Notes

- The app speaks through the phone using `expo-speech`.
- Audio route is set to speaker in `speechService.ts`.
- Backend does not speak. It only returns guidance text.
- Distance is approximate (`near`, `medium`, `far`), not exact meters.
- For local testing, laptop and phone must be on the same Wi-Fi.

## 8. Common issues

### Frontend cannot reach backend
- check the IP in `src/services/api.ts`
- make sure laptop firewall is not blocking port `8000`
- make sure phone and laptop are on same Wi-Fi

### Backend import/model issue
Run again:

```bash
pip install -r requirements.txt
```

### Expo cache issue
Run:

```bash
npx expo start -c --tunnel
```

## 9. Current inactivity close behavior

- After 10 seconds without timer reset, the app announces:
  - `User is not active. App is closing.`
- On Android it exits the app
- On iPhone it returns to the countdown screen

## 10. Later improvements

- replace generic YOLO model with custom-trained obstacle model
- add better obstacle priority logic
- use motion analysis instead of only timer-based inactivity
- add Siri / Google Assistant opening support
- reduce repeated announcements
