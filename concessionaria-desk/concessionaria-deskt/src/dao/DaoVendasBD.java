package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entidade.Vendas;

public class DaoVendasBD implements DaoVenda {

    /**
     * Registra a venda e automaticamente marca o carro como "Vendido".
     * Ambas as operações ocorrem na mesma transação — ou as duas salvam, ou nenhuma.
     */
    @Override
    public void registrarVenda(Vendas venda) throws SQLException {
        String sqlVenda  = "INSERT INTO vendas (id_carro, id_funcionario, valor_venda, comissao, data_venda) VALUES (?,?,?,?,?)";
        String sqlStatus = "UPDATE carros SET status = 'Vendido' WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // inicia transação

            try (PreparedStatement pVenda = conn.prepareStatement(sqlVenda)) {
                pVenda.setInt(1, venda.getIdCarro());
                pVenda.setInt(2, venda.getIdFuncionario());
                pVenda.setDouble(3, venda.getValorvenda());
                pVenda.setDouble(4, venda.getComissao());
                pVenda.setDate(5, Date.valueOf(venda.getDataVenda()));
                pVenda.executeUpdate();
            }

            try (PreparedStatement pStatus = conn.prepareStatement(sqlStatus)) {
                pStatus.setInt(1, venda.getIdCarro());
                pStatus.executeUpdate();
            }

            conn.commit(); // confirma transação

        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // desfaz se algo falhar
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    @Override
    public List<Vendas> listarVendas() {
        List<Vendas> vendas = new ArrayList<>();
        String sql = "SELECT * FROM vendas ORDER BY data_venda DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                vendas.add(new Vendas(
                    rs.getInt("id"),
                    rs.getInt("id_carro"),
                    rs.getInt("id_funcionario"),
                    rs.getDouble("valor_venda"),
                    rs.getDouble("comissao"),
                    rs.getDate("data_venda").toLocalDate()
                ));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar vendas: " + e.getMessage());
        }

        return vendas;
    }

    @Override
    public void update(Vendas venda) throws SQLException {
        String sql = "UPDATE vendas SET id_carro=?, id_funcionario=?, valor_venda=?, comissao=?, data_venda=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, venda.getIdCarro());
            pstmt.setInt(2, venda.getIdFuncionario());
            pstmt.setDouble(3, venda.getValorvenda());
            pstmt.setDouble(4, venda.getComissao());
            pstmt.setDate(5, Date.valueOf(venda.getDataVenda()));
            pstmt.setInt(6, venda.getIdVenda());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM vendas WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
