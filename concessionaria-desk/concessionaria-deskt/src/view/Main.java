package view;

import javax.swing.SwingUtilities;
import dao.DatabaseConnection;

public class Main {
 
    public static void main(String[] args) {
        // 1. Inicializa o schema do banco UMA ÚNICA VEZ na subida da aplicação
        try {
            DatabaseConnection.initialize();
        } catch (Exception e) {
            System.err.println("Falha ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // 2. Abre a tela de login na thread de UI do Swing
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
