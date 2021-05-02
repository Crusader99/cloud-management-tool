package de.hsaalen.cmt

import de.hsaalen.cmt.pages.LoginPage
import de.hsaalen.cmt.pages.MainPage
import materialui.styles.themeprovider.themeProvider
import react.*

external interface WebAppState : RState {
    var isLoggedIn: Boolean
}

/**
 * The main app component.
 */
class WebApp : RComponent<RProps, WebAppState>() {

    override fun WebAppState.init() {
        isLoggedIn = false
    }

    override fun RBuilder.render() {
        themeProvider(Themes.LIGHT) {
            if (state.isLoggedIn) {
                child(MainPage::class) {}
            } else {
                child(LoginPage::class) {
                    attrs {
                        onLogin = {
                            setState {
                                state.isLoggedIn = true
                            }
                        }
                    }
                }
            }
        }
    }

}