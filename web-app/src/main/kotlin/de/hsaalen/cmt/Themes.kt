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
                main = Color(Theme.primaryColor.hex)
            }
        }
    }

}