package view;

import dao.DaoCarroBD;
import dao.DaoFuncionario;
import dao.DaoVenda;
import entidade.Carro;
import entidade.Funcionario;
import entidade.Vendas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Painel de relatórios profissional.
 * Exibe resumo financeiro, ranking de vendedores e histórico filtrado por período.
 */
public class RelatorioPanel extends JPanel {

    // ── Cores do tema ──────────────────────────────────────────────────
    private static final Color FUNDO       = new Color(12, 20, 40);
    private static final Color CARD_BG     = new Color(18, 35, 70);
    private static final Color CARD_BORDA  = new Color(30, 60, 120);
    private static final Color AZUL        = new Color(0, 122, 204);
    private static final Color VERDE       = new Color(39, 174, 96);
    private static final Color AMARELO     = new Color(241, 196, 15);
    private static final Color BRANCO      = Color.WHITE;
    private static final Color CINZA_TEXTO = new Color(160, 190, 230);

    // ── Dependências ───────────────────────────────────────────────────
    private final DaoVenda        vendasDAO;
    private final DaoFuncionario  funcDAO;
    private final DaoCarroBD      carroDAO;

    // ── Componentes dinâmicos ──────────────────────────────────────────
    private JLabel lblTotalVendas, lblReceitaTotal, lblTicketMedio, lblMelhorMes;
    private final DefaultTableModel dtmRanking  = new DefaultTableModel(
            new Object[]{"#", "Vendedor", "Vendas", "Comissão Total"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel dtmHistorico = new DefaultTableModel(
            new Object[]{"Data", "Carro", "Vendedor", "Valor", "Comissão"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private JComboBox<String> comboPeriodo;
    private JTextField txtBusca;

    // ── Formato monetário ─────────────────────────────────────────────
    private static final NumberFormat FMT_BRL =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // ─────────────────────────────────────────────────────────────────
    public RelatorioPanel(DaoVenda vendasDAO, DaoFuncionario funcDAO, DaoCarroBD carroDAO) {
        this.vendasDAO = vendasDAO;
        this.funcDAO   = funcDAO;
        this.carroDAO  = carroDAO;

        setBackground(FUNDO);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(criarCabecalho(),  BorderLayout.NORTH);
        add(criarCorpo(),      BorderLayout.CENTER);

        // Carrega ao abrir
        SwingUtilities.invokeLater(this::atualizar);
    }

    // ── Cabeçalho ────────────────────────────────────────────────────
    private JPanel criarCabecalho() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setBackground(FUNDO);
        cab.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel titulo = new JLabel("Relatório de Desempenho");
        titulo.setForeground(BRANCO);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controles.setBackground(FUNDO);

        comboPeriodo = new JComboBox<>(new String[]{
                "Todos os períodos", "Último mês", "Últimos 3 meses", "Último ano"
        });
        estilizarCombo(comboPeriodo);

        JButton btnAtualizar = criarBotao("Atualizar", AZUL, null);
        btnAtualizar.addActionListener(e -> atualizar());

        JButton btnExportar = criarBotao("Exportar CSV", new Color(39, 120, 70), null);
        btnExportar.addActionListener(e -> exportarCSV());

        controles.add(new JLabel("<html><font color='#90AEDD'>Período:</font></html>"));
        controles.add(comboPeriodo);
        controles.add(btnAtualizar);
        controles.add(btnExportar);

        cab.add(titulo,    BorderLayout.WEST);
        cab.add(controles, BorderLayout.EAST);
        return cab;
    }

    // ── Corpo principal ────────────────────────────────────────────────
    private JSplitPane criarCorpo() {
        JPanel superior = new JPanel(new BorderLayout(0, 12));
        superior.setBackground(FUNDO);
        superior.add(criarCardsResumo(), BorderLayout.NORTH);
        superior.add(criarPainelRanking(), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, superior, criarPainelHistorico());
        split.setDividerLocation(340);
        split.setDividerSize(4);
        split.setBackground(FUNDO);
        split.setBorder(null);
        split.setResizeWeight(0.45);
        return split;
    }

    // ── Cards de resumo ────────────────────────────────────────────────
    private JPanel criarCardsResumo() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 0));
        cards.setBackground(FUNDO);

        lblTotalVendas  = new JLabel("—");
        lblReceitaTotal = new JLabel("—");
        lblTicketMedio  = new JLabel("—");
        lblMelhorMes    = new JLabel("—");

        cards.add(criarCard("Total de Vendas", lblTotalVendas, AZUL));
        cards.add(criarCard("Receita Total", lblReceitaTotal, VERDE));
        cards.add(criarCard("Ticket Médio", lblTicketMedio, AMARELO));
        cards.add(criarCard("Melhor Mês", lblMelhorMes, new Color(155, 89, 182)));
        return cards;
    }

