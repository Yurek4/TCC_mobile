package dao;

import java.sql.SQLException;
import java.util.List;

import entidade.Vendas;

public interface DaoVenda {
    void registrarVenda(Vendas venda) throws SQLException;
    List<Vendas> listarVendas();
    void update(Vendas venda) throws SQLException;
    void deleteById(int id) throws SQLException;
}
