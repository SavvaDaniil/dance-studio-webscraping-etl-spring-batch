package org.savvadaniil.shared.storage;

import io.minio.*;
import io.minio.errors.MinioException;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class S3Storage {

    private final MinioClient minioClient;
    private final String bucketName;

    public S3Storage(
            MinioClient minioClient,
            @Value("${MINIO_BUCKET_NAME}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.createBucketIfNotExists(minioClient, bucketName);
    }

    public void createBucketIfNotExists(MinioClient minioClient, String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }

        } catch (MinioException | IOException | InvalidKeyException
                 | NoSuchAlgorithmException e) {

            throw new RuntimeException(
                    "Failed to initialize MinIO bucket: " + bucketName,
                    e
            );
        }
    }

    public void uploadFile(String s3Prefix, Path localFile, String objectName) throws Exception {
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(s3Prefix + "/" + objectName)
                        .filename(localFile.toString())
                        .build()
        );
    }

    public InputStream download(String s3Prefix, String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(s3Prefix + "/" + objectName)
                        .build()
        );
    }
}
