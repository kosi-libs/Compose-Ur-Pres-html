import org.kodein.compose.html.pres.Slide
import org.kodein.compose.html.pres.Transitions.Fade
import org.kodein.compose.html.pres.hiddenIf
import org.kodein.compose.html.pres.emojis.Emoji
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.px
import org.kodein.compose.html.css.css
import org.kodein.compose.html.pres.Animations
import org.kodein.compose.html.pres.Animations.Move.Towards.Bottom
import org.kodein.compose.html.pres.Slides


val modes = Slides(Animations.Move(towards = Bottom)) {
    +Slide(
        "router",
        stateCount = 2
    ) { state ->
        H1 { Text("Have you noticed the address bar ${Emoji.point_up}?") }
        P({
            hiddenIf(state < 1, Fade())
        }) {
            Text("The router mode generates unique URLs for each slide & state!")
        }
    }

    +Slide(
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
            hiddenIf(state < 1, Fade())
        }) {
            Small {
                Text("Except for the current slide before any movement!")
            }
        }
        P {
            B { Text("On mobile:") }
            Ul({
                css { margin(0.px) }
            }) {
                Li {
                    B { Text("To enter:") }
                    Text(" use the the right ┅ button.")
                }
                Li {
                    B { Text("To exit:") }
                    Text(" click on the middle (current) slide.")
                }
            }
        }
    }

    +Slide(
        name = "presenter",
        notes = {
            Text("This slide has notes!")
        }
    ) {
        H1 {
            Text( "Hit 'p' to toggle presenter mode!")
        }
        P {
            Text("Sorry, no presenter mode on mobile.")
        }
    }

    +Slide(
        name = "synchronized",
    ) {
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
}
