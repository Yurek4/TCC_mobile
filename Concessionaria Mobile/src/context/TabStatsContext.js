import React, { createContext, useContext, useState, useCallback, useEffect } from 'react';
import { AppState } from 'react-native';
import { useAuth } from './AuthContext';
import { getCarrosDisponiveis, getMinhasVendas } from '../api';

/**
 * Contadores compartilhados entre as telas e a barra de abas,
 * para badges e subtítulos atualizarem sem mudar o visual.
 */
const TabStatsContext = createContext(null);

export function TabStatsProvider({ children }) {
  const { sessao } = useAuth();
  const [carrosDisponiveis, setCarrosDisponiveis] = useState(0);
  const [totalVendas, setTotalVendas] = useState(0);

  const atualizarCarros = useCallback((n) => setCarrosDisponiveis(n), []);
  const atualizarVendas = useCallback((n) => setTotalVendas(n), []);
  const sincronizarStats = useCallback(async () => {
    if (!sessao?.token) {
      setCarrosDisponiveis(0);
      setTotalVendas(0);
      return;
    }

    const [carrosResp, vendasResp] = await Promise.allSettled([
      getCarrosDisponiveis(sessao.token),
      getMinhasVendas(sessao.token),
    ]);

    if (carrosResp.status === 'fulfilled') {
      setCarrosDisponiveis(Array.isArray(carrosResp.value) ? carrosResp.value.length : 0);
    }
    if (vendasResp.status === 'fulfilled') {
      setTotalVendas(Array.isArray(vendasResp.value) ? vendasResp.value.length : 0);
    }
  }, [sessao?.token]);

  useEffect(() => {
    sincronizarStats();

    const appStateSub = AppState.addEventListener('change', (state) => {
      if (state === 'active') sincronizarStats();
    });

    const intervalId = setInterval(sincronizarStats, 30000);

    return () => {
      appStateSub.remove();
      clearInterval(intervalId);
    };
  }, [sincronizarStats]);

  return (
    <TabStatsContext.Provider
      value={{ carrosDisponiveis, totalVendas, atualizarCarros, atualizarVendas, sincronizarStats }}
    >
      {children}
    </TabStatsContext.Provider>
  );
}

export function useTabStats() {
  return useContext(TabStatsContext);
}
