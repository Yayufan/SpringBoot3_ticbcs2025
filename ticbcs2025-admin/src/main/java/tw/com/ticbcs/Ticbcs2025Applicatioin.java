package tw.com.ticbcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan("tw.com.ticbcs")
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class Ticbcs2025Applicatioin {
	public static void main(String[] args) {
		SpringApplication.run(Ticbcs2025Applicatioin.class, args);
	}
}
