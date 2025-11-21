package fr.rakambda.rsndiscord.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "fr.rakambda.rsndiscord.spring")
@EnableJpaRepositories("fr.rakambda.rsndiscord.spring.storage")
@EntityScan("fr.rakambda.rsndiscord.spring.storage")
public class Application{
	public static void main(String[] args){
		SpringApplication.run(Application.class, args);
	}
}
