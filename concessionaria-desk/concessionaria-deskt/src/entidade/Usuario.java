package entidade;

/**
 * Representa um usuário do sistema.
 *
 * SEGURANÇA: A senha NÃO é transportada neste objeto após o login.
 * O hash fica no banco; a verificação ocorre em DaoUsuario.autenticar().
 *
 * funcionarioId: obrigatório para VENDEDOR (vincula o login às vendas/comissões).
 * Para GERENTE pode ser null.
 */
public class Usuario {

    private int id;
    private String username;
    private String papel; // "GERENTE" ou "VENDEDOR"
    private Integer funcionarioId;

    public Usuario(int id, String username, String papel) {
        this(id, username, papel, null);
    }

    public Usuario(int id, String username, String papel, Integer funcionarioId) {
        this.id = id;
        this.username = username;
        this.papel = papel;
        this.funcionarioId = funcionarioId;
    }

    public int getId()                { return id; }
    public String getUsername()       { return username; }
    public String getPapel()          { return papel; }
    public Integer getFuncionarioId() { return funcionarioId; }
}
