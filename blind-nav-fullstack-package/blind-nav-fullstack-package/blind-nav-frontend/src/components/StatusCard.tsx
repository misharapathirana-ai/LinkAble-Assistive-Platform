import React from 'react';
import { StyleSheet, Text, View } from 'react-native';

type Props = {
  title: string;
  value: string;
};

export default function StatusCard({ title, value }: Props) {
  return (
    <View style={styles.card}>
      <Text style={styles.title}>{title}</Text>
      <Text style={styles.value}>{value}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#111827',
    borderRadius: 14,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: '#1f2937'
  },
  title: {
    color: '#94a3b8',
    fontSize: 14,
    marginBottom: 6
  },
  value: {
    color: '#fff',
    fontSize: 18,
    fontWeight: '700'
  }
});
