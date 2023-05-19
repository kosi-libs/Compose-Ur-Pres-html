package org.kodein.compose.html.pres

import androidx.compose.runtime.*
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.kodein.compose.html.css.css
import org.kodein.compose.html.pres.util.FEventListener
import org.kodein.compose.html.pres.util.zIndex
import org.w3c.dom.DOMRect

@Composable
internal fun TouchControls(
    hasNext: Boolean,
    goNext: () -> Unit,
    hasPrev: Boolean,
    goPrev: () -> Unit,
    toggleOverview: () -> Unit,
    slideSize: DOMRect?,
) {
    var opacity by remember { mutableStateOf(1.0) }

    DisposableEffect(null) {
        var timeoutId: Int? = null
        val listener = FEventListener {
            opacity = 1.0
            timeoutId?.let { window.clearTimeout(it) }
            timeoutId = window.setTimeout({ opacity = 0.2 }, 3_000)
        }
        window.addEventListener("touchstart", listener)
        window.addEventListener("resize", listener)
        listener()
        onDispose {
            window.removeEventListener("touchstart", listener)
            window.removeEventListener("resize", listener)
        }
    }

    var windowIsLandscape: Boolean? by remember { mutableStateOf(null) }

    DisposableEffect(null) {
        val listener = FEventListener {
            windowIsLandscape = window.innerWidth > window.innerHeight
        }
        window.addEventListener("resize", listener)
        listener()
        onDispose { window.removeEventListener("resize", listener) }
    }
    if (windowIsLandscape == null) return

    val slideIsLandscape = slideSize != null && slideSize.width > slideSize.height
    if (windowIsLandscape != slideIsLandscape) {
        var dismissed by remember { mutableStateOf(false) }
        if (!dismissed) {
            Div({
                css {
                    width(80.vw)
                    position(Position.Absolute)
                    top(20.vw)
                    property("left", 10.vw - 0.5.em)
                    padding(0.5.em)
                    backgroundColor(Color("#CCCCCC"))
                    color(Color.black)
                    borderRadius(0.5.em)
                    zIndex(1_001)
                    textAlign("center")
                }
            }) {
                Text("This presentation is optimised for ${if (slideIsLandscape) "landscape" else "portrait"} display. You should rotate your screen.")
                Br()
                Button({
                    css {
                        marginTop(0.5.em)
                        padding(0.5.em)
                    }
                    onClick { dismissed = true }
                }) {
                    Text("DISMISS")
                }
            }
        }
    }

    val divCss: CSSBuilder.() -> Unit = {
        position(Position.Absolute)
        display(DisplayStyle.Flex)
        flexDirection(if (windowIsLandscape == true) FlexDirection.Column else FlexDirection.Row)
        margin(0.5.cssRem)
        zIndex(1_000)
        transitions { "opacity" { duration = 1.s } }

        "button" {
            fontSize(3.em)
            backgroundColor(Color.transparent)
            border(0.px)
            padding(0.25.em, 0.5.em)
            margin(0.5.cssRem)
            backgroundColor(Color("#BBBBBB66"))
            color(Color.black)
            (self + active) {
                backgroundColor(Color("#44444466"))
                color(Color.white)
            }
            transitions {
                "backgroundColor" { duration = 0.1.s }
                "color" { duration = 0.1.s }
                "opacity" { duration = 0.3.s }
            }
        }
    }

    Div({
        css {
            fontSize(0.6.em)
            divCss()
            right(0.px)
            top(0.px)
        }
        style { opacity(opacity * 0.6) }
    }) {
        Button({
            onClick { toggleOverview() }
        }) {
            Text("┅")
        }
    }
    Div({
        css {
            divCss()
            right(0.px)
            bottom(0.px)
        }
        style { opacity(opacity) }
    }) {
        Button({
            onClick { goPrev() }
            if (!hasPrev) {
                style { opacity(0) }
                disabled()
            }
        }) {
            Text("◄")
        }
        Button({
            onClick { goNext() }
            if (!hasNext) {
                style { opacity(0) }
                disabled()
            }
        }) {
            Text("►")
        }
    }
}