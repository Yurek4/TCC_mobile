package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entidade.Carro;

public class DaoCarroBD implements DaoCarro {

    @Override
    public void save(Carro carro) throws SQLException {
        String sql = "INSERT INTO carros (nome, modelo, ano, preco, status, nivel) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, carro.getNome().trim());
            pstmt.setString(2, carro.getModelo().trim());
            pstmt.setInt(3, carro.getAno());
            pstmt.setDouble(4, carro.getPreco());
            pstmt.setString(5, carro.getStatus());
            pstmt.setString(6, carro.getNivel());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Carro> loadAll() {
        List<Carro> carros = new ArrayList<>();
        String sql = "SELECT * FROM carros ORDER BY nome";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                carros.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar carros: " + e.getMessage());
        }

        return carros;
    }

    /** Retorna apenas carros com status "Disponível", para uso no cadastro de vendas. */
    public List<Carro> loadDisponiveis() {
        List<Carro> carros = new ArrayList<>();
        String sql = "SELECT * FROM carros WHERE status = 'Disponível' ORDER BY nome";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                carros.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar carros disponíveis: " + e.getMessage());
        }

        return carros;
    }

    @Override
    public void update(Carro carro) throws SQLException {
        String sql = "UPDATE carros SET nome = ?, modelo = ?, ano = ?, preco = ?, status = ?, nivel = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, carro.getNome().trim());
            pstmt.setString(2, carro.getModelo().trim());
            pstmt.setInt(3, carro.getAno());
            pstmt.setDouble(4, carro.getPreco());
            pstmt.setString(5, carro.getStatus());
            pstmt.setString(6, carro.getNivel());
            pstmt.setInt(7, carro.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM carros WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private Carro mapRow(ResultSet rs) throws SQLException {
        return new Carro(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("modelo"),
            rs.getInt("ano"),
            rs.getDouble("preco"),
            rs.getString("status"),
            rs.getString("nivel")
        );
    }
}
