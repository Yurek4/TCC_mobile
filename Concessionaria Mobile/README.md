# Concessionária — App do Vendedor

App mobile (Expo/React Native) para os vendedores verem os carros
disponíveis e registrarem vendas. Conecta na API Java (Spring Boot)
que já testamos — não acessa o banco de dados diretamente.

## Antes de rodar

1. A **API Java precisa estar rodando** (veja o projeto `concessionaria-api`).
2. Abra `src/api.js` e confira o `BASE_URL`:
   - Testando no **emulador Android** com a API rodando no seu PC: `http://10.0.2.2:8080` (já é o padrão).
   - Testando no **celular físico**, mesma rede Wi-Fi do PC: troque para `http://SEU_IP_LOCAL:8080` (descubra seu IP com `ipconfig` no Windows, procure por "Endereço IPv4").
   - Depois de publicar a API no Render: troque para `https://sua-api.onrender.com`.

## Instalar e rodar

```bash
npm install
npx expo start
```

Escaneie o QR code com o app **Expo Go** (disponível na Play Store/App Store) no celular, ou pressione `a` no terminal para abrir num emulador Android já configurado.

## Criando um usuário VENDEDOR para testar

O cadastro de usuários continua sendo feito pelo desktop (ou diretamente
no banco). É preciso que o usuário tenha `papel = 'VENDEDOR'` e esteja
vinculado a um `funcionario_id` válido — sem isso, o registro de venda
não funciona (a API precisa saber qual funcionário está vendendo).

## Estrutura

```
App.js                        # navegação + decide Login vs telas internas
src/
  api.js                      # chamadas HTTP para a API Java
  theme.js                    # paleta de cores (igual ao desktop)
  context/AuthContext.js      # guarda o token JWT e os dados da sessão
  screens/
    LoginScreen.js
    CarrosScreen.js           # lista carros disponíveis + registrar venda
    MinhasVendasScreen.js     # histórico de vendas do vendedor logado
```
