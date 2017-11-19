package ru.oleg.rsoi.service.gateway;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class AppConfig {
    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public CommandLineRunner schedulingRunner(TaskExecutor executor, Queue queue) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
               executor.execute(queue);
            }
        };
    }

    @Bean
    @Scope("singleton")
    public Queue queue() {
        return new Queue();
    }
}
