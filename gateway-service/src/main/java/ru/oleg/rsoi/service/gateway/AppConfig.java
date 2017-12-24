package ru.oleg.rsoi.service.gateway;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import ru.oleg.rsoi.service.gateway.Statistics.Statistics;

@Configuration
public class AppConfig {
    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public CommandLineRunner schedulingRunner(TaskExecutor executor, @Qualifier("retryQueue") Queue queue) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
               executor.execute(queue);
            }
        };
    }

    @Bean
    public CommandLineRunner schedulingStatRunner(TaskExecutor executor, @Qualifier("statQueue") Queue queue) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                executor.execute(queue);
            }
        };
    }


    @Bean(name = "retryQueue")
    @Scope("singleton")
    public Queue queue() {
        return new Queue();
    }

    @Bean(name = "statQueue")
    @Scope("singleton")
    public Queue statQueue() {
        return new Queue();
    }

    @Scope("singleton")
    public Statistics statistics() {
        return new Statistics();
    }
}
