/**
 * Cliente HTTP da API Java (Spring Boot), publicada no Render.
 *
 * Se precisar voltar a testar contra a API rodando local no seu PC:
 *   - Emulador Android:                       http://10.0.2.2:8080
 *   - Celular físico, mesma rede Wi-Fi do PC:  http://SEU_IP_LOCAL:8080
 */
export const BASE_URL = 'https://concessionaria-api-rypy.onrender.com';

async function request(path, { method = 'GET', body, token } = {}) {
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  let res;
  try {
    res = await fetch(`${BASE_URL}${path}`, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    });
  } catch (e) {
    // Erro de rede (API fora do ar, IP errado, sem internet etc.)
    throw new Error('Não foi possível conectar à API. Verifique o endereço em src/api.js e se a API está rodando.');
  }

  const data = await res.json().catch(() => ({}));

  if (!res.ok) {
    // O formato de erro da API Java é { "mensagem": "..." }
    throw new Error(data.mensagem || 'Erro ao comunicar com o servidor.');
  }
  return data;
}

export function login(username, senha) {
  return request('/auth/login', { method: 'POST', body: { username, senha } });
}

export function getCarrosDisponiveis(token) {
  return request('/carros/disponiveis', { token });
}

export function registrarVenda(carroId, token) {
  return request('/vendas', { method: 'POST', body: { carroId }, token });
}

export function getMinhasVendas(token) {
  return request('/vendas/minhas', { token });
}
