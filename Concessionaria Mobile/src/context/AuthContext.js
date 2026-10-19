import React, { createContext, useContext, useState, useEffect } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { login as apiLogin } from '../api';

/**
 * Guarda a sessão do vendedor logado (token JWT + dados do usuário) e
 * disponibiliza pra qualquer tela via useAuth(). Também salva no
 * armazenamento do celular (AsyncStorage) para o vendedor não precisar
 * logar de novo toda vez que abrir o app.
 */
const AuthContext = createContext(null);
const STORAGE_KEY = '@concessionaria:sessao';

export function AuthProvider({ children }) {
  const [sessao, setSessao] = useState(null); // { token, username, papel, funcionarioId }
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    AsyncStorage.getItem(STORAGE_KEY)
      .then((json) => {
        if (json) setSessao(JSON.parse(json));
      })
      .finally(() => setCarregando(false));
  }, []);

  async function entrar(username, senha) {
    const data = await apiLogin(username, senha);

    // O app mobile é só para vendedores -- gerente usa o desktop.
    if (data.papel !== 'VENDEDOR') {
      throw new Error('Este app é exclusivo para vendedores. Use o sistema desktop para acessar como gerente.');
    }

    // Sem vínculo com funcionário a API rejeita o POST /vendas.
    if (data.funcionarioId == null) {
      throw new Error(
        'Seu usuário ainda não está vinculado a um funcionário. Peça ao gerente para editar o usuário no desktop e selecionar o funcionário.'
      );
    }

    setSessao(data);
    await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(data));
  }

  async function sair() {
    setSessao(null);
    await AsyncStorage.removeItem(STORAGE_KEY);
  }

  return (
    <AuthContext.Provider value={{ sessao, carregando, entrar, sair }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
