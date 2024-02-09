package liquibaseTestProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages={"liquibaseTestProject"})
public class LiquibaseTestApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
        SpringApplication.run(LiquibaseTestApplication.class, args);        
    }
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return super.configure(builder);
	}
 }