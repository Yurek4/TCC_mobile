import org.mindrot.jbcrypt.BCrypt;
public class GerarHash {
    public static void main(String[] args) {
        String hash = BCrypt.hashpw("1234", BCrypt.gensalt(12));
        System.out.println(hash);
    }
}
