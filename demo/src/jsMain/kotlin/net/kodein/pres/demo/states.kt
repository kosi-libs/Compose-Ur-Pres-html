package net.kodein.pres.demo

import net.kodein.pres.Animations
import net.kodein.pres.emojis.Emoji
import net.kodein.pres.Slide
import net.kodein.pres.Transitions.fade
import net.kodein.pres.Transitions.grow
import net.kodein.pres.hiddenIf
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
                hiddenIf(state < 1, fade)
            }) {
                Text("This slide has 5 states!")
            }
            Li({
                hiddenIf(state < 2, fade)
            }) {
                Text("You can use states to animate things...")
            }
            Li({
                hiddenIf(state < 3, fade)
            }) {
                Text("...such as progressively revealing a list ${Emoji.sweat_smile}")
            }
        }
    }

    H2({
        hiddenIf(state < 4, grow)
    }) {
        Text("Or attracting attention! ${Emoji.star_struck}")
    }
}