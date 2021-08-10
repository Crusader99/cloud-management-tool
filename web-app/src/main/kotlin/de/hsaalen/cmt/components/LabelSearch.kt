package de.hsaalen.cmt.components

import com.ccfraser.muirwik.components.form.MFormControlMargin
import com.ccfraser.muirwik.components.form.MFormControlVariant
import com.ccfraser.muirwik.components.lab.mAutoCompleteMultiValue
import com.ccfraser.muirwik.components.mTextField
import com.ccfraser.muirwik.components.spreadProps
import de.hsaalen.cmt.events.EventType
import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.events.SearchEvent
import de.hsaalen.cmt.events.launchNotification
import de.hsaalen.cmt.extensions.launch
import de.hsaalen.cmt.extensions.onEnterKey
import de.hsaalen.cmt.extensions.onTextChange
import de.hsaalen.cmt.network.session.Session
import kotlinx.css.*
import react.*
import styled.css
import styled.styledDiv

/**
 * React state of the [LabelSearch] component.
 */
external interface LabelSearchState : RState {
    var allLabels: Array<String>
    var filterLabels: Array<String>
    var searchText: String
    var isLoading: Boolean
}

/**
 * The [LabelSearch] component allows searching for specific references by label names or reference title.
 */
@JsExport
class LabelSearch : RComponent<RProps, LabelSearchState>() {

    /**
     * Register events for this component.
     */
    private val events = GlobalEventDispatcher.createBundle(this) {
        launch {
            val labels = Session.instance!!.listLabels()
            setState {
                allLabels = labels.toTypedArray()
                isLoading = false
            }
        }
    }

    /**
     * Initialize state of the [LabelSearch].
     */
    override fun LabelSearchState.init() {
        isLoading = true
        searchText = ""
        allLabels = emptyArray()
        filterLabels = emptyArray()
    }

    /**
     * Remove registered event handlers.
     */
    override fun componentWillUnmount() {
        events.unregisterAll()
    }

    /**
     * Called when complete search component is rendered.
     */
    override fun RBuilder.render() {
        styledDiv {
            css {
                position = Position.relative
                flex(1.0, 1.0, FlexBasis.auto)
            }

            mAutoCompleteMultiValue(
                options = state.allLabels,
                { args -> renderField(args) },
                value = state.filterLabels
            ) {
                attrs.filterSelectedOptions = true
                attrs.getOptionLabel = { option -> option ?: "" }
                attrs.inputValue = state.searchText
                attrs.loading = state.isLoading
                attrs.onChange = { _, value, _ ->
                    setState {
                        filterLabels = value
                    }
                    launchNotification(EventType.PRE_CHANGE_SEARCH, SearchEvent(state.searchText, state.filterLabels))
                }
                css {
                    paddingLeft = 10.px
                    paddingRight = 20.px
                }
            }
        }
    }

    /**
     * Called when only field in the search is rendered.
     */
    private fun RBuilder.renderField(params: RProps) = mTextField(
        label = "Search by labels or title",
        variant = MFormControlVariant.filled,
        margin = MFormControlMargin.dense,
        value = state.searchText
    ) {
        spreadProps(params)
        attrs {
            onTextChange { newText ->
                setState {
                    searchText = newText
                }
            }
            onEnterKey {
                launchNotification(EventType.PRE_CHANGE_SEARCH, SearchEvent(state.searchText, state.filterLabels))
            }
        }
    }

}
