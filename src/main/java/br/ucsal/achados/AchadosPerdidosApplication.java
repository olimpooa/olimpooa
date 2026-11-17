package br.ucsal.achados;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AchadosPerdidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(AchadosPerdidosApplication.class, args);
	}

}
