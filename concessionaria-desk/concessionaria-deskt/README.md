# Concessionária X — Desktop (Versão Melhorada)

## O que foi corrigido nesta versão

### 🔴 Segurança Crítica

| Problema | Solução aplicada |
|----------|-----------------|
| Credenciais do banco expostas no código | Movidas para `resources/config.properties` (fora do Git) |
| Senhas armazenadas em texto puro | Hash BCrypt antes de salvar (`PasswordUtil.hashSenha()`) |
| Senha visível ao cadastrar usuário | `JPasswordField` com campo de confirmação de senha |
| Comparação de senha em SQL (`WHERE senha = ?`) | Verificação local com `BCrypt.checkpw()` |

### 🟡 Usabilidade

| Problema | Solução aplicada |
|----------|-----------------|
| IDs de carro/funcionário digitados à mão na venda | `JComboBox` carregado do banco com dados reais |
| Comissão calculada manualmente | Calculada automaticamente pelo `ComissaoService` ao selecionar o carro |
| Dashboard sem dados reais | Cards exibem contagens reais do banco |
| Carro não era marcado como "Vendido" ao vender | `DaoVendasBD.registrarVenda()` atualiza o status em transação |
| Schema do banco recriado a cada conexão | `DatabaseConnection.initialize()` chamado uma única vez no `Main` |

### 🟢 Código / Boas Práticas

| Problema | Solução aplicada |
|----------|-----------------|
| Getters fora do padrão Java (`getnome()`, `getid()`) | Renomeados para `getNome()`, `getId()`, `getSalario()` |
| `Databaseconection` (typo) | Renomeado para `DatabaseConnection` |
| Tabela `vendas` sem Foreign Keys | Adicionado `REFERENCES` nas colunas `id_carro` e `id_funcionario` |
| Exceções silenciosas com `e.printStackTrace()` | DAOs propagam `SQLException` para a view tratar com mensagem ao usuário |
| `Usuario` carregava senha do banco | Objeto `Usuario` não tem mais campo `senha` — hash fica somente no banco |
| Usuário logado não aparecia na interface | Título da janela e menu lateral mostram o nome do usuário logado |
| Gerente podia se auto-excluir | Verificação impede excluir o próprio usuário logado |

---

## Configuração do Projeto

### 1. Dependências (PostgreSQL + jBCrypt)

Este projeto já possui `pom.xml` com as dependências:
- `org.postgresql:postgresql`
- `org.mindrot:jbcrypt`

No IntelliJ, **não precisa adicionar JAR manualmente**:
1. Abra a pasta `concessionaria-desktop-final` como projeto Maven.
2. Clique em **Load Maven Changes** (ou botão direito no `pom.xml` → **Maven → Reload project**).
3. Aguarde o download das dependências.

### 2. Credenciais do Banco

Copie o arquivo de exemplo e preencha com seus dados:

```
resources/config.properties.example  →  resources/config.properties
```

Conteúdo do `config.properties`:
```properties
db.url=jdbc:postgresql://SEU_HOST/Concessionaria?sslmode=require&channel_binding=require
db.user=SEU_USUARIO
db.password=SUA_SENHA
```

> ⚠️ O arquivo `config.properties` já está no `.gitignore` — **nunca commite ele**.

### 3. Ponto de Entrada

A classe principal agora é `view.Main`.

No IntelliJ:
> Run/Debug Configurations → Application → Main class: `view.Main`

---

## Estrutura de Arquivos

```
src/
├── dao/
│   ├── DatabaseConnection.java   ← Conexão com banco (lê config.properties)
│   ├── DaoCarro.java             ← Interface
│   ├── DaoCarroBD.java           ← Implementação (inclui loadDisponiveis())
│   ├── DaoFuncionario.java
│   ├── DaoUsuario.java           ← Login com BCrypt
│   ├── DaoVenda.java             ← Interface
│   └── DaoVendasBD.java          ← Transação: insere venda + marca carro como Vendido
├── entidade/
│   ├── Carro.java
│   ├── Funcionario.java          ← Getters corrigidos para padrão Java
│   ├── Usuario.java              ← Sem campo senha
│   └── Vendas.java
├── service/
│   ├── ComissaoService.java
│   └── PasswordUtil.java         ← Hash e verificação BCrypt
└── view/
    ├── Main.java                 ← Ponto de entrada (chama initialize() uma vez)
    ├── LoginFrame.java           ← Login com JPasswordField
    └── InterfaceGrafica.java     ← Dashboard com dados reais, venda com ComboBox

resources/
├── config.properties.example    ← Template de credenciais (versionado)
└── config.properties            ← Credenciais reais (NO .gitignore — NÃO versionar)
```

---

## Migração das Senhas Existentes

Se já existem usuários no banco com senhas em texto puro, é necessário fazer a migração.
Execute este trecho **uma única vez** para converter as senhas:

```java
// MigrarSenhas.java — execute uma vez e depois apague
DaoUsuario dao = new DaoUsuario();
// Para cada usuário, recrie com a senha atual hasheada:
// dao.criarUsuario("admin", "senha_atual_em_texto", "GERENTE", null);
// Vendedor precisa do id do funcionário:
// dao.criarUsuario("vendedor1", "senha123", "VENDEDOR", 1);
// Depois delete o usuário antigo com senha em texto puro.
```

Ou, mais simples: delete todos os usuários do banco e recadastre-os pelo sistema já com BCrypt.