    private JPanel criarCard(String titulo, JLabel valorLabel, Color cor) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(0, 100));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cor.darker(), 1),
                new EmptyBorder(12, 14, 12, 14)
        ));
        card.setLayout(new BorderLayout(0, 6));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.LEFT);
        lblTitulo.setForeground(CINZA_TEXTO);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        valorLabel.setForeground(BRANCO);
        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valorLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Barra colorida no topo
        JPanel barra = new JPanel();
        barra.setBackground(cor);
        barra.setPreferredSize(new Dimension(0, 3));

        card.add(barra,     BorderLayout.NORTH);
        card.add(lblTitulo, BorderLayout.CENTER);
        card.add(valorLabel, BorderLayout.SOUTH);
        return card;
    }

    // ── Ranking de vendedores ─────────────────────────────────────────
    private JPanel criarPainelRanking() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(FUNDO);
        painel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel cab = new JLabel("Ranking de Vendedores", SwingConstants.LEFT);
        cab.setForeground(BRANCO);
        cab.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cab.setBorder(new EmptyBorder(0, 0, 8, 0));

        JTable tabela = new JTable(dtmRanking);
        estilizarTabela(tabela);
        configurarColuna(tabela, 2, SwingConstants.CENTER);
        configurarColunaMoeda(tabela, 3);

        // Destaque para 1º lugar
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBackground(sel ? AZUL : (row == 0 ? new Color(25, 55, 100) : new Color(18, 30, 55)));
                setForeground(row == 0 ? AMARELO : BRANCO);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                setHorizontalAlignment(col == 0 || col == 2 ? SwingConstants.CENTER : SwingConstants.LEFT);
                if (col == 0 && v != null) setText(String.valueOf(v));
                if (col == 3 && v instanceof Number)
                    setText(FMT_BRL.format(((Number) v).doubleValue()));
                return this;
            }
        });

        painel.add(cab, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    // ── Histórico de vendas ────────────────────────────────────────────
    private JPanel criarPainelHistorico() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(FUNDO);
        painel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel cabHistorico = new JPanel(new BorderLayout());
        cabHistorico.setBackground(FUNDO);
        cabHistorico.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel lblHistorico = new JLabel("Histórico Detalhado", SwingConstants.LEFT);
        lblHistorico.setForeground(BRANCO);
        lblHistorico.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Barra de busca
        JPanel barraBusca = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        barraBusca.setBackground(FUNDO);
        txtBusca = new JTextField(16);
        txtBusca.setBackground(CARD_BG);
        txtBusca.setForeground(BRANCO);
        txtBusca.setCaretColor(BRANCO);
        txtBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDA),
                new EmptyBorder(3, 6, 3, 6)));
        txtBusca.setToolTipText("Buscar por carro ou vendedor…");
        txtBusca.addActionListener(e -> filtrarHistorico());

        JButton btnBuscar = criarBotao("Buscar", AZUL, null);
        btnBuscar.setPreferredSize(new Dimension(70, 26));
        btnBuscar.addActionListener(e -> filtrarHistorico());

        barraBusca.add(new JLabel("<html><font color='#90AEDD'>Buscar:</font></html>"));
        barraBusca.add(txtBusca);
        barraBusca.add(btnBuscar);

        cabHistorico.add(lblHistorico, BorderLayout.WEST);
        cabHistorico.add(barraBusca,  BorderLayout.EAST);

        JTable tabela = new JTable(dtmHistorico);
        estilizarTabela(tabela);
        configurarColunaMoeda(tabela, 3);
        configurarColunaMoeda(tabela, 4);

        painel.add(cabHistorico,          BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    // ── Lógica de atualização ─────────────────────────────────────────
    public void atualizar() {
        List<Vendas>     todas  = vendasDAO.listarVendas();
        List<Funcionario> funcs  = funcDAO.loadAll();
        List<Carro>       carros = carroDAO.loadAll();

        // Filtro por período
        LocalDate inicio = calcularInicio();
        List<Vendas> filtradas = todas.stream()
                .filter(v -> inicio == null || !v.getDataVenda().isBefore(inicio))
                .collect(Collectors.toList());

        atualizarCards(filtradas);
        atualizarRanking(filtradas, funcs);
        preencherHistorico(filtradas, funcs, carros);
    }

    private LocalDate calcularInicio() {
        String sel = (String) comboPeriodo.getSelectedItem();
        if (sel == null) return null;
        return switch (sel) {
            case "Último mês"      -> LocalDate.now().minusMonths(1);
            case "Últimos 3 meses" -> LocalDate.now().minusMonths(3);
            case "Último ano"      -> LocalDate.now().minusYears(1);
            default                -> null;
        };
    }

    private void atualizarCards(List<Vendas> vendas) {
        int total = vendas.size();
        double receita = vendas.stream().mapToDouble(Vendas::getValorvenda).sum();
        double ticket  = total == 0 ? 0 : receita / total;

        // Melhor mês
        Map<String, Long> porMes = vendas.stream().collect(
                Collectors.groupingBy(v ->
                        v.getDataVenda().format(DateTimeFormatter.ofPattern("MMM/yy", new Locale("pt", "BR"))),
                        Collectors.counting()));
        String melhorMes = porMes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " (" + e.getValue() + ")")
                .orElse("—");

        lblTotalVendas.setText(String.valueOf(total));
        lblReceitaTotal.setText(total == 0 ? "R$ 0,00" : FMT_BRL.format(receita));
        lblTicketMedio.setText(total == 0 ? "R$ 0,00" : FMT_BRL.format(ticket));
        lblMelhorMes.setText(melhorMes);
    }

    private void atualizarRanking(List<Vendas> vendas, List<Funcionario> funcs) {
        // Agrupa por funcionário
        Map<Integer, Long>   qtdPorFunc     = vendas.stream().collect(
                Collectors.groupingBy(Vendas::getIdFuncionario, Collectors.counting()));
        Map<Integer, Double> comissaoPorFunc = vendas.stream().collect(
                Collectors.groupingBy(Vendas::getIdFuncionario, Collectors.summingDouble(Vendas::getComissao)));

        // Ordena por quantidade de vendas
        List<Funcionario> ordenados = funcs.stream()
                .filter(f -> qtdPorFunc.containsKey(f.getId()))
                .sorted((a, b) -> Long.compare(
                        qtdPorFunc.getOrDefault(b.getId(), 0L),
                        qtdPorFunc.getOrDefault(a.getId(), 0L)))
                .collect(Collectors.toList());

        dtmRanking.setRowCount(0);
        int pos = 1;
        for (Funcionario f : ordenados) {
            dtmRanking.addRow(new Object[]{
                    pos,
                    f.getNome(),
                    qtdPorFunc.getOrDefault(f.getId(), 0L),
                    comissaoPorFunc.getOrDefault(f.getId(), 0.0)
            });
            pos++;
        }
    }

    private void preencherHistorico(List<Vendas> vendas, List<Funcionario> funcs, List<Carro> carros) {
        Map<Integer, String> nomeFunc  = funcs.stream().collect(
                Collectors.toMap(Funcionario::getId, Funcionario::getNome, (a, b) -> a));
        Map<Integer, String> nomeCarro = carros.stream().collect(
                Collectors.toMap(Carro::getId, c -> c.getNome() + " " + c.getModelo(), (a, b) -> a));

        dtmHistorico.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Vendas v : vendas) {
            dtmHistorico.addRow(new Object[]{
                    v.getDataVenda().format(fmt),
                    nomeCarro.getOrDefault(v.getIdCarro(), "ID " + v.getIdCarro()),
                    nomeFunc.getOrDefault(v.getIdFuncionario(), "ID " + v.getIdFuncionario()),
                    v.getValorvenda(),
                    v.getComissao()
            });
        }
    }

    private void filtrarHistorico() {
        String busca = txtBusca.getText().trim().toLowerCase();
        if (busca.isEmpty()) {
            atualizar();
            return;
        }
        // Filtra nas linhas existentes da tabela
        for (int i = dtmHistorico.getRowCount() - 1; i >= 0; i--) {
            String carro    = String.valueOf(dtmHistorico.getValueAt(i, 1)).toLowerCase();
            String vendedor = String.valueOf(dtmHistorico.getValueAt(i, 2)).toLowerCase();
            if (!carro.contains(busca) && !vendedor.contains(busca))
                dtmHistorico.removeRow(i);
        }
    }

    /** Exporta o histórico para CSV, deixando o usuário escolher onde salvar. */
    private void exportarCSV() {
        StringBuilder sb = new StringBuilder("Data;Carro;Vendedor;Valor;Comissão\n");
        for (int i = 0; i < dtmHistorico.getRowCount(); i++) {
            Object valor = dtmHistorico.getValueAt(i, 3);
            Object comissao = dtmHistorico.getValueAt(i, 4);
            sb.append(csv(dtmHistorico.getValueAt(i, 0))).append(";")
                    .append(csv(dtmHistorico.getValueAt(i, 1))).append(";")
                    .append(csv(dtmHistorico.getValueAt(i, 2))).append(";")
                    .append(csv(valor instanceof Number n ? FMT_BRL.format(n.doubleValue()) : valor)).append(";")
                    .append(csv(comissao instanceof Number n ? FMT_BRL.format(n.doubleValue()) : comissao)).append("\n");
        }

        javax.swing.JFileChooser seletor = new javax.swing.JFileChooser();
        seletor.setDialogTitle("Salvar relatório como...");
        seletor.setSelectedFile(new java.io.File("relatorio_vendas.csv"));
        seletor.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivo CSV (*.csv)", "csv"));

        int escolha = seletor.showSaveDialog(this);
        if (escolha != javax.swing.JFileChooser.APPROVE_OPTION) return;

        java.io.File destino = seletor.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".csv")) {
            destino = new java.io.File(destino.getParentFile(), destino.getName() + ".csv");
        }

        try (java.io.FileWriter fw = new java.io.FileWriter(destino)) {
            fw.write(sb.toString());
            JOptionPane.showMessageDialog(this,
                    "Relatório exportado para:\n" + destino.getAbsolutePath(),
                    "Exportação concluída", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao exportar: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String csv(Object v) {
        String s = v == null ? "" : String.valueOf(v);
        if (s.contains(";") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    // ── Helpers de estilo ─────────────────────────────────────────────
    private void estilizarTabela(JTable t) {
        t.setBackground(new Color(18, 30, 55));
        t.setForeground(BRANCO);
        t.setRowHeight(28);
        t.setGridColor(CARD_BORDA);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(AZUL);
        t.setSelectionForeground(BRANCO);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.getTableHeader().setBackground(new Color(10, 20, 40));
        t.getTableHeader().setForeground(CINZA_TEXTO);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDA));
        t.setAutoCreateRowSorter(true);
    }

    private void configurarColunaMoeda(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new DefaultTableCellRenderer() {
            @Override protected void setValue(Object v) {
                setText(v instanceof Number ? FMT_BRL.format(((Number) v).doubleValue())
                        : v == null ? "" : v.toString());
                setForeground(new Color(100, 220, 140));
                setBackground(new Color(18, 30, 55));
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
        });
    }

    private void configurarColuna(JTable t, int col, int alinhamento) {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(alinhamento);
        t.getColumnModel().getColumn(col).setCellRenderer(r);
    }

    private JButton criarBotao(String texto, Color cor, Icon icone) {
        JButton b = new JButton(texto, icone);
        b.setBackground(cor);
        b.setForeground(BRANCO);
        b.setFocusPainted(false);
        b.setIconTextGap(6);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setBorder(new EmptyBorder(5, 12, 5, 12));
        return b;
    }

    private void estilizarCombo(JComboBox<String> c) {
        c.setBackground(CARD_BG);
        c.setForeground(BRANCO);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        c.setBorder(BorderFactory.createLineBorder(CARD_BORDA));
    }
}
