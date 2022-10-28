package net.kodein.pres.widget

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.kodein.cic.css


@Composable
public fun StrikeThrough(strikeColor: CSSColorValue, shown: Boolean, content: @Composable () -> Unit) {
    Span({
        css {
            display(DisplayStyle.InlineBlock)
            position(Position.Relative)
        }
    }) {
        Div({
            css {
                position(Position.Absolute)
                height(0.1.em)
                backgroundColor(strikeColor)
                left(0.em)
                property("top", 50.percent - 0.05.em)
                transitions {
                    "opacity" { duration = 1.s }
                    "width" { duration = 1.s }
                }
            }
            style {
                if (shown) {
                    width(100.percent)
                    opacity(1)
                } else {
                    width(0.percent)
                    opacity(0)
                }
            }
        })
        Span({
            css {
                transitions {
                    "opacity" { duration = 1.s }
                }
            }
            style {
                if (shown) { opacity(0.6) }
            }
        }) {
            content()
        }
    }
}