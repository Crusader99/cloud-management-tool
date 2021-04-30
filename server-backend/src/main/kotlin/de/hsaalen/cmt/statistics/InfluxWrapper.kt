package de.hsaalen.cmt.statistics

import com.influxdb.LogLevel
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.influxdb.client.write.Point
import io.ktor.http.*
import mu.KotlinLogging
import java.util.*

/**
 * Wrapper class for the connection to the influx database. Influx db can be used to write
 * statistics which can be displayed in grafana later.
 */
class InfluxWrapper(
    url: Url,
    private val org: String,
    private val bucket: String,
    token: String,
) {

    /**
     * Logger for this wrapper class
     */
    private val logger = KotlinLogging.logger("InfluxDB")

    /**
     * Connection to influx database
     */
    private val connection: InfluxDBClientKotlin

    init {
        logger.info { "Connecting to influx ($url) using username ($token) and password..." }
        connection = InfluxDBClientKotlinFactory.create(url.toString(), token.toCharArray(), org)
        connection.setLogLevel(LogLevel.HEADERS)
    }

    /**
     * Report the given measurements to influx with time stamp of current date
     *
     * @param table - Table name in database
     * @param fields - Fields to be saved
     */
    suspend fun report(table: String, fields: Map<String, Any>) {
        // Report makes no sense when fields are empty
        if (fields.isEmpty()) {
            return
        }

        val point = Point.measurement(table)// Build a measurement point
            .addFields(fields.mapKeys { it.key.toLowerCase(Locale.ROOT) })  // Add measurements

        // Send data to influx database
        connection.getWriteKotlinApi().writePoint(point, bucket, org)
    }

}