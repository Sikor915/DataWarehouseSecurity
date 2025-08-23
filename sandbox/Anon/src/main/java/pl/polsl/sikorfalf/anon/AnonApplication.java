package pl.polsl.sikorfalf.anon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.polsl.sikorfalf.config.HelloController;

@SpringBootApplication(scanBasePackageClasses = HelloController.class)
public class AnonApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnonApplication.class, args);
    }


}