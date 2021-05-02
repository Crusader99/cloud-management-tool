package de.hsaalen.cmt

import de.hsaalen.cmt.pages.LoginPage
import de.hsaalen.cmt.pages.MainPage
import materialui.styles.themeprovider.themeProvider
import react.*


/**
 * The main app component.
 */
class WebApp : RComponent<RProps, WebApp.State>() {

    interface State : RState {
        var isLoggedIn: Boolean
    }

    /**
     * Called when this component is loaded.
     */
    override fun State.init() {
        isLoggedIn = false
    }

    /**
     * Called whenever an update is required.
     */
    override fun RBuilder.render() {
        themeProvider(Themes.LIGHT) {
            if (state.isLoggedIn) {
                child(MainPage::class) {}
            } else {
                child(LoginPage::class) {
                    attrs {
                        onLogin = ::onLogin
                    }
                }
            }
        }
    }

    /**
     * Called when user had entered the username and password.
     */
    private fun onLogin(credentials: LoginPage.Credentials) {
        setState {
            isLoggedIn = true
        }
    }

}