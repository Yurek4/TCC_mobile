import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { colors, radius, spacing } from '../theme';

/**
 * Monograma + wordmark -- dá identidade visual própria ao app, em vez de
 * só um título de tela genérico. Usado na tela de Login e como cabeçalho
 * das telas internas.
 */
export default function Logo({ size = 'lg' }) {
  const grande = size === 'lg';

  return (
    <View style={styles.container}>
      <View style={[styles.monograma, grande ? styles.monogramaGrande : styles.monogramaPequeno]}>
        <Text style={[styles.monogramaTexto, grande && styles.monogramaTextoGrande]}>C</Text>
      </View>
      <View>
        <Text style={[styles.wordmark, grande && styles.wordmarkGrande]}>CONCESSIONÁRIA</Text>
        <Text style={styles.tagline}>ÁREA DO VENDEDOR</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flexDirection: 'row', alignItems: 'center', gap: spacing.sm },
  monograma: {
    backgroundColor: colors.accent,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: radius.md,
  },
  monogramaGrande: { width: 44, height: 44 },
  monogramaPequeno: { width: 32, height: 32 },
  monogramaTexto: { color: colors.textPrimary, fontWeight: '900', fontSize: 16 },
  monogramaTextoGrande: { fontSize: 22 },
  wordmark: { color: colors.textPrimary, fontWeight: '800', fontSize: 13, letterSpacing: 1.5 },
  wordmarkGrande: { fontSize: 17 },
  tagline: { color: colors.gold, fontSize: 10, fontWeight: '700', letterSpacing: 2, marginTop: 2 },
});
