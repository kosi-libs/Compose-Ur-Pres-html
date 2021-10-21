package net.kodein.pres

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.CSSSelector.PseudoClass.Companion.active
import org.jetbrains.compose.web.css.selectors.descendant
import org.jetbrains.compose.web.css.selectors.plus
import org.jetbrains.compose.web.css.selectors.type
import org.jetbrains.compose.web.dom.*


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
        classes(PresStyle.css {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
        })
    }) {
        Div({
            classes(PresStyle.css {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Row)
                flex(1)
            })
        }) {
            val currentSlide = slides[currentState.index]
            for (i in 0..currentSlide.lastState) {
                Div({
                    classes(PresStyle.css {
                        flex(1)
                        marginRight(1.em)
                        height(0.8.em)
                        borderRadius(0.4.em)
                    })
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
            classes(PresStyle.css {
                fontFamily("sans-serif")
                textAlign("center")
            })
        }) {
            B {
                Text("${currentState.index}..${slides.lastIndex}")
            }
            Br()
            Small({
                classes(PresStyle.css {
                    color(Color.gray)
                })
            }) {
                val pastStates = slides.subList(0, currentState.index).sumOf { it.stateCount }
                val totalStates = slides.sumOf { it.stateCount }
                Text("${pastStates + currentState.state}..${totalStates - 1}")
            }
        }
    }

    Div({
        classes(PresStyle.css {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)
        })
    }) {
        Div({
            classes(PresStyle.css {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                (descendant(self, type("button"))) {
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
            })
        }) {
            if (Timer.isRunning()) Button({ onClick { Timer.stop() } }) { Text("PAUSE") }
            else Button({ onClick { Timer.start() } }) { Text("START") }
            Button({
                if (Timer.isRunning()) disabled()
                onClick { Timer.clear() }
            }) { Text("RESET") }
        }
        P({
            classes(PresStyle.css {
                width(5.em)
                fontSize(3.em)
                textAlign("center")
                fontFamily("sans-serif")
            })
        }) {
            B { Text((Timer.seconds / 60).toString().padStart(2, '0')) }
            Span({
                classes(PresStyle.css {
                    color(Color.gray)
                })
            }) { Text(" : ") }
            Text((Timer.seconds % 60).toString().padStart(2, '0'))
        }
    }
}
