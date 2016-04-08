package be.ordina.ordineo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.retry.annotation.EnableRetry;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

/**
 * Created by gide on 08/04/16.
 */
@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
@EnableRetry
@EnableHypermediaSupport(type = HAL)
public class MilestoneCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MilestoneCoreApplication.class, args);
    }

}
