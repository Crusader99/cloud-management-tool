package de.hsaalen.cmt.statistics

import io.ktor.http.*
import mu.KotlinLogging
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.Point
import java.util.*

/**
 * Wrapper class for the connection to the influx database. Influx db can be used to write
 * statistics which can be displayed in grafana later.
 */
class InfluxWrapper(
    url: Url,
    private val database: String,
    user: String,
    password: String
) {

    /**
     * Logger for this wrapper class
     */
    private val logger = KotlinLogging.logger("InfluxDB")

    /**
     * Connection to influx database
     */
    private val connection: InfluxDB

    init {
        logger.info { "Connecting to influx ($url) using username ($user) and password..." }
        connection = InfluxDBFactory.connect(url.toString(), user, password)
    }

    /**
     * Report the given measurements to influx with time stamp of current date
     *
     * @param table - Table name in database
     * @param fields - Fields to be saved
     * @param tag - Optional tag to simplify identify in grafana
     */
    fun report(table: String, fields: Map<String, Any>, tag: String? = null) {
        // Report makes no sense when fields are empty
        if (fields.isEmpty()) {
            return
        }

        // Build a measurement point
        val builder: Point.Builder = Point.measurement(table)

        // Add optional tag
        if (tag != null) {
            builder.tag("tag", tag)
        }

        // Add measurements
        builder.fields(fields.mapKeys { it.key.toLowerCase(Locale.ROOT) })

        // Send data to influx database
        connection.write(database, null, builder.build())
    }

}