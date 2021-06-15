package de.hsaalen.cmt

import com.ccfraser.muirwik.components.styles.ThemeOptions
import com.ccfraser.muirwik.components.styles.createMuiTheme
import kotlinext.js.jsObject
import kotlinx.css.Color


/**
 * Convert multiplatform theme to mui theme used in material ui frontend.
 */
fun Theme.toMuiTheme(): com.ccfraser.muirwik.components.styles.Theme {
    val options: ThemeOptions = jsObject {
        palette = jsObject {
            type = "light"
            primary = jsObject { main = primaryColor.hex }
        }
    }
    return createMuiTheme(options)
}

/**
 * Convert common multiplatform color class to css color.
 */
fun de.crusader.objects.color.Color.toCssColor() = Color(hex)
