package de.hsaalen.cmt.test

import de.crusader.webscraper.common.WebScraper
import de.crusader.webscraper.htmlcleaner.selenium
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.net.ServerSocket
import java.net.URL
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReactAvailabilityTest {

    private var webServer: ApplicationEngine? = null
    private val freeHostSystemPort = ServerSocket(0).use { it.localPort }

    @BeforeTest
    fun setupWebServer() {
        val webFolder = File("../web-app/build/artifact-js")
        if (!webFolder.exists()) {
            throw IllegalStateException("Please build web-app first to run test. Not found: " + webFolder.canonicalPath)
        }
        webServer = embeddedServer(CIO, freeHostSystemPort) {
            routing {
                static {
                    println("Provided static folder")
                    staticRootFolder = webFolder
                    default(File(webFolder, "index.html"))
                }
            }
        }.start(wait = false)
    }

    @AfterTest
    fun stopWebServer() {
        //  webServer?.stop(1000, 1000)
    }

    @Test
    @Disabled
    fun test() {
        WebScraper.selenium(URL("http://localhost:$freeHostSystemPort/")).use { browser ->
            println("Connected")
        }
    }

}
