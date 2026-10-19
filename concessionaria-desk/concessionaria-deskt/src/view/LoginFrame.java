package view;

import java.awt.*;
import javax.swing.*;

import dao.DaoCarroBD;
import dao.DaoUsuario;
import entidade.Usuario;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtSenha; // JPasswordField — senha não fica visível
    private final DaoUsuario daoUsuario = new DaoUsuario();

    public LoginFrame() {
        Color azulFundo  = new Color(10, 20, 40);
        Color azulBotao  = new Color(0, 122, 204);
        Color branco     = Color.WHITE;

        setTitle("Automanager — Login");
        setSize(400, 270);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(azulFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(painel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titulo = new JLabel("Automanager", JLabel.CENTER);
        titulo.setForeground(branco);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        painel.add(titulo, gbc);

        gbc.gridwidth = 1;

        // Usuário
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0;
        JLabel lblUsuario = new JLabel("Usuário:");
        lblUsuario.setForeground(branco);
        painel.add(lblUsuario, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        txtUsuario = new JTextField(15);
        painel.add(txtUsuario, gbc);

        // Senha
        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0;
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setForeground(branco);
        painel.add(lblSenha, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        txtSenha = new JPasswordField(15);
        painel.add(txtSenha, gbc);

        // Botão entrar
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2; gbc.weightx = 0;
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBackground(azulBotao);
        btnEntrar.setForeground(branco);
        btnEntrar.setPreferredSize(new Dimension(120, 38));
        btnEntrar.setFocusPainted(false);
        painel.add(btnEntrar, gbc);

        // Enter no campo senha também faz login
        txtSenha.addActionListener(e -> fazerLogin());
        btnEntrar.addActionListener(e -> fazerLogin());
    }

    private void fazerLogin() {
        String usuario = txtUsuario.getText().trim();
        String senha   = new String(txtSenha.getPassword());

        if (usuario.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha usuário e senha.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // autenticar() verifica BCrypt — a senha pura nunca vai ao banco
        Usuario u = daoUsuario.autenticar(usuario, senha);

        // Limpa o campo de senha imediatamente após a tentativa
        txtSenha.setText("");

        if (u == null) {
            JOptionPane.showMessageDialog(this,
                "Usuário ou senha inválidos.",
                "Falha no login", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!"GERENTE".equalsIgnoreCase(u.getPapel())) {
            JOptionPane.showMessageDialog(this,
                "Apenas usuários com papel GERENTE podem acessar o sistema desktop.",
                "Acesso negado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        abrirTelaPrincipal(u);
    }

    private void abrirTelaPrincipal(Usuario u) {
        dispose();
        InterfaceGrafica tela = new InterfaceGrafica(new DaoCarroBD(), u);
        tela.setLocationRelativeTo(null);
        tela.setVisible(true);
    }
}
