import org.kodein.compose.html.pres.Slide
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.kodein.compose.html.pres.emojis.Emoji
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.dom.Br


val inner = Slide(
    "inner"
) {
    H1 { Text("Have you noticed the bottom progress bar ${Emoji.point_down}?") }
    P {
        Text("This is not standard.")
        Br()
        Text("You can add your own composition on top of the presentation engine!")
    }
    H1({
        style {
            margin(0.em)
            fontSize(10.cssRem)
        }
    }) {
        Text(Emoji.genie)
    }
}
