package dsy1103.bibliotecaam.taller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/*
------------------------------------------- AVISO DE UTILIDAD --------------------------------------------
* * * * * INICIALIZA EL MICRO SERVICIO DE EMPLEADO
* * * * * O NO PODRAS COMUNICARTE PARA CREAR O MODIFICAR
* * * * * TODA FUNCION QUE USE EL "idEmpleado"
 */
@SpringBootApplication
@EnableDiscoveryClient
public class TallerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TallerApplication.class, args);
	}

}
