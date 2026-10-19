package service;

public class ComissaoService {

    /**
     * Calcula a comissão com base no nível do carro.
     *   Básico → 2%
     *   Médio  → 5%
     *   Luxo   → 8%
     */
    public static double calcularComissao(double preco, String nivelCarro) {
        if (nivelCarro == null) return 0;

        return switch (nivelCarro.toLowerCase()) {
            case "basico", "básico" -> preco * 0.02;
            case "medio",  "médio"  -> preco * 0.05;
            case "luxo"             -> preco * 0.08;
            default -> 0;
        };
    }
}
