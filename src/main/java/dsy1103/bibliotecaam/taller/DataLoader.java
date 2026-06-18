package dsy1103.bibliotecaam.taller;

import dsy1103.bibliotecaam.taller.model.Taller;
import dsy1103.bibliotecaam.taller.repository.TallerRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private TallerRepository tallerRepository;

    @Override
    public void run(String... args) throws Exception{
        Faker faker = new Faker();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            Taller taller = new Taller();
            taller.setNombreTaller(faker.lorem().sentence());
            taller.setFechaTaller((faker.timeAndDate().birthday()));
            taller.setIdEmpleado((long)faker.number().numberBetween(1,3));

            tallerRepository.save(taller);
        }
    }
}
