package liquibaseTestProject;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@EnableAsync
@Configuration
public class AsyncEventsConfig {
	
	
	@Bean(name = "threadPoolTaskExecutorLiquibase")
	public ThreadPoolTaskExecutor eventsThreadPoolTaskExecutorLiquibase() throws IOException {			
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(Integer.MAX_VALUE);
		executor.setQueueCapacity(Integer.MAX_VALUE);
		executor.initialize();
		return executor;
	}}
