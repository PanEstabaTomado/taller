package dsy1103.bibliotecaam.taller.config;

import dsy1103.bibliotecaam.taller.model.Taller;
import dsy1103.bibliotecaam.taller.repository.TallerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TallerRepository tallerRepository;

    @Override
    public void run(String... args) {
        if (tallerRepository.count() > 0) {
            log.info(">>> Sanciones ya cargadas. Se omite inicialización.");
            return;
        }
        log.info(">>> Cargando mascotas iniciales...");
        tallerRepository.save(new Taller(null, "Taller De Escritura y Gramatica", LocalDate.of(2010,05,10)));
        tallerRepository.save(new Taller(null, "Taller De Literatura y Escritura", LocalDate.of(2010,04,10)));
        tallerRepository.save(new Taller(null, "Taller De Lectura y Filosofia", LocalDate.of(2010,03,10)));
        log.info(">>> 3 Sanciones cargadas OK.");
    }
}
