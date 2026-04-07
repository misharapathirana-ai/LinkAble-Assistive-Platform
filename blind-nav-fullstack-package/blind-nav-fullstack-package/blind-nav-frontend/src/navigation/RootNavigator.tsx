import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import CountdownScreen from '../screens/CountdownScreen';
import NavigationScreen from '../screens/NavigationScreen';

export type RootStackParamList = {
  Countdown: undefined;
  Navigation: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export default function RootNavigator() {
  return (
    <Stack.Navigator
      initialRouteName="Countdown"
      screenOptions={{
        headerStyle: { backgroundColor: '#0f172a' },
        headerTintColor: '#fff',
        contentStyle: { backgroundColor: '#0b1220' }
      }}
    >
      <Stack.Screen
        name="Countdown"
        component={CountdownScreen}
        options={{ title: 'Starting Session', headerShown: false }}
      />
      <Stack.Screen
        name="Navigation"
        component={NavigationScreen}
        options={{ title: 'Live Navigation' }}
      />
    </Stack.Navigator>
  );
}
