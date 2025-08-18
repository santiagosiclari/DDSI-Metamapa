package Metamapa.business.Solicitudes;

public interface DetectorDeSpam {
    static boolean esSpam(String texto) {
        // Lógica simple para simular comportamiento
        return texto.length()<25;
    }
}