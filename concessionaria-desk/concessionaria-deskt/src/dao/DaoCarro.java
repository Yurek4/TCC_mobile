package dao;

import java.sql.SQLException;
import java.util.List;

import entidade.Carro;

public interface DaoCarro {
    void save(Carro carro) throws SQLException;
    List<Carro> loadAll();
    void update(Carro carro) throws SQLException;
    void deleteById(int id) throws SQLException;
}
