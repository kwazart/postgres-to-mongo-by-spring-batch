package com.polozov.postgrestomongobyspringbatch.config;

import com.polozov.postgrestomongobyspringbatch.model.Task;
import com.polozov.postgrestomongobyspringbatch.model.TaskDetail;
import com.polozov.postgrestomongobyspringbatch.service.CleanUpService;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.batch.item.database.builder.HibernateCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;


@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CleanUpService cleanUpService;

    @Autowired
    ItemReader<Task> reader;

    @Autowired
    MongoItemWriter<TaskDetail> writer;

    @Autowired
    ItemProcessor<Task, TaskDetail> processor;

    @Bean
    public Job createJob() {
        return jobBuilderFactory.get("MyJob")
                .incrementer(new RunIdIncrementer())
                .flow(createStep()).end().build();
    }

    @Bean
    public Step createStep() {
        return stepBuilderFactory.get("MyStep")
                .<Task, TaskDetail> chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @StepScope
    @Bean
    public ItemReader<Task> reader() throws Exception {
        HibernateCursorItemReader<Task> reader = new HibernateCursorItemReaderBuilder<Task>()
                .name("cursorItemReader")
                .sessionFactory(this.sessionFactory)
                .fetchSize(2)
                .queryString("from Task")
                .build();
        reader.afterPropertiesSet();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        return reader;
    }

    @Bean
    public ItemProcessor<Task, TaskDetail> processor() {
        return item -> {
            TaskDetail taskDetail = new TaskDetail();
            taskDetail.setTaskName(item.getTaskName());
            taskDetail.setPriority(item.getPriority());
            taskDetail.setIsComplete(item.getIsComplete());
            taskDetail.setStartDateTime(item.getStartDateTime());
            taskDetail.setEndDateTime(item.getEndDateTime());
            return taskDetail;
        };
    }

    @StepScope
    @Bean
    public MongoItemWriter<TaskDetail> writer(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<TaskDetail>()
                .template(mongoTemplate)
                .collection("task")
                .build();
    }

    @Bean
    public MethodInvokingTaskletAdapter cleanUpTasklet() {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();

        adapter.setTargetObject(cleanUpService);
        adapter.setTargetMethod("cleanUp");

        return adapter;
    }

    @Bean
    public Step cleanUpStep() {
        return this.stepBuilderFactory.get("cleanUpStep")
                .tasklet(cleanUpTasklet())
                .build();
    }
}
