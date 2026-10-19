import React, { useState, useCallback, useMemo } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, Alert, RefreshControl, ScrollView } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useAuth } from '../context/AuthContext';
import { useTabStats } from '../context/TabStatsContext';
import { getCarrosDisponiveis, registrarVenda } from '../api';
import { colors, radius, spacing, cardShadow } from '../theme';
import LevelBadge from '../components/LevelBadge';
import StateBlock from '../components/StateBlock';

const FILTROS = [
  { id: 'todos', label: 'Todos' },
  { id: 'basico', label: 'Básico' },
  { id: 'medio', label: 'Médio' },
  { id: 'luxo', label: 'Luxo' },
];

const TAXA = {
  basico: 0.02, 'básico': 0.02,
  medio: 0.05, 'médio': 0.05,
  luxo: 0.08,
};

function estimarComissao(preco, nivel) {
  const taxa = TAXA[String(nivel || '').toLowerCase()] ?? 0;
  return Number(preco) * taxa;
}

function nivelNormalizado(nivel) {
  const n = String(nivel || '').toLowerCase();
  if (n === 'básico' || n === 'basico') return 'basico';
  if (n === 'médio' || n === 'medio') return 'medio';
  if (n === 'luxo') return 'luxo';
  return n;
}

export default function CarrosScreen({ navigation }) {
  const { sessao } = useAuth();
  const { atualizarCarros, sincronizarStats } = useTabStats();
  const [carros, setCarros] = useState([]);
  const [filtro, setFiltro] = useState('todos');
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [erro, setErro] = useState(null);
  const [vendendoId, setVendendoId] = useState(null);

  const carregar = useCallback(async () => {
    try {
      const data = await getCarrosDisponiveis(sessao.token);
      setCarros(data);
      atualizarCarros(data.length);
      setErro(null);
    } catch (e) {
      setErro(e.message);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [sessao.token, atualizarCarros]);

  React.useEffect(() => {
    const unsubscribe = navigation.addListener('focus', carregar);
    return unsubscribe;
  }, [navigation, carregar]);

  const carrosFiltrados = useMemo(() => {
    if (filtro === 'todos') return carros;
    return carros.filter((c) => nivelNormalizado(c.nivel) === filtro);
  }, [carros, filtro]);

  function confirmarVenda(carro) {
    if (sessao.funcionarioId == null) {
      Alert.alert(
        'Cadastro incompleto',
        'Seu usuário não está vinculado a um funcionário. Peça ao gerente para corrigir no desktop (aba Usuários).'
      );
      return;
    }

    const comissaoEstimada = estimarComissao(carro.preco, carro.nivel);
    Alert.alert(
      'Confirmar venda',
      `${carro.nome} ${carro.modelo}\nAno: ${carro.ano}\nPreço: R$ ${Number(carro.preco).toFixed(2)}\nComissão estimada: R$ ${comissaoEstimada.toFixed(2)}\n\nRegistrar esta venda em seu nome?`,
      [
        { text: 'Cancelar', style: 'cancel' },
        { text: 'Confirmar venda', onPress: () => efetivarVenda(carro) },
      ]
    );
  }

  async function efetivarVenda(carro) {
    setVendendoId(carro.id);
    try {
      const resultado = await registrarVenda(carro.id, sessao.token);
      Alert.alert(
        'Venda registrada!',
        `${resultado.carroNome}\nValor: R$ ${Number(resultado.valorVenda).toFixed(2)}\nSua comissão: R$ ${Number(resultado.comissao).toFixed(2)}`,
        [
          { text: 'OK' },
          { text: 'Ver minhas vendas', onPress: () => navigation.navigate('Vendas') },
        ]
      );
      await carregar();
      await sincronizarStats();
    } catch (e) {
      Alert.alert('Não foi possível registrar a venda', e.message);
    } finally {
      setVendendoId(null);
    }
  }

  if (loading && carros.length === 0) {
    return (
      <View style={[styles.container, styles.centered]}>
        <StateBlock loading title="Carregando carros..." />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <View>
          <Text style={styles.headerTitulo}>Carros disponíveis</Text>
          <Text style={styles.headerSubtitulo}>
            {carrosFiltrados.length} {carrosFiltrados.length === 1 ? 'veículo' : 'veículos'}
            {filtro !== 'todos' ? ` · filtro ${FILTROS.find((f) => f.id === filtro)?.label}` : ' para venda'}
          </Text>
        </View>
      </View>

      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.filtros}
        style={styles.filtrosScroll}
      >
        {FILTROS.map((f) => {
          const ativo = filtro === f.id;
          return (
            <TouchableOpacity
              key={f.id}
              style={[styles.chip, ativo && styles.chipAtivo]}
              onPress={() => setFiltro(f.id)}
              activeOpacity={0.8}
            >
              <Text style={[styles.chipTexto, ativo && styles.chipTextoAtivo]}>{f.label}</Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>

      {erro ? <Text style={styles.erro}>{erro}</Text> : null}

      <FlatList
        data={carrosFiltrados}
        keyExtractor={(item) => String(item.id)}
        contentContainerStyle={{ paddingBottom: spacing.xl }}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={() => { setRefreshing(true); carregar(); }}
            tintColor={colors.accent}
            colors={[colors.accent]}
          />
        }
        ListEmptyComponent={
          <StateBlock
            icon={erro ? 'alert-circle-outline' : 'car-outline'}
            title={erro ? 'Não foi possível carregar os carros.' : 'Nenhum carro disponível'}
            description={filtro === 'todos' ? 'Tente atualizar para buscar novos veículos.' : 'Altere o filtro para ver outros níveis.'}
            actionLabel="Tentar novamente"
            onAction={carregar}
          />
        }
        renderItem={({ item }) => {
          const comissao = estimarComissao(item.preco, item.nivel);
          const ocupado = vendendoId === item.id;
          return (
            <View style={[styles.card, ocupado && styles.cardOcupado]}>
              <View style={styles.cardTopo}>
                <View style={styles.iconeCarro}>
                  <Ionicons name="car-sport" size={22} color={colors.accent} />
                </View>
                <View style={{ flex: 1 }}>
                  <Text style={styles.nome}>{item.nome} {item.modelo}</Text>
                  <Text style={styles.ano}>Ano {item.ano}</Text>
                </View>
                <LevelBadge nivel={item.nivel} mostrarComissao />
              </View>

              <View style={styles.linha} />

              <View style={styles.cardBase}>
                <View>
                  <Text style={styles.preco}>R$ {Number(item.preco).toFixed(2)}</Text>
                  <Text style={styles.comissaoPrev}>Comissão ~ R$ {comissao.toFixed(2)}</Text>
                </View>
                <TouchableOpacity
                  style={[styles.btnVender, ocupado && styles.btnVenderOff]}
                  onPress={() => confirmarVenda(item)}
                  activeOpacity={0.85}
                  disabled={ocupado}
                >
                  <Ionicons name="checkmark-circle-outline" size={16} color={colors.textPrimary} />
                  <Text style={styles.btnVenderText}>{ocupado ? 'Salvando...' : 'Vender'}</Text>
                </TouchableOpacity>
              </View>
            </View>
          );
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: spacing.md, backgroundColor: colors.bgPrimary },
  centered: { justifyContent: 'center', alignItems: 'center' },
  loading: { color: colors.textSecondary },
  header: { marginBottom: spacing.sm },
  headerTitulo: { color: colors.textPrimary, fontSize: 20, fontWeight: '800' },
  headerSubtitulo: { color: colors.textMuted, fontSize: 13, marginTop: 2 },
  filtrosScroll: { flexGrow: 0, marginBottom: spacing.md },
  filtros: { gap: spacing.sm, paddingRight: spacing.sm },
  chip: {
    paddingVertical: 8,
    paddingHorizontal: 14,
    borderRadius: radius.pill,
    backgroundColor: colors.bgCard,
    borderWidth: 1,
    borderColor: colors.border,
  },
  chipAtivo: {
    backgroundColor: colors.accent,
    borderColor: colors.accent,
  },
  chipTexto: { color: colors.textMuted, fontSize: 12, fontWeight: '700' },
  chipTextoAtivo: { color: colors.textPrimary },
  erro: { color: '#FF8A8A', marginBottom: spacing.sm },
  vazio: { alignItems: 'center', marginTop: spacing.xl, gap: spacing.sm },
  empty: { color: colors.textMuted, textAlign: 'center' },
  card: {
    backgroundColor: colors.bgCard,
    padding: spacing.md,
    borderRadius: radius.lg,
    marginBottom: spacing.sm,
    borderWidth: 1,
    borderColor: colors.border,
    ...cardShadow,
  },
  cardOcupado: { opacity: 0.7 },
  cardTopo: { flexDirection: 'row', alignItems: 'center', gap: spacing.sm },
  iconeCarro: {
    width: 40, height: 40, borderRadius: radius.md,
    backgroundColor: colors.bgInput, alignItems: 'center', justifyContent: 'center',
  },
  nome: { color: colors.textPrimary, fontSize: 16, fontWeight: '800' },
  ano: { color: colors.textMuted, fontSize: 12, marginTop: 2 },
  linha: { height: 1, backgroundColor: colors.border, marginVertical: spacing.sm },
  cardBase: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  preco: { color: colors.textPrimary, fontSize: 18, fontWeight: '800' },
  comissaoPrev: { color: colors.success, fontSize: 12, fontWeight: '700', marginTop: 2 },
  btnVender: {
    flexDirection: 'row', alignItems: 'center', gap: 6,
    backgroundColor: colors.accent, paddingVertical: 10, paddingHorizontal: 16, borderRadius: radius.md,
  },
  btnVenderOff: { opacity: 0.6 },
  btnVenderText: { color: colors.textPrimary, fontWeight: '800', fontSize: 13 },
});
