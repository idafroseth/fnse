package no.mil.fnse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContext ctx = SpringApplication.run(Application.class);
	}

}
