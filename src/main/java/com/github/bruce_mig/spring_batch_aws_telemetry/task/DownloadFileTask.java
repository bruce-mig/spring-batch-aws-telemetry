package com.github.bruce_mig.spring_batch_aws_telemetry.task;

import com.github.bruce_mig.spring_batch_aws_telemetry.service.CustomS3Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DownloadFileTask implements Tasklet {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final CustomS3Client customS3Client;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Optional<Path> optionalDownloadedFilePath = customS3Client.download(bucketName);
        optionalDownloadedFilePath.ifPresent(filePath -> chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .put("input.file.path", filePath.toAbsolutePath().toString()));

        return RepeatStatus.FINISHED;
    }


}
