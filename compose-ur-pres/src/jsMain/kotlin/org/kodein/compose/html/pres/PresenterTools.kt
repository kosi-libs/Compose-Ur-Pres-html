package org.kodein.compose.html.pres

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.kodein.compose.html.css.css
import org.w3c.dom.HTMLElement


private object Timer {
    private var state by mutableStateOf(0)
    private var interval: Int? by mutableStateOf(null)

    val seconds get() = state

    fun start() {
        if (interval != null) return
        interval = window.setInterval({
            state += 1
        }, 1000)
    }
    fun stop() {
        if (interval == null) return
        window.clearInterval(interval!!)
        interval = null
    }
    fun clear() {
        state = 0
    }
    fun isRunning() = interval != null
}

@Composable
internal fun PresenterTools(
    slides: List<Slide>,
    currentState: SlideState
) {
    Div({
        css {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
        }
    }) {
        Div({
            css {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Row)
                flex(1)
            }
        }) {
            val currentSlide = slides[currentState.index]
            for (i in 0..currentSlide.lastState) {
                Div({
                    css {
                        flex(1)
                        marginRight(1.em)
                        height(0.8.em)
                        borderRadius(0.4.em)
                    }
                    style {
                        when {
                            i == currentSlide.lastState -> {}
                            i < currentState.state -> {
                                border(0.2.em, LineStyle.Solid, Color.gray)
                                backgroundColor(Color.gray)
                            }
                            else -> {
                                border(0.2.cssRem, LineStyle.Solid, Color.darkgray)
                            }
                        }
                    }
                })
            }
        }
        Div({
            css {
                fontFamily("sans-serif")
                textAlign("center")
            }
        }) {
            B {
                Text("${currentState.index}..${slides.lastIndex}")
            }
            Br()
            Small({
                css {
                    color(Color.gray)
                }
            }) {
                val pastStates = slides.subList(0, currentState.index).sumOf { it.stateCount }
                val totalStates = slides.sumOf { it.stateCount }
                Text("${pastStates + currentState.state}..${totalStates - 1}")
            }
        }
    }

    Div({
        css {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)
        }
    }) {
        Div({
            css {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                "button" {
                    padding(0.4.em, 0.6.em)
                    margin(0.4.em)
                    backgroundColor(Color.darkgray)
                    borderRadius(0.2.em)
                    border(0.em)
                    cursor("pointer")
                    (self + active) {
                        backgroundColor(Color.gray)
                    }
                }
            }
        }) {
            Button({
                onClick {
                    if (Timer.isRunning()) Timer.stop()
                    else Timer.start()
                    ((it.target as HTMLElement).parentElement as HTMLElement).focus()
                }
            }) { Text(if (Timer.isRunning()) "PAUSE" else "START") }

            Button({
                if (Timer.isRunning()) disabled()
                onClick {
                    Timer.clear()
                    ((it.target as HTMLElement).parentElement as HTMLElement).focus()
                }
            }) { Text("RESET") }
        }
        P({
            css {
                width(5.em)
                fontSize(3.em)
                textAlign("center")
                fontFamily("sans-serif")
            }
        }) {
            B { Text((Timer.seconds / 60).toString().padStart(2, '0')) }
            Span({
                css {
                    color(Color.gray)
                }
            }) { Text(" : ") }
            Text((Timer.seconds % 60).toString().padStart(2, '0'))
        }
    }
}
