package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entidade.Funcionario;

public class DaoFuncionario {

    public void save(Funcionario funcionario) throws SQLException {
        String sql = "INSERT INTO funcionarios (nome, salario) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, funcionario.getNome().trim());
            pstmt.setDouble(2, funcionario.getSalario());
            pstmt.executeUpdate();
        }
    }

    public List<Funcionario> loadAll() {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT * FROM funcionarios ORDER BY nome";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Funcionario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getDouble("salario")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar funcionários: " + e.getMessage());
        }

        return lista;
    }

    public Funcionario findById(int id) {
        String sql = "SELECT * FROM funcionarios WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Funcionario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("salario")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar funcionário: " + e.getMessage());
        }

        return null;
    }

    public void update(Funcionario funcionario) throws SQLException {
        String sql = "UPDATE funcionarios SET nome = ?, salario = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, funcionario.getNome().trim());
            pstmt.setDouble(2, funcionario.getSalario());
            pstmt.setInt(3, funcionario.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM funcionarios WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
