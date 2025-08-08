package com.hfgroup.mortgage.service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;

public class S3Service {

    private final S3Client s3Client; // For uploading files
    private final S3Presigner s3Presigner; // For generating presigned URLs

    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    /**
     * Upload a file to an S3 bucket.
     *
     * @param bucketName The name of the S3 bucket.
     * @param keyName    The path for the object in S3.
     * @param filePath   The path of the local file to be uploaded to S3.
     */
    public void uploadFile(String bucketName, String keyName, String filePath) {
        // Build the PutObjectRequest
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        // Upload the file
        PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, Paths.get(filePath));
        System.out.println("File uploaded successfully. ETag: " + putObjectResponse.eTag());
    }

    /**
     * Generate a presigned URL to access an uploaded file.
     *
     * @param bucketName The name of the S3 bucket.
     * @param keyName    The key (path) for the object in S3.
     * @param duration   The duration the presigned URL will be valid for.
     * @return The presigned URL for accessing the file.
     */
    public URL generatePresignedUrl(String bucketName, String keyName, Duration duration) {
        // Build the GetObjectRequest for the uploaded file
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        // Generate a presigned request
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(r -> r
                .signatureDuration(duration)
                .getObjectRequest(getObjectRequest));

        // Return the presigned URL
        return presignedRequest.url();
    }
}