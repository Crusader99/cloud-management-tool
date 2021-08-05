package de.hsaalen.cmt.events

import com.ccfraser.muirwik.components.lab.alert.MAlertSeverity
import de.hsaalen.cmt.EnumPageType
import de.hsaalen.cmt.WebApp
import de.hsaalen.cmt.components.dialogs.show
import de.hsaalen.cmt.components.features.show
import de.hsaalen.cmt.file.FileSelector
import react.setState

object GuiOperations {

    /**
     * Reference to the main web app instance.
     */
    lateinit var webApp: WebApp

    /**
     * Show a loading screen until the action code block is completed.
     */
    inline fun <R> loading(action: () -> R): R {
        return try {
            webApp.setState {
                loadingTasks++
            }
            action()
        } finally {
            webApp.setState {
                loadingTasks--
            }
        }
    }

    /**
     * Show dialog for the user to request for an input value. This function will
     * suspend until the user cancels or enters a valid value.
     */
    suspend fun showInputDialog(
        title: String,
        message: String? = null,
        placeholder: String = "",
        defaultValue: String = "",
        button: String = "OK"
    ) = webApp.refInputDialog.current?.show(title, message, placeholder, defaultValue, button)

    /**
     * Open a file selector dialog that can be used to upload files. Note that
     * this function will suspend until user cancels the action or has selected some files.
     */
    suspend fun showFileSelector() = FileSelector.openDialog()

    /**
     * Opens a new snack bar and replaces the previous one when the previous one is still open.
     */
    suspend fun showSnackBar(message: String, severity: MAlertSeverity, timeoutMs: Long = 4_000) {
        webApp.refSnackBar.current?.show(message, severity, timeoutMs)
    }

    /**
     * Determinate which page should be printed on web app main screen.
     */
    fun setPage(pageType: EnumPageType) {
        webApp.setState {
            page = pageType
        }
    }

}
