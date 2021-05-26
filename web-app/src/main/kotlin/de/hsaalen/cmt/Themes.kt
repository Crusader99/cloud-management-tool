package de.hsaalen.cmt

import kotlinx.css.Color
import materialui.styles.createMuiTheme
import materialui.styles.muitheme.MuiTheme
import materialui.styles.muitheme.options.palette
import materialui.styles.palette.options.main
import materialui.styles.palette.options.primary

/**
 * Theme color schemes.
 */
object Themes {

    /**
     * Defines the colors of the light theme.
     */
    val LIGHT: MuiTheme = createMuiTheme {
        palette {
            primary {
                main = Theme.primaryColor.toCssColor()
            }
        }
    }

}

/**
 * Convert common multiplatform color class to css color.
 */
fun de.crusader.objects.color.Color.toCssColor() = Color(hex)