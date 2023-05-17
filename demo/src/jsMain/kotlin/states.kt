import org.kodein.compose.html.pres.Animations
import org.kodein.compose.html.pres.emojis.Emoji
import org.kodein.compose.html.pres.Slide
import org.kodein.compose.html.pres.Transitions.Fade
import org.kodein.compose.html.pres.Transitions.Grow
import org.kodein.compose.html.pres.hiddenIf
import org.jetbrains.compose.web.dom.*
import kotlin.time.Duration.Companion.seconds


val states = Slide(
    name = "states",
    stateCount = 5,
    outAnimation = Animations.Flip(2.seconds)
) { state ->

    H1 {
        Text("A slide may contain multiple states.")
    }

    P {
        Ul {
            Li({
                hiddenIf(state < 1, Fade())
            }) {
                Text("This slide has 5 states!")
            }
            Li({
                hiddenIf(state < 2, Fade())
            }) {
                Text("You can use states to animate things...")
            }
            Li({
                hiddenIf(state < 3, Fade())
            }) {
                Text("...such as progressively revealing a list ${Emoji.sweat_smile}")
            }
        }
    }

    H2({
        hiddenIf(state < 4, Grow())
    }) {
        Text("Or attracting attention! ${Emoji.star_struck}")
    }
}