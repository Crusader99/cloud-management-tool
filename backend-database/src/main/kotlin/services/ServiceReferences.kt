package services

import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import kotlin.random.Random
import kotlin.random.nextInt

object ServiceReferences {

    suspend fun createItem(title: String) {

    }

    suspend fun listReferences(query: ClientReferenceQueryDto): ServerReferenceListDto {
        val refs = mutableListOf<Reference>()

        // Currently create only dummy objects
        // TODO: replace with actual db query
        repeat(10 + Random.nextInt(100)) { index ->
            val accessCode = Random.nextInt('A'.toInt()..'Z'.toInt()).toChar().toString()
            val displayName = "File-Ref-$index"
            val now = System.currentTimeMillis()
            val labels = listOf("note")
            refs += Reference(accessCode, displayName, "text", now, now, labels)
        }
        return ServerReferenceListDto(refs)
    }

}
