package za.co.pp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PrettyParadiseAdminApplication {
    public static void main(String... args){
        SpringApplication.run(PrettyParadiseAdminApplication.class, args);
    }

}
