package com.github.bruce_mig.spring_batch_aws_telemetry.job;

import com.github.bruce_mig.spring_batch_aws_telemetry.domain.SalesInfo;
import com.github.bruce_mig.spring_batch_aws_telemetry.dto.SalesDTO;
import com.github.bruce_mig.spring_batch_aws_telemetry.mapper.SalesMapper;
import com.github.bruce_mig.spring_batch_aws_telemetry.task.DownloadFileTask;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.builder.TaskletStepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ImportSalesInfoJob {

    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SalesMapper salesMapper;
    private final DownloadFileTask downloadFileTask;

    @Bean
    public Job syncSalesJob(Step downloadFileStep, Step fromFileDownloadedToDb){
        return new JobBuilder("sync-sales-job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(downloadFileStep)
                .next(fromFileDownloadedToDb)
                .end()
                .build();

    }

    @Bean
    public Step downloadFileStep(){
        return new TaskletStepBuilder(new StepBuilder("downloadFileStep", jobRepository))
                .tasklet(downloadFileTask, transactionManager)
                .build();
    }

    @Bean
    public Step fromFileDownloadedToDb(FlatFileItemReader<SalesDTO> flatFileItemReader){
        return new StepBuilder("fromFileDownloadedToDb", jobRepository)
                .<SalesDTO,SalesInfo>chunk(10, transactionManager)
                .reader(flatFileItemReader)
                .processor(salesMapper::mapToEntity)
                .writer(salesJpaWriter())
                .build();
    }

    @Bean
    @JobScope
    public FlatFileItemReader<SalesDTO> flatFileItemReader(@Value("#{jobExecutionContext['input.file.path']}") String resource){
        return new FlatFileItemReaderBuilder<SalesDTO>()
                .name("fileReader")
                .resource(new FileSystemResource(resource))
                .delimited()
                .delimiter(",")
                .names("saleId", "productId", "customerId", "saleDate", "saleAmount", "location", "country")
                .targetType(SalesDTO.class)
                .linesToSkip(1)
                .saveState(Boolean.TRUE)  // saves reader's state for resumability on error
                .build();
    }

    @Bean
    public JpaItemWriter<SalesInfo> salesJpaWriter(){
        return new JpaItemWriterBuilder<SalesInfo>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
