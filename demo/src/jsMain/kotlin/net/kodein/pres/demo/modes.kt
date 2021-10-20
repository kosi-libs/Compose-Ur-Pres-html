package net.kodein.pres.demo

import net.kodein.pres.Slide
import net.kodein.pres.Transitions.fade
import net.kodein.pres.hiddenIf
import org.jetbrains.compose.web.dom.*
import net.kodein.pres.emojis.Emoji
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target


val modes = listOf(
    Slide(
        "router",
        stateCount = 2
    ) { state ->
        H1 { Text("Have you noticed the address bar ${Emoji.point_up}?") }
        P({
            hiddenIf(state < 1, fade)
        }) {
            Text("The router mode generates unique URLs for each slide & state!")
        }
    },

    Slide(
        name = "overview",
        stateCount = 2
    ) { state ->
        H1 {
            Text( "Hit escape to toggle overview mode!")
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
    },

    Slide(
        name = "presenter",
        notes = {
            Text("This slide has notes!")
        }
    ) { state ->
        H1 {
            Text( "Hit 'p' to toggle presenter mode!")
        }
    },

    Slide(
        name = "synchronized",
    ) { state ->
        H1 {
            Text( "Synchronized")
        }

        P {
            Text("You can ")
            A(href = "#synchronized", {
                target(ATarget.Blank)
            }) {
                Text("open a second window with the same URL")
            }
            Text(",")
            Br()
            Text("and both window presentation states will be synchronized!")
        }

        P {
            Small {
                Text("This is very useful to have presentation mode on one screen, and full screen presentation on another.")
            }
        }
    }
)
