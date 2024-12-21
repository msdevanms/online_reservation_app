package com.cbt.eurekaserverboot3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class Eurekaserverboot3Application
{

    public static void main(String[] args)
    {
        SpringApplication application = new SpringApplication(Eurekaserverboot3Application.class);

        // Set the active profile programmatically
        application.setAdditionalProfiles("dev");

        application.run(args);
    }

}
