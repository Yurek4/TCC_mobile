import React, { useRef, useEffect } from 'react';
import { View, ActivityIndicator, StyleSheet, Animated, Pressable } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';
import { StatusBar } from 'expo-status-bar';

import { AuthProvider, useAuth } from './src/context/AuthContext';
import { TabStatsProvider, useTabStats } from './src/context/TabStatsContext';
import LoginScreen from './src/screens/LoginScreen';
import CarrosScreen from './src/screens/CarrosScreen';
import MinhasVendasScreen from './src/screens/MinhasVendasScreen';
import PerfilScreen from './src/screens/PerfilScreen';
import { colors } from './src/theme';

const Stack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();

const ICONES = {
  Carros: 'car-sport',
  Vendas: 'cash',
  Perfil: 'person',
};

/** Ícone da aba com leve escala ao focar — mesmo visual, feedback tátil. */
function TabIcone({ routeName, color, size, focused }) {
  const escala = useRef(new Animated.Value(focused ? 1.12 : 1)).current;

  useEffect(() => {
    Animated.spring(escala, {
      toValue: focused ? 1.12 : 1,
      friction: 6,
      tension: 140,
      useNativeDriver: true,
    }).start();
  }, [focused, escala]);

  return (
    <Animated.View style={{ transform: [{ scale: escala }] }}>
      <Ionicons
        name={`${ICONES[routeName]}${focused ? '' : '-outline'}`}
        size={size - 2}
        color={color}
      />
    </Animated.View>
  );
}

/** Botão de aba com leve opacidade ao pressionar. */
function TabBotao({ children, onPress, ...rest }) {
  return (
    <Pressable
      {...rest}
      onPress={onPress}
      style={({ pressed }) => [{ flex: 1, opacity: pressed ? 0.7 : 1 }]}
      android_ripple={{ color: colors.accent + '33' }}
    >
      {children}
    </Pressable>
  );
}

/** As 3 telas principais do vendedor, já logado, em formato de abas. */
function AbasPrincipais() {
  const { carrosDisponiveis, totalVendas } = useTabStats();

  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarStyle: {
          backgroundColor: colors.bgMenu,
          borderTopColor: colors.border,
          borderTopWidth: 1,
          height: 64,
          paddingBottom: 8,
          paddingTop: 8,
          paddingHorizontal: 6,
        },
        tabBarActiveTintColor: colors.accent,
        tabBarInactiveTintColor: colors.textMuted,
        tabBarLabelStyle: { fontSize: 11, fontWeight: '700', textAlign: 'center' },
        tabBarItemStyle: { alignItems: 'center', justifyContent: 'center' },
        tabBarIconStyle: { alignSelf: 'center', marginBottom: 2 },
        tabBarBadgeStyle: {
          backgroundColor: colors.accent,
          color: colors.textPrimary,
          fontSize: 10,
          fontWeight: '800',
          minWidth: 18,
          height: 18,
          lineHeight: 17,
        },
        tabBarButton: (props) => <TabBotao {...props} />,
        tabBarIcon: ({ color, size, focused }) => (
          <TabIcone routeName={route.name} color={color} size={size} focused={focused} />
        ),
      })}
    >
      <Tab.Screen
        name="Carros"
        component={CarrosScreen}
        options={{
          tabBarBadge: carrosDisponiveis > 0 ? carrosDisponiveis : undefined,
        }}
      />
      <Tab.Screen
        name="Vendas"
        component={MinhasVendasScreen}
        options={{
          tabBarBadge: totalVendas > 0 ? totalVendas : undefined,
        }}
      />
      <Tab.Screen name="Perfil" component={PerfilScreen} />
    </Tab.Navigator>
  );
}

function Rotas() {
  const { sessao, carregando } = useAuth();

  if (carregando) {
    return (
      <View style={styles.splash}>
        <ActivityIndicator color={colors.accent} size="large" />
      </View>
    );
  }

  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      {sessao ? (
        <Stack.Screen name="Principal">
          {() => (
            <TabStatsProvider>
              <AbasPrincipais />
            </TabStatsProvider>
          )}
        </Stack.Screen>
      ) : (
        <Stack.Screen name="Login" component={LoginScreen} />
      )}
    </Stack.Navigator>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <NavigationContainer>
        <StatusBar style="light" />
        <Rotas />
      </NavigationContainer>
    </AuthProvider>
  );
}

const styles = StyleSheet.create({
  splash: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: colors.bgPrimary },
});
