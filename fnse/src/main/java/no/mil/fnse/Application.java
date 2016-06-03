package no.mil.fnse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import no.mil.fnse.controller.SystemController;


@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties
public class Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContext ctx = SpringApplication.run(Application.class);
		SystemController controller = (SystemController) ctx.getBean("systemController");
		controller.start();
		
	}

}
