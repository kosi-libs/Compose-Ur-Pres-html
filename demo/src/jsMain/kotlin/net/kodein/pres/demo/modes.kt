package net.kodein.pres.demo

import net.kodein.pres.Slide
import net.kodein.pres.Transitions.fade
import net.kodein.pres.hiddenIf
import org.jetbrains.compose.web.dom.*
import net.kodein.pres.emojis.Emoji


val router = Slide(
    "router",
    states = 2
) { state ->
    H1 { Text("Have you noticed the address bar ${Emoji.point_up}?") }
    P({
        hiddenIf(state < 1, fade)
    }) {
        Text("The router mode generates unique URLs for each slide & state!")
    }
}

val overview = Slide(
    name = "overview",
    states = 2
) { state ->
    H1 {
        Text( "Hit escape to see overview mode")
    }
    P {
        Text("Unless explicitly configured, overview mode shows each slide in its last state")
    }
    P({
        hiddenIf(state < 1, fade)
    }) {
        Small {
            Text("Except for the current slide before any movement!")
        }
    }
}

val presenter = Slide(
    name = "presenter"
) { state ->
    H1 {
        Text( "Presenter mode is a work in progress!")
    }
}
