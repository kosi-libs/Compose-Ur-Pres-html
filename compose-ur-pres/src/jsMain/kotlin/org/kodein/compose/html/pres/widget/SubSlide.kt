package org.kodein.compose.html.pres.widget

import androidx.compose.runtime.Composable
import org.kodein.compose.html.pres.Transition
import org.kodein.compose.html.pres.shownIf
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.kodein.compose.html.css.css


@Composable
public fun SubSlide(
    shownIf: Boolean,
    transition: Transition,
    content: @Composable () -> Unit
) {
    Div({
        shownIf(shownIf, transition)
        css {
            position(Position.Absolute)
            top(0.px)
            left(0.px)
            width(100.percent - 4.em)
            height(100.percent - 4.em)
            padding(2.em)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
        }
    }) {
        content()
    }
}
