package com.github.bruce_mig.spring_batch_aws_telemetry.service;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

import java.nio.file.Path;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomS3Client {

    private final S3Template s3Template;
    private final S3TransferManager s3TransferManager;

    public Optional<Path> download(String bucketName){
        Assert.notNull(bucketName, "bucketName is required");
        return s3Template.listObjects(bucketName,"2025")
                .stream()
                .findFirst()
                .map(s3Resource -> transferFile(bucketName,s3Resource));
    }

    private Path transferFile(String bucketName, S3Resource s3Resource){
        Path pathDestination = Path.of(s3Resource.getFilename());


        DownloadFileRequest downloadFileRequest = DownloadFileRequest.builder()
                .getObjectRequest(request -> request.bucket(bucketName).key(s3Resource.getFilename()))
                .destination(pathDestination)
                .addTransferListener(LoggingTransferListener.create())
                .build();

        s3TransferManager.downloadFile(downloadFileRequest)
                .completionFuture()
                .join();

        log.info("File: {} downloaded successfully from bucket {}", s3Resource.getFilename(), bucketName);

        return pathDestination;
    }
}
