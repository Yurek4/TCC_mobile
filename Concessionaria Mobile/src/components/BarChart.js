import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { colors, radius, spacing } from '../theme';

const ALTURA_MAX = 120;

/**
 * Gráfico de barras bem simples, construído só com <View> -- de propósito,
 * pra não depender de nenhuma biblioteca externa de gráficos (menos uma
 * dependência que pode quebrar em builds do EAS).
 *
 * data: [{ label: 'Jan', value: 1200 }, ...]
 */
export default function BarChart({ data }) {
  if (!data || data.length === 0) return null;

  const maiorValor = Math.max(...data.map((d) => d.value), 1);

  return (
    <View style={styles.container}>
      {data.map((item, i) => {
        const alturaBarra = Math.max((item.value / maiorValor) * ALTURA_MAX, 4);
        return (
          <View key={i} style={styles.coluna}>
            <Text style={styles.valor}>
              {item.value >= 1000 ? `${(item.value / 1000).toFixed(1)}k` : item.value.toFixed(0)}
            </Text>
            <View style={styles.trilho}>
              <View style={[styles.barra, { height: alturaBarra }]} />
            </View>
            <Text style={styles.label}>{item.label}</Text>
          </View>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    justifyContent: 'space-around',
    paddingTop: spacing.md,
    height: ALTURA_MAX + 56,
  },
  coluna: { alignItems: 'center', flex: 1 },
  valor: { color: colors.textSecondary, fontSize: 10, marginBottom: 4, fontWeight: '700' },
  trilho: { height: ALTURA_MAX, justifyContent: 'flex-end' },
  barra: { width: 18, backgroundColor: colors.accent, borderRadius: radius.sm },
  label: { color: colors.textMuted, fontSize: 10, marginTop: 6 },
});
