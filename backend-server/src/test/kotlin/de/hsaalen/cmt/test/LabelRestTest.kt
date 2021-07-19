package de.hsaalen.cmt.test

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

@TestInstance(Lifecycle.PER_CLASS)
class LabelRestTest {

//    private val freePort = ServerSocket(0).use { it.localPort }

    @BeforeAll
    @Disabled
    fun setupRestServer() {
//        DatabaseModules.init()
//        EventHandlers.init()
//        RestServer.configure(freePort).start(wait = false)
    }

    @Test
    @Disabled
    fun testLabels() {
//        runBlocking {
//            Session.register("Simon", "simon@test.de", "123456")
//        }
    }

}
