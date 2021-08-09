package de.hsaalen.cmt.redis

import de.hsaalen.cmt.environment.REDIS_HOST
import de.hsaalen.cmt.environment.REDIS_PORT
import de.hsaalen.cmt.environment.REDIS_TOPIC
import de.hsaalen.cmt.events.Event
import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.events.eventModule
import de.hsaalen.cmt.events.notifications.DirectNotificator
import de.hsaalen.cmt.events.notifications.Notificator
import de.hsaalen.cmt.network.dto.objects.UUID
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import mu.KotlinLogging
import org.redisson.Redisson
import org.redisson.api.RTopic
import org.redisson.config.Config
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * A [Notificator] notifies the registered event handlers when an event was called. This [RedisNotificator] allows
 * synchronizing events over redis for multiple backend servers.
 */
object RedisNotificator : DirectNotificator() {

    /**
     * Logging instance for this class.
     */
    private val logger = KotlinLogging.logger("RedisNotificator")

    /**
     * Serialization for event instances to be sent over network. This is done using ProtoBuf protocol.
     */
    val serializer = ProtoBuf {
        serializersModule = eventModule
    }

    /**
     * Unique backend server instance identifier.
     */
    var serverInstance: UUID = UUID(java.util.UUID.randomUUID().toString())
        private set

    /**
     * The registered topic to listed on for new event notifications from other server instances.
     */
    private var topic: RTopic? = null

    /**
     * Configure redisson (redis driver) with system environment variables, test connection and register as default
     * notificator in the [GlobalEventDispatcher].
     */
    fun configure() {
        val configuration = Config().apply {
            useSingleServer().address = "redis://$REDIS_HOST:$REDIS_PORT"
        }

        // Connect to redis
        val topic = Redisson.create(configuration).getTopic(REDIS_TOPIC)
        topic.addListener(ByteArray::class.java, ::onReceiveEvent)
        this.topic = topic

        // Register this redis notificator as default notificator
        GlobalEventDispatcher.notificator = this
    }

    /**
     * Deploy events to redis instead of directly calling the registered event handlers.
     */
    override suspend fun notify(event: Event) {
        // Execute in async mode using kotlin coroutines and suspend functions
        suspendCoroutine<Unit> { continuation ->
            logger.debug { "Sending event over Redis: $event" }
            val jsonEvent = serializer.encodeToByteArray(event)
            topic?.publishAsync(jsonEvent)?.onComplete { _, error ->
                if (error == null) {
                    continuation.resume(Unit)
                } else {
                    continuation.resumeWithException(error)
                }
            }
        }
    }

    /**
     * Handle incoming events from other server instances.
     */
    private fun onReceiveEvent(channel: CharSequence, jsonEvent: ByteArray) {
        if (channel == REDIS_TOPIC) {
            runBlocking {
                // Execute like normal events
                val event: Event = serializer.decodeFromByteArray(jsonEvent)
                logger.debug { "Received event over Redis: $event" }
                super.notify(event)
            }
        }
    }

}
