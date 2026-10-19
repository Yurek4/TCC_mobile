/**
 * Paleta copiada direto do desktop (InterfaceGrafica.java / LoginFrame.java)
 * para os dois aplicativos parecerem do mesmo fornecedor.
 */
export const colors = {
  bgPrimary: '#0C1428',    // azulFundo do desktop
  bgMenu: '#0F1E3C',       // azulMenu (topo/cabeçalho)
  bgCard: '#193264',       // fundo dos cards
  bgCardAlt: '#152A54',    // variação mais escura, para hierarquia visual
  bgInput: '#0F1E3C',
  accent: '#007ACC',       // azulBotao do desktop
  gold: '#D4AF37',         // toque de identidade -- reservado para "Luxo" e destaques
  danger: '#B41E1E',       // vermelho do botão de logout
  success: '#2ECC71',
  border: '#324664',       // grid/borda das tabelas
  textPrimary: '#FFFFFF',
  textSecondary: '#96C8FF',
  textMuted: '#7C8CAE',

  // Cores por nível de carro -- usadas nos badges
  nivelBasico: '#5C7099',
  nivelMedio: '#007ACC',
  nivelLuxo: '#D4AF37',
};

export const radius = { sm: 6, md: 10, lg: 16, pill: 999 };

export const spacing = { xs: 4, sm: 8, md: 16, lg: 24, xl: 32 };

// Sombra padrão dos cards -- funciona em iOS (shadow*) e Android (elevation)
export const cardShadow = {
  shadowColor: '#000',
  shadowOffset: { width: 0, height: 2 },
  shadowOpacity: 0.25,
  shadowRadius: 4,
  elevation: 3,
};
