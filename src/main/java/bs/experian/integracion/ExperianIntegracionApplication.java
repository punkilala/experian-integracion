package bs.experian.integracion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ExperianIntegracionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExperianIntegracionApplication.class, args);
	}

}
