import React, { useCallback, useEffect, useRef, useState } from 'react';
import {
  StyleSheet,
  Text,
  TouchableWithoutFeedback,
  View,
  Platform,
  BackHandler
} from 'react-native';
import { CameraView, useCameraPermissions } from 'expo-camera';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RootStackParamList } from '../navigation/RootNavigator';
import StatusCard from '../components/StatusCard';
import { detectFromBackend } from '../services/api';
import { speak, stopSpeaking } from '../services/speechService';
import { useInactivityTimer } from '../hooks/useInactivityTimer';

type Props = NativeStackScreenProps<RootStackParamList, 'Navigation'>;

export default function NavigationScreen({ navigation }: Props) {
  const [permission, requestPermission] = useCameraPermissions();
  const [latestMessage, setLatestMessage] = useState('Waiting for detections...');
  const [lastObject, setLastObject] = useState('None');
  const [isDetecting, setIsDetecting] = useState(false);

  const cameraRef = useRef<CameraView | null>(null);
  const closedRef = useRef(false);
  const lastSpokenRef = useRef<string>('');
  const lastSpokenAtRef = useRef<number>(0);

  const closeAppFlow = useCallback(async () => {
    if (closedRef.current) return;
    closedRef.current = true;

    stopSpeaking();
    await speak('User is not active. App is closing.');

    setTimeout(() => {
      if (Platform.OS === 'android') {
        BackHandler.exitApp();
      } else {
        navigation.reset({
          index: 0,
          routes: [{ name: 'Countdown' }]
        });
      }
    }, 1800);
  }, [navigation]);

  const { resetTimer } = useInactivityTimer(10000, () => {
    closeAppFlow();
  });

  useEffect(() => {
    requestPermission();
  }, [requestPermission]);

  const maybeSpeak = useCallback(async (message: string) => {
    const now = Date.now();
    const sameAsLast = lastSpokenRef.current === message;
    const tooSoon = now - lastSpokenAtRef.current < 3000;

    if (sameAsLast && tooSoon) return;

    lastSpokenRef.current = message;
    lastSpokenAtRef.current = now;
    await speak(message);
  }, []);

  useEffect(() => {
    if (!permission?.granted) return;

    const interval = setInterval(async () => {
      if (!cameraRef.current || isDetecting || closedRef.current) return;

      try {
        setIsDetecting(true);

        const photo = await cameraRef.current.takePictureAsync({
          quality: 0.4,
          skipProcessing: true
        });

        if (!photo?.uri) return;

        const result = await detectFromBackend(photo.uri);

        const message = result?.message || 'Path ahead appears clear.';
        const first = result?.detections?.[0];

        setLatestMessage(message);
        setLastObject(first?.object_name ?? 'None');

        if (message) {
          await maybeSpeak(message);
        }

        resetTimer();
      } catch (error) {
        console.log(error);
        setLatestMessage('Backend connection failed.');
      } finally {
        setIsDetecting(false);
      }
    }, 3500);

    return () => clearInterval(interval);
  }, [permission?.granted, isDetecting, maybeSpeak, resetTimer]);

  if (!permission) {
    return (
      <View style={styles.center}>
        <Text style={styles.info}>Loading camera permission...</Text>
      </View>
    );
  }

  if (!permission.granted) {
    return (
      <View style={styles.center}>
        <Text style={styles.info}>Camera permission is required.</Text>
      </View>
    );
  }

  return (
    <TouchableWithoutFeedback onPress={resetTimer}>
      <View style={styles.container}>
        <View style={styles.cameraWrap}>
          <CameraView
            ref={(ref) => {
              cameraRef.current = ref;
            }}
            style={styles.camera}
            facing="back"
          />
        </View>

        <View style={styles.panel}>
          <StatusCard title="Latest Guidance" value={latestMessage} />
          <StatusCard title="Detected Object" value={lastObject} />
          <StatusCard title="Detection Status" value={isDetecting ? 'Processing' : 'Idle'} />

          <Text style={styles.closeText} onPress={closeAppFlow}>
            End Session
          </Text>
        </View>
      </View>
    </TouchableWithoutFeedback>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0b1220'
  },
  cameraWrap: {
    flex: 1
  },
  camera: {
    flex: 1
  },
  panel: {
    padding: 16,
    backgroundColor: '#0b1220'
  },
  info: {
    color: '#fff',
    fontSize: 18
  },
  center: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#0b1220'
  },
  closeText: {
    color: '#60a5fa',
    textAlign: 'center',
    marginTop: 12,
    fontSize: 16,
    fontWeight: '700'
  }
});
