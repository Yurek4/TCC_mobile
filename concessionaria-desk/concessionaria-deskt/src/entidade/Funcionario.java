package entidade;

public class Funcionario {

    private int id;
    private String nome;
    private double salario;

    public Funcionario(int id, String nome, double salario) {
        this.id = id;
        this.nome = nome;
        this.salario = salario;
    }

    // Convenção Java: getters com letra maiúscula
    public int getId()          { return id; }
    public String getNome()     { return nome; }
    public double getSalario()  { return salario; }

    /** Exibição amigável no JComboBox */
    @Override
    public String toString() {
        return String.format("[%d] %s", id, nome);
    }
}
