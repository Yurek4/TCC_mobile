import React, { useState, useCallback } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, RefreshControl, Alert } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useAuth } from '../context/AuthContext';
import { useTabStats } from '../context/TabStatsContext';
import { getMinhasVendas } from '../api';
import { colors, radius, spacing, cardShadow } from '../theme';
import StatCard from '../components/StatCard';

function iniciais(nome) {
  if (!nome) return '?';
  return nome.trim().slice(0, 2).toUpperCase();
}

export default function PerfilScreen({ navigation }) {
  const { sessao, sair } = useAuth();
  const { atualizarVendas } = useTabStats();
  const [resumo, setResumo] = useState({ totalVendas: 0, totalComissao: 0 });
  const [refreshing, setRefreshing] = useState(false);

  const carregar = useCallback(async () => {
    try {
      const vendas = await getMinhasVendas(sessao.token);
      const totalComissao = vendas.reduce((s, v) => s + v.comissao, 0);
      setResumo({ totalVendas: vendas.length, totalComissao });
      atualizarVendas(vendas.length);
    } catch (e) {
      // Falha silenciosa aqui -- o Perfil não deve travar por causa disso,
      // os dados básicos do usuário (nome, cargo) já vêm da sessão local.
    } finally {
      setRefreshing(false);
    }
  }, [sessao.token, atualizarVendas]);

  React.useEffect(() => {
    const unsubscribe = navigation.addListener('focus', carregar);
    return unsubscribe;
  }, [navigation, carregar]);

  function confirmarLogout() {
    Alert.alert(
      'Sair da conta',
      'Deseja encerrar a sessão neste aparelho?',
      [
        { text: 'Cancelar', style: 'cancel' },
        { text: 'Sair', style: 'destructive', onPress: () => sair() },
      ]
    );
  }

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={{ padding: spacing.md, paddingBottom: spacing.xl }}
      refreshControl={
        <RefreshControl
          refreshing={refreshing}
          onRefresh={() => { setRefreshing(true); carregar(); }}
          tintColor={colors.accent}
          colors={[colors.accent]}
        />
      }
    >
      <Text style={styles.headerTitulo}>Perfil</Text>

      <View style={styles.cardPerfil}>
        <View style={styles.avatar}>
          <Text style={styles.avatarTexto}>{iniciais(sessao.username)}</Text>
        </View>
        <Text style={styles.nome}>{sessao.username}</Text>
        <View style={styles.cargoBadge}>
          <Ionicons name="briefcase-outline" size={12} color={colors.accent} />
          <Text style={styles.cargoTexto}>VENDEDOR</Text>
        </View>
        {sessao.funcionarioId != null && (
          <Text style={styles.idFuncionario}>Funcionário #{sessao.funcionarioId}</Text>
        )}
      </View>

      <View style={styles.statsRow}>
        <StatCard label="Vendas realizadas" valor={String(resumo.totalVendas)} />
        <StatCard label="Comissão acumulada" valor={`R$ ${resumo.totalComissao.toFixed(0)}`} destaque />
      </View>

      <View style={styles.secao}>
        <View style={styles.linhaInfo}>
          <Ionicons name="server-outline" size={16} color={colors.textMuted} />
          <Text style={styles.linhaInfoTexto}>Conectado ao servidor da concessionária</Text>
        </View>
      </View>

      <TouchableOpacity style={styles.btnLogout} onPress={confirmarLogout} activeOpacity={0.85}>
        <Ionicons name="log-out-outline" size={18} color={colors.textPrimary} />
        <Text style={styles.btnLogoutTexto}>Sair da conta</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.bgPrimary },
  headerTitulo: { color: colors.textPrimary, fontSize: 20, fontWeight: '800', marginBottom: spacing.md },
  cardPerfil: {
    backgroundColor: colors.bgCard,
    borderRadius: radius.lg,
    padding: spacing.lg,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: colors.border,
    marginBottom: spacing.md,
    ...cardShadow,
  },
  avatar: {
    width: 72, height: 72, borderRadius: 36,
    backgroundColor: colors.accent, alignItems: 'center', justifyContent: 'center',
    marginBottom: spacing.sm,
  },
  avatarTexto: { color: colors.textPrimary, fontSize: 26, fontWeight: '800' },
  nome: { color: colors.textPrimary, fontSize: 18, fontWeight: '800' },
  cargoBadge: {
    flexDirection: 'row', alignItems: 'center', gap: 4,
    backgroundColor: colors.bgInput, borderRadius: radius.pill,
    paddingVertical: 4, paddingHorizontal: 10, marginTop: 8,
  },
  cargoTexto: { color: colors.accent, fontSize: 11, fontWeight: '800', letterSpacing: 0.5 },
  idFuncionario: { color: colors.textMuted, fontSize: 12, marginTop: 8 },
  statsRow: { flexDirection: 'row', gap: spacing.sm, marginBottom: spacing.md },
  secao: {
    backgroundColor: colors.bgCardAlt,
    borderRadius: radius.md,
    padding: spacing.md,
    borderWidth: 1,
    borderColor: colors.border,
    marginBottom: spacing.lg,
  },
  linhaInfo: { flexDirection: 'row', alignItems: 'center', gap: spacing.sm },
  linhaInfoTexto: { color: colors.textMuted, fontSize: 12, flex: 1 },
  btnLogout: {
    flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: spacing.sm,
    backgroundColor: colors.danger, borderRadius: radius.md, padding: 14,
  },
  btnLogoutTexto: { color: colors.textPrimary, fontWeight: '800', fontSize: 14 },
});
