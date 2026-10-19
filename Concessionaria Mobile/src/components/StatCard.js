import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { colors, radius, spacing, cardShadow } from '../theme';

export default function StatCard({ label, valor, destaque = false }) {
  return (
    <View style={[styles.card, destaque && styles.cardDestaque]}>
      <Text style={styles.label}>{label}</Text>
      <Text style={[styles.valor, destaque && styles.valorDestaque]}>{valor}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    flex: 1,
    backgroundColor: colors.bgCard,
    borderRadius: radius.md,
    padding: spacing.md,
    borderWidth: 1,
    borderColor: colors.border,
    ...cardShadow,
  },
  cardDestaque: { borderColor: colors.gold, backgroundColor: colors.bgCardAlt },
  label: { color: colors.textMuted, fontSize: 11, fontWeight: '700', letterSpacing: 0.5, textTransform: 'uppercase' },
  valor: { color: colors.textPrimary, fontSize: 20, fontWeight: '800', marginTop: 6 },
  valorDestaque: { color: colors.gold },
});
