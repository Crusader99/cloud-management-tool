package de.hsaalen.cmt.storage

import de.hsaalen.cmt.environment.*
import de.hsaalen.cmt.network.dto.objects.UUID
import mu.KotlinLogging
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
     * Local logger instance for this class.
     */
    private val logger = KotlinLogging.logger { }

    /**
     * Local client instance.
     */
    lateinit var client: S3Client

    /**
     * Configure the client and test connection with server.
     */
    fun configure() {
        if (S3_PASSWORD == DEFAULT_CREDENTIAL_VALUE) {
            logger.warn("Please configure a secure password for S3 via system environment variables!")
        }

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
        logger.info("Uploading file content for $uuid")
        client.putObject({
            it.bucket(S3_BUCKET)
            it.key(uuid.value)
        }, RequestBody.fromInputStream(contentStream, contentLength))
    }

    /**
     * Download the file content from a specific reference by [UUID].
     */
    fun downloadFile(uuid: UUID): InputStream {
        logger.info("Downloading file content for $uuid")
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
            logger.info("Deleting file content from $uuid")
            client.deleteObject {
                it.bucket(S3_BUCKET)
                it.key(uuid.value)
            }
        } catch (ex: Exception) {
            throw IllegalStateException("Unable to delete $uuid from S3 storage", ex)
        }
    }

}
