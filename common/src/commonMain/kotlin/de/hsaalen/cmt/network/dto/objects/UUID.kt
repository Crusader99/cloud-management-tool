package de.hsaalen.cmt.network.dto.objects

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Value inline class for using type safe UUID class. Basically it represents a string.
 */
@JvmInline
@Serializable
value class UUID(val value: String) {

    /**
     * Directly convert to plain string without object notation.
     */
    override fun toString() = value

}
