package org.kodein.compose.html.pres.widget

import androidx.compose.runtime.Composable
import org.kodein.compose.html.pres.util.zIndex
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.kodein.compose.html.css.css
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLSpanElement


@Composable
public fun InternalStrikeThroughContent(
    strikeColor: CSSColorValue,
    shown: Boolean,
    height: CSSNumeric = 50.percent,
    strikeAttrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLSpanElement>
) {
    Div({
        css {
            position(Position.Absolute)
            height(0.1.em)
            backgroundColor(strikeColor)
            left(0.em)
            zIndex(2)
            property("top", height - 0.05.em)
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
        strikeAttrs?.invoke(this)
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

@Composable
public fun StrikeThrough(strikeColor: CSSColorValue, shown: Boolean, content: ContentBuilder<HTMLSpanElement>) {
    Span({
        css {
            display(DisplayStyle.InlineBlock)
            position(Position.Relative)
        }
    }) {
        InternalStrikeThroughContent(strikeColor = strikeColor, shown = shown, content = content)
    }
}
