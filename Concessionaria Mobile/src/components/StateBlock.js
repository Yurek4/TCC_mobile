import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ActivityIndicator } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors, spacing } from '../theme';

export default function StateBlock({ icon, title, description, loading = false, actionLabel, onAction }) {
  return (
    <View style={styles.wrap}>
      {loading ? (
        <ActivityIndicator size="large" color={colors.accent} />
      ) : (
        <Ionicons name={icon || 'information-circle-outline'} size={42} color={colors.textMuted} />
      )}
      <Text style={styles.title}>{title}</Text>
      {description ? <Text style={styles.description}>{description}</Text> : null}
      {actionLabel && onAction ? (
        <TouchableOpacity style={styles.action} onPress={onAction} activeOpacity={0.85}>
          <Text style={styles.actionText}>{actionLabel}</Text>
        </TouchableOpacity>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: { alignItems: 'center', justifyContent: 'center', gap: spacing.sm, paddingVertical: spacing.xl },
  title: { color: colors.textPrimary, fontSize: 15, fontWeight: '700', textAlign: 'center' },
  description: { color: colors.textMuted, textAlign: 'center', maxWidth: 280 },
  action: { marginTop: spacing.xs, backgroundColor: colors.accent, borderRadius: 10, paddingHorizontal: 14, paddingVertical: 8 },
  actionText: { color: colors.textPrimary, fontWeight: '800' },
});
