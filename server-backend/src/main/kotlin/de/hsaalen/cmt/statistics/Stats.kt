package de.hsaalen.cmt.statistics

import kotlinx.coroutines.*
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KProperty

/**
 * A sealed/abstract statistic class which is intended to be implemented with various fields using
 * delegating / the by operator. All subclasses of Stats will automatically be found and reported to
 * influx db in a periodic delay.
 */
sealed class Stats {

    /**
     * Fields values of the subclass
     */
    private val fields = mutableMapOf<String, AtomicInteger>()

    /**
     * Create a new field value by the sub class using delegating / the by operator
     */
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): AtomicInteger {
        val counter = AtomicInteger(0)
        fields[property.name] = counter
        return counter
    }

    /**
     * Report the data fields of the current stats to the given target wrapper
     */
    private fun report(target: InfluxWrapper) {
        val statsName = this::class.simpleName
            ?.toLowerCase(Locale.ROOT)
            ?.removePrefix("stats")
            ?: throw IllegalStateException("Unknown name of stats class")
        val statsFields = fields.mapValues { (_, counter) ->
            counter.getAndSet(0)
        }
        target.report(statsName, statsFields)
    }

    companion object {
        /**
         * Logger for this counter class
         */
        private val logger = KotlinLogging.logger { }

        /**
         * Start reporting data with a periodic delay of all subclasses to influx db using the given
         * target parameter
         */
        fun startReporting(target: InfluxWrapper) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    while (isActive) {
                        performReport(target)
                        delay(5 * 60 * 1000L) // Report every 5 min
                    }
                } catch (t: Throwable) {
                    logger.error("Report routine failed", t)
                }
            }
        }

        /**
         * Report data fields of all subclasses to the given influx target
         */
        private fun performReport(target: InfluxWrapper) {
            logger.info("Reporting stats to influx-db...")
            for (cls in Stats::class.sealedSubclasses) {
                try {
                    val stats = cls.objectInstance
                    if (stats == null) {
                        logger.error(cls.simpleName + " doesn't seem to be an object instance")
                    } else {
                        stats.report(target)
                    }
                } catch (ex: Exception) {
                    logger.warn("Failed to report stats for " + cls.simpleName, ex)
                }
            }
        }

    }

}