package domain.business.tiposSolicitudes;

public interface DetectorDeSpam {
    static static boolean esSpam(String texto) {
        // Lógica simple para simular comportamiento
        return texto.length()>25;
}
