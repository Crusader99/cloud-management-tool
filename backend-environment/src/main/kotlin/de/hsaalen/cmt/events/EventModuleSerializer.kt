package de.hsaalen.cmt.events

import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import mu.KotlinLogging
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType

/**
 * Logging instance for this class.
 */
private val logger = KotlinLogging.logger {}

/**
 * Define a module for kotlin serializers (like JSON or ProtoBuf). A module declares subtypes of the [Event] interface
 * so that the serializer knows where to find the implementation classes. This field automatically scans all packages
 * for subtypes of the [Event] interface and adds them to the module. A module can be passed as parameter to serializer.
 */
val eventModule = SerializersModule {
    polymorphic(Event::class) {
        // Scan for subclasses of the Event class at runtime including subpackages
        val reflections = Reflections("de.hsaalen.cmt", SubTypesScanner(false))
        val subTypes: Set<Class<out Event>> = reflections.getSubTypesOf(Event::class.java)
        for (cls in subTypes) {
            register(cls.kotlin)
        }
    }
}

/**
 * Helper extension function to register a class as polymorphic subtype of a base class in the current function context.
 */
private fun <T : Any> PolymorphicModuleBuilder<T>.register(cls: KClass<T>) {
    logger.debug("subclass(" + cls.simpleName + "::class)")
    if (cls.isSealed) {
        logger.debug("Skip " + cls.simpleName)
        return
    }
    try {
        val serializer = serializer(cls.starProjectedType) as KSerializer<T>
        subclass(cls, serializer)
    } catch (ex: Exception) {
        throw IllegalStateException("Unable to register event for serialization: " + cls.qualifiedName, ex)
    }
}
