package Agregador.persistencia;
import Agregador.business.Consenso.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsensoSeeder implements CommandLineRunner {
  private final RepositorioConsenso consensoRepository;

  @Override
  public void run(String... args) {
    if (consensoRepository.count() == 0) {
      consensoRepository.save(new MayoriaSimple());
      consensoRepository.save(new Absoluto());
      consensoRepository.save(new MultiplesMenciones());
    }
  }
}