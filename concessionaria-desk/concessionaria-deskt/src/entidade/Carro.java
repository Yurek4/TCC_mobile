package entidade;

public class Carro {

    private int id;
    private String nome;
    private String modelo;
    private int ano;
    private double preco;
    private String status;
    private String nivel;

    public Carro() {}

    public Carro(int id, String nome, String modelo, int ano,
                 double preco, String status, String nivel) {
        this.id = id;
        this.nome = nome;
        this.modelo = modelo;
        this.ano = ano;
        this.preco = preco;
        this.status = status;
        this.nivel = nivel;
    }

    public int getId()          { return id; }
    public String getNome()     { return nome; }
    public String getModelo()   { return modelo; }
    public int getAno()         { return ano; }
    public double getPreco()    { return preco; }
    public String getStatus()   { return status; }
    public String getNivel()    { return nivel; }

    public void setId(int id)           { this.id = id; }
    public void setNome(String nome)    { this.nome = nome; }
    public void setModelo(String m)     { this.modelo = m; }
    public void setAno(int ano)         { this.ano = ano; }
    public void setPreco(double p)      { this.preco = p; }
    public void setStatus(String s)     { this.status = s; }
    public void setNivel(String n)      { this.nivel = n; }

    /** Exibição amigável no JComboBox de vendas */
    @Override
    public String toString() {
        return String.format("[%d] %s %s (%d) - R$ %.2f | %s",
            id, nome, modelo, ano, preco, nivel);
    }
}
