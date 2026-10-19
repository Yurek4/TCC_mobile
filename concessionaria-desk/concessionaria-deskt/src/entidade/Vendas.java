package entidade;

import java.time.LocalDate;

public class Vendas {

    private int id;
    private int idCarro;
    private int idFuncionario;
    private double valorVenda;
    private double comissao;
    private LocalDate dataVenda;

    public Vendas(int id, int idCarro, int idFuncionario,
                  double valorVenda, double comissao, LocalDate dataVenda) {
        this.id = id;
        this.idCarro = idCarro;
        this.idFuncionario = idFuncionario;
        this.valorVenda = valorVenda;
        this.comissao = comissao;
        this.dataVenda = dataVenda;
    }

    public int getIdVenda()          { return id; }
    public int getIdCarro()          { return idCarro; }
    public int getIdFuncionario()    { return idFuncionario; }
    public double getValorvenda()    { return valorVenda; }
    public double getComissao()      { return comissao; }
    public LocalDate getDataVenda()  { return dataVenda; }
}
