import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, ActivityIndicator, KeyboardAvoidingView, Platform } from 'react-native';
import { useAuth } from '../context/AuthContext';
import { colors, radius, spacing } from '../theme';
import Logo from '../components/Logo';

export default function LoginScreen() {
  const { entrar } = useAuth();
  const [username, setUsername] = useState('');
  const [senha, setSenha] = useState('');
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState(null);

  async function handleLogin() {
    if (!username.trim() || !senha) {
      setErro('Preencha usuário e senha.');
      return;
    }
    setErro(null);
    setLoading(true);
    try {
      await entrar(username.trim(), senha);
    } catch (e) {
      setErro(e.message || 'Não foi possível fazer login.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      <View style={styles.centro}>
        <View style={styles.logoWrapper}>
          <Logo size="lg" />
        </View>

        <View style={styles.card}>
          <Text style={styles.cardTitulo}>Entrar</Text>

          <Text style={styles.rotulo}>Usuário</Text>
          <TextInput
            style={styles.input}
            placeholder="seu.usuario"
            placeholderTextColor={colors.textMuted}
            value={username}
            onChangeText={setUsername}
            autoCapitalize="none"
            autoCorrect={false}
          />

          <Text style={styles.rotulo}>Senha</Text>
          <TextInput
            style={styles.input}
            placeholder="••••••••"
            placeholderTextColor={colors.textMuted}
            value={senha}
            onChangeText={setSenha}
            secureTextEntry
          />

          {erro ? <Text style={styles.erro}>{erro}</Text> : null}

          <TouchableOpacity style={styles.button} onPress={handleLogin} disabled={loading} activeOpacity={0.85}>
            {loading ? <ActivityIndicator color={colors.textPrimary} /> : <Text style={styles.buttonText}>Entrar</Text>}
          </TouchableOpacity>
        </View>

        <Text style={styles.rodape}>Acesso exclusivo para vendedores cadastrados</Text>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.bgPrimary },
  centro: { flex: 1, justifyContent: 'center', padding: spacing.lg },
  logoWrapper: { alignItems: 'center', marginBottom: spacing.xl },
  card: {
    backgroundColor: colors.bgCard,
    borderRadius: radius.lg,
    padding: spacing.lg,
    borderWidth: 1,
    borderColor: colors.border,
  },
  cardTitulo: { color: colors.textPrimary, fontSize: 18, fontWeight: '800', marginBottom: spacing.md },
  rotulo: { color: colors.textSecondary, fontSize: 12, fontWeight: '700', marginBottom: 6, marginTop: spacing.sm },
  input: {
    backgroundColor: colors.bgInput,
    borderRadius: radius.md,
    padding: 14,
    color: colors.textPrimary,
    borderWidth: 1,
    borderColor: colors.border,
  },
  erro: { color: '#FF8A8A', marginTop: spacing.sm, textAlign: 'center' },
  button: {
    backgroundColor: colors.accent,
    borderRadius: radius.md,
    padding: 16,
    marginTop: spacing.lg,
    alignItems: 'center',
  },
  buttonText: { color: colors.textPrimary, fontWeight: '800', fontSize: 15, letterSpacing: 0.5 },
  rodape: { color: colors.textMuted, fontSize: 12, textAlign: 'center', marginTop: spacing.lg },
});
