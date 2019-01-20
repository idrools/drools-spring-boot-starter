package com.idrools.spring.boot;

import com.idrools.spring.boot.configuration.DroolsAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import(DroolsAutoConfiguration.class)
public class SpringBootStarterDroolsApplicationTests {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootStarterDroolsApplicationTests.class, args);
	}

}

