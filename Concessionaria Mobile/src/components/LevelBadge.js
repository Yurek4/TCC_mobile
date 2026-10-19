import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { colors, radius } from '../theme';

const CONFIG = {
  basico:  { cor: colors.nivelBasico, label: 'BÁSICO', comissao: '2%' },
  'básico': { cor: colors.nivelBasico, label: 'BÁSICO', comissao: '2%' },
  medio:   { cor: colors.nivelMedio,  label: 'MÉDIO',  comissao: '5%' },
  'médio':  { cor: colors.nivelMedio,  label: 'MÉDIO',  comissao: '5%' },
  luxo:    { cor: colors.nivelLuxo,   label: 'LUXO',    comissao: '8%' },
};

export default function LevelBadge({ nivel, mostrarComissao = false }) {
  const chave = String(nivel || '').toLowerCase();
  const config = CONFIG[chave] || { cor: colors.textMuted, label: String(nivel || '—').toUpperCase(), comissao: '' };

  return (
    <View style={[styles.badge, { backgroundColor: config.cor + '26', borderColor: config.cor }]}>
      <View style={[styles.dot, { backgroundColor: config.cor }]} />
      <Text style={[styles.texto, { color: config.cor }]}>
        {config.label}{mostrarComissao && config.comissao ? ` · ${config.comissao} comissão` : ''}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  badge: {
    flexDirection: 'row',
    alignItems: 'center',
    alignSelf: 'flex-start',
    paddingVertical: 4,
    paddingHorizontal: 10,
    borderRadius: radius.pill,
    borderWidth: 1,
  },
  dot: { width: 6, height: 6, borderRadius: 3, marginRight: 6 },
  texto: { fontSize: 11, fontWeight: '800', letterSpacing: 0.5 },
});
