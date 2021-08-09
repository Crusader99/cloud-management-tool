package de.hsaalen.cmt.test.events

import de.hsaalen.cmt.events.Event
import de.hsaalen.cmt.events.eventModule
import de.hsaalen.cmt.network.dto.objects.LabelChangeMode
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.network.dto.rsocket.LabelUpdateDto
import de.hsaalen.cmt.network.dto.rsocket.LiveDto
import de.hsaalen.cmt.network.dto.rsocket.ReferenceUpdateRemoveDto
import de.hsaalen.cmt.utils.SerializeHelper
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests related to event serialization to multiple server instances using Redis.
 */
class EventSerializerTest {

    /**
     * Test whether the initialization of the event module is correctly done while in runtime environment.
     */
    @Test
    fun testInitialization() {
        val serializer = Json {
            serializersModule = eventModule
        }
        val uuid = UUID("ceeba5c7-3e48-467a-8b85-427f2f4ca711")
        val json = serializer.encodeToString(ReferenceUpdateRemoveDto(uuid) as Event)
        println(json)

        val result = serializer.decodeFromString<Event>(json) as ReferenceUpdateRemoveDto
        println(result)
        assertEquals(uuid, result.uuid)
    }

    /**
     * Test serialization and deserialization of [LiveDto].
     */
    @Test
    fun testLiveDto() {
        val uuid = UUID("ceeba5c7-3e48-467a-8b85-427f2f4ca711")
        val event: LiveDto = LabelUpdateDto(uuid, "test", LabelChangeMode.REMOVE)
        val bytes = SerializeHelper.encodeProtoBuf(event)

        val deserialized: LiveDto = SerializeHelper.decodeProtoBuf(bytes)
        assertEquals(event, deserialized)
    }

}
