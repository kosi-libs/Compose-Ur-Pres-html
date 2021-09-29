package net.kodein.pres

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import net.kodein.pres.util.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.attr
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement


interface ContainerAttrs {
    val containerAttrs: AttrBuilderContext<HTMLDivElement>?
}

fun ContainerAttrs(attrs: AttrBuilderContext<HTMLDivElement>? = null) = object : ContainerAttrs {
    override val containerAttrs: AttrBuilderContext<HTMLDivElement>? get() = attrs
}

@Composable
fun PresentationState.presentationContainer(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: @Composable () -> Unit
) {
    Div({
        classes(PresStyle.css {
            width(100.percent)
            height(100.percent)
            position(Position.Absolute)
            top(0.px)
            left(0.px)
            overflow("hidden")
        })
        attrs?.invoke(this)
        if (slideConfig is ContainerAttrs) slideConfig.containerAttrs?.invoke(this)
    }) {
        content()
    }
}

interface OverlayAttrs : ContainerAttrs {
    val overlayAttrs: AttrBuilderContext<HTMLDivElement>?
}

fun OverlayAttrs(
    containerAttrs: AttrBuilderContext<HTMLDivElement>? = null,
    overlayAttrs: AttrBuilderContext<HTMLDivElement>?
) = object : OverlayAttrs {
    override val containerAttrs: AttrBuilderContext<HTMLDivElement>? get() = containerAttrs
    override val overlayAttrs: AttrBuilderContext<HTMLDivElement>? get() = overlayAttrs
}

@Composable
fun PresentationState.overlayedPresentationContainer(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: @Composable () -> Unit
) {
    presentationContainer(attrs) {
        presentationContainer({
            style {
                transition { "background-color"(slideTransitionDuration.inWholeMilliseconds.ms) }
            }
            (slideConfig as? OverlayAttrs)?.overlayAttrs?.invoke(this)
        }) {
            content()
        }
    }
}

@Composable
fun PresentationState.progress(
    color: CSSColorValue,
    height: CSSNumeric = 0.3.cssRem
) {
    Div({
        classes(AppStyle.css {
            height(height)
            position(Position.Absolute)
            left(0.px)
            bottom(0.px)
            backgroundColor(color)
            transition { "width"(0.3.s) }
        })
        style {
            width((globalState.toDouble() / (globalStateCount - 1).toDouble() * 100.0).percent)
        }
    }) {}
}
