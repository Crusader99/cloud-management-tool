package de.hsaalen.cmt.theme

import com.ccfraser.muirwik.components.MuiThemeProviderProps
import com.ccfraser.muirwik.components.mMuiThemeProvider
import com.ccfraser.muirwik.components.themeContext
import react.RBuilder
import react.RComponent
import react.RState

/**
 * A custom theme provider to support IR compiler with the @JsExport annotation.
 */
@JsExport
class ThemeProvider(props: MuiThemeProviderProps) : RComponent<MuiThemeProviderProps, RState>(props) {
    override fun RBuilder.render() {
        mMuiThemeProvider(props.theme) {
            themeContext.Provider(props.theme) {
                children()
            }
        }
    }
}
