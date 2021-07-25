package de.hsaalen.cmt.storage

import de.hsaalen.cmt.environment.S3_BUCKET
import de.hsaalen.cmt.environment.S3_ENDPOINT
import de.hsaalen.cmt.environment.S3_PASSWORD
import de.hsaalen.cmt.environment.S3_USER
import de.hsaalen.cmt.network.dto.objects.UUID
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException
import java.io.InputStream
import java.net.URI

/**
 * This object provides access to files stored in S3 storage.
 */
internal object StorageS3 {

    /**
     * Local client instance.
     */
    lateinit var client: S3Client

    /**
     * Configure the client and test connection with server.
     */
    fun configure() {
        try {
            val credentials = StaticCredentialsProvider.create(AwsBasicCredentials.create(S3_USER, S3_PASSWORD))
            client = S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI(S3_ENDPOINT))
                .credentialsProvider(credentials)
                .build()
        } catch (ex: Exception) {
            throw IllegalStateException("Unable to configure S3 client", ex)
        }
        try {
            client.createBucket {
                it.bucket(S3_BUCKET)
            }
            client.waiter().waitUntilBucketExists {
                it.bucket(S3_BUCKET)
            }
        } catch (ex: Exception) {
            // Ignore when bucket already existing
            if (ex !is BucketAlreadyExistsException && ex !is BucketAlreadyOwnedByYouException) {
                throw IllegalStateException("Unable to connect S3 client", ex)
            }
        }
    }

    /**
     * Create and upload a file with content.
     */
    fun uploadFile(uuid: UUID, contentStream: InputStream, contentLength: Long) {
        client.putObject({
            it.bucket(S3_BUCKET)
            it.key(uuid.value)
        }, RequestBody.fromInputStream(contentStream, contentLength))
    }

    /**
     * Download the file content from a specific reference by [UUID].
     */
    fun downloadFile(uuid: UUID): InputStream {
        return client.getObject {
            it.bucket(S3_BUCKET)
            it.key(uuid.value)
        }
    }

    /**
     * Delete a file by a specific reference [UUID].
     */
    fun deleteFile(uuid: UUID) {
        try {
            client.deleteObject {
                it.bucket(S3_BUCKET)
                it.key(uuid.value)
            }
        } catch (ex: Exception) {
            throw IllegalStateException("Unable to delete $uuid from S3 storage", ex)
        }
    }

}
