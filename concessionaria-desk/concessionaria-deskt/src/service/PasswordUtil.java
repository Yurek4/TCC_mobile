package service;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitário para hash e verificação de senhas com BCrypt.
 *
 * DEPENDÊNCIA Maven (adicionar ao pom.xml):
 *   <dependency>
 *       <groupId>org.mindrot</groupId>
 *       <artifactId>jbcrypt</artifactId>
 *       <version>0.4</version>
 *   </dependency>
 *
 * Para projetos sem Maven, baixe jbcrypt-0.4.jar e adicione ao Build Path.
 */
public class PasswordUtil {

    // Fator de custo do BCrypt (10-12 é o recomendado para produção)
    private static final int CUSTO = 12;

    /** Gera o hash BCrypt de uma senha em texto puro. Nunca armazene a senha pura. */
    public static String hashSenha(String senhaPura) {
        return BCrypt.hashpw(senhaPura, BCrypt.gensalt(CUSTO));
    }

    /**
     * Verifica se uma senha digitada corresponde ao hash armazenado.
     * @param senhaPura  senha digitada pelo usuário
     * @param hashArmazenado hash BCrypt salvo no banco
     */
    public static boolean verificarSenha(String senhaPura, String hashArmazenado) {
        if (senhaPura == null || hashArmazenado == null) return false;
        try {
            return BCrypt.checkpw(senhaPura, hashArmazenado);
        } catch (Exception e) {
            // hashArmazenado malformado — não deixa vazar exception
            return false;
        }
    }
}
