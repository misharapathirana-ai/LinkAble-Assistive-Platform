import * as Speech from 'expo-speech';
import { setAudioModeAsync } from 'expo-audio';

export async function speak(text: string) {
  try {
    await setAudioModeAsync({
      playsInSilentMode: true,
      shouldRouteThroughEarpiece: false
    });
  } catch (error) {
    console.log('Audio mode setup failed', error);
  }

  Speech.stop();
  Speech.speak(text, {
    rate: 0.95,
    pitch: 1.0,
    language: 'en'
  });
}

export function stopSpeaking() {
  Speech.stop();
}
