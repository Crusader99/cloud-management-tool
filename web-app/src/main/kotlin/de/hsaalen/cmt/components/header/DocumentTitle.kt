package de.hsaalen.cmt.components.header

import com.ccfraser.muirwik.components.mTooltip
import com.ccfraser.muirwik.components.mTypography
import de.hsaalen.cmt.events.*
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.rsocket.ReferenceUpdateRenameDto
import kotlinx.css.*
import react.*
import styled.css
import styled.styledDiv

/**
 * React state of the [DocumentTitle] component.
 */
external interface DocumentTitleState : RState {
    var reference: Reference
}

/**
 * The [DocumentTitle] component allows renaming the title of a document.
 */
@JsExport
class DocumentTitle : RComponent<RProps, DocumentTitleState>() {

    /**
     * Register events for this component.
     */
    private val events = GlobalEventDispatcher.createBundle(this) {
        register(::onServerRenamedReference) // Event called by server
    }

    /**
     * Initialize state of the [DocumentTitle].
     */
    override fun DocumentTitleState.init() {
        reference = GuiOperations.webApp.state.reference!!
    }

    /**
     * Remove registered event handlers.
     */
    override fun componentWillUnmount() {
        events.unregisterAll()
    }

    /**
     * Called when complete [DocumentTitle] component is rendered.
     */
    override fun RBuilder.render() {
        mTooltip("Rename") {
            mTypography(state.reference.displayName) {
                attrs {
                    onClick = {
                        launchNotification(EventType.PRE_USER_RENAME_REFERENCE, ReferenceEvent(state.reference))
                    }
                }
                css {
                    cursor = Cursor.pointer
                }
            }
        }
        styledDiv {
            css {
                position = Position.relative
                flex(1.0, 1.0, FlexBasis.auto)
            }
        }
    }

    /**
     * Event called by server after a reference got renamed.
     */
    private fun onServerRenamedReference(event: ReferenceUpdateRenameDto) {
        setState {
            reference = reference.copy(displayName = event.newName)
        }
    }


}
