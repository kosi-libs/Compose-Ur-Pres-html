package net.kodein.pres.widget

import androidx.compose.runtime.Composable
import net.kodein.pres.PresStyle
import net.kodein.pres.Transition
import net.kodein.pres.shownIf
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div


@Composable
public fun SubSlide(
    shownIf: Boolean,
    transition: Transition,
    content: @Composable () -> Unit
) {
    Div({
        shownIf(shownIf, transition)
        classes(PresStyle.css {
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
        })
    }) {
        content()
    }
}
