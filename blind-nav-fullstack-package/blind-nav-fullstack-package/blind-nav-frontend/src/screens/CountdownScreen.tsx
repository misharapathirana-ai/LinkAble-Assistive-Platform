import React, { useEffect, useState } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RootStackParamList } from '../navigation/RootNavigator';
import { speak } from '../services/speechService';

type Props = NativeStackScreenProps<RootStackParamList, 'Countdown'>;

export default function CountdownScreen({ navigation }: Props) {
  const [count, setCount] = useState(3);

  useEffect(() => {
    speak('App is opening in 3, 2, 1');

    let current = 3;
    const interval = setInterval(() => {
      current -= 1;
      setCount(current);

      if (current <= 0) {
        clearInterval(interval);
        navigation.replace('Navigation');
      }
    }, 1000);

    return () => clearInterval(interval);
  }, [navigation]);

  return (
    <View style={styles.container}>
      <Text style={styles.text}>Starting in</Text>
      <Text style={styles.count}>{count}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0b1220',
    justifyContent: 'center',
    alignItems: 'center'
  },
  text: {
    color: '#cbd5e1',
    fontSize: 24,
    marginBottom: 10
  },
  count: {
    color: '#fff',
    fontSize: 84,
    fontWeight: '800'
  }
});
