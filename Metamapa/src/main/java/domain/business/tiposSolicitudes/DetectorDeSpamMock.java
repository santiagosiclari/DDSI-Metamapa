package domain.business.tiposSolicitudes;

public class DetectorDeSpamMock implements DetectorDeSpam {
  @Override
  public boolean esSpam(String texto) {
    // Lógica simple para simular comportamiento
    return texto.length()>25;
  }
}