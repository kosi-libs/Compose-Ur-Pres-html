import org.kodein.compose.html.pres.emojis.Emoji
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.kodein.compose.html.css.css
import org.kodein.compose.html.pres.*
import org.kodein.compose.html.pres.Transitions.Fade
import kotlin.time.Duration.Companion.seconds


val animations = Slide(
    name = "container",
    stateCount = 2,
    inAnimation = Animations.Flip(2.seconds),
    config = {
        OverlayAttrs {
            css {
                backgroundColor(Color("#480F40"))
            }
        }
    }
) { state ->
    H1 {
        Text("There can also be complex slide transition animations!")
    }
    P({
        hiddenIf(state < 1, Fade())
    }) {
        Text("Have you noticed the background change? ${Emoji.thinking}")
    }
}
