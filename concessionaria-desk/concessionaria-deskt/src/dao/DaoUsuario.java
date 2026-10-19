package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import entidade.Usuario;
import service.PasswordUtil;

/**
 * SEGURANÇA:
 * - Senhas são armazenadas como hash BCrypt, nunca em texto puro.
 * - O login busca apenas pelo username e verifica o hash localmente,
 *   jamais comparando senhas em SQL.
 */
public class DaoUsuario {

    /**
     * Tenta autenticar. Retorna o Usuario se credenciais corretas, null caso contrário.
     * A senha NÃO é enviada ao banco — verificamos o hash localmente com BCrypt.
     */
    public Usuario autenticar(String username, String senhaPura) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashArmazenado = rs.getString("senha");
                    if (PasswordUtil.verificarSenha(senhaPura, hashArmazenado)) {
                        return mapRow(rs);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao autenticar usuário: " + e.getMessage());
        }

        return null;
    }

    /**
     * Cria um novo usuário fazendo hash da senha antes de salvar.
     * @param funcionarioId vínculo com funcionarios.id (obrigatório para VENDEDOR no mobile)
     * @throws SQLException se o username já existir (constraint UNIQUE) ou outro erro de banco.
     */
    public void criarUsuario(String username, String senhaPura, String papel, Integer funcionarioId)
            throws SQLException {
        String hashSenha = PasswordUtil.hashSenha(senhaPura);
        String sql = "INSERT INTO usuarios (username, senha, papel, funcionario_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, hashSenha);
            pstmt.setString(3, papel);
            setFuncionarioId(pstmt, 4, funcionarioId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Atualiza um usuário. Se a nova senha não for vazia, faz hash; caso contrário mantém a atual.
     */
    public void update(Usuario usuario, String novaSenhaPura) throws SQLException {
        if (novaSenhaPura != null && !novaSenhaPura.trim().isEmpty()) {
            String sql = "UPDATE usuarios SET username = ?, senha = ?, papel = ?, funcionario_id = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, usuario.getUsername().trim());
                pstmt.setString(2, PasswordUtil.hashSenha(novaSenhaPura));
                pstmt.setString(3, usuario.getPapel());
                setFuncionarioId(pstmt, 4, usuario.getFuncionarioId());
                pstmt.setInt(5, usuario.getId());
                pstmt.executeUpdate();
            }
        } else {
            String sql = "UPDATE usuarios SET username = ?, papel = ?, funcionario_id = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, usuario.getUsername().trim());
                pstmt.setString(2, usuario.getPapel());
                setFuncionarioId(pstmt, 3, usuario.getFuncionarioId());
                pstmt.setInt(4, usuario.getId());
                pstmt.executeUpdate();
            }
        }
    }

    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
        }

        return usuarios;
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private void setFuncionarioId(PreparedStatement pstmt, int index, Integer funcionarioId)
            throws SQLException {
        if (funcionarioId == null) {
            pstmt.setNull(index, Types.INTEGER);
        } else {
            pstmt.setInt(index, funcionarioId);
        }
    }

    // Não inclui a senha no objeto Usuario — ela não deve circular pela aplicação
    private Usuario mapRow(ResultSet rs) throws SQLException {
        Integer funcionarioId = null;
        try {
            int fid = rs.getInt("funcionario_id");
            if (!rs.wasNull()) funcionarioId = fid;
        } catch (SQLException ignored) {
            // Coluna ainda não existe em bancos muito antigos (antes do migrate)
        }

        return new Usuario(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("papel"),
            funcionarioId
        );
    }
}
