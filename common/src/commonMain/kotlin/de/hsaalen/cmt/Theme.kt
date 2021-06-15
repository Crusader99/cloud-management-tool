package de.hsaalen.cmt

import de.crusader.objects.color.Color

/**
 * Sealed theme interface to define colors to be used in all projects because this is in multiplatform common code.
 */
sealed interface Theme {

    /**
     * The color which is used for highlighted components.
     */
    val primaryColor: Color

    companion object {
        val LIGHT = LightTheme
    }
}

/**
 * Define colors for the light theme in multiplatform common project for all devices.
 */
object LightTheme : Theme {

    /**
     * The color which is used for highlighted components.
     */
    override val primaryColor = Color.byHex("#1890f2")

}

