package com.jio.lbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class McsApplication {

    public static void main(String[] args) {
        String log4jConfigFile = System.getProperty("app.config")
                + File.separator + "log_staticmapgen.xml";
    	if(new File(log4jConfigFile).exists()) {
			DOMConfigurator.configure(log4jConfigFile);
	        SpringApplication.run(McsApplication.class, args);
    	}else {
    		System.out.println("Logging properties file not exists can't start server...");
    	}
    }
}
