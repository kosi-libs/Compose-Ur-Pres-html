import org.kodein.compose.html.pres.emojis.Emoji
import org.kodein.compose.html.pres.Slide
import org.jetbrains.compose.web.dom.*
import org.kodein.compose.html.pres.Slides


val move = Slides {
    +Slide(
        name = "move-1"
    ) {
        H1 {
            Text("This is ComposeUrPres")
        }

        P {
            B { Text("To advance the presentation") }
            Text(": type Right Arrow, Bottom Arrow, Space, or Enter.")
        }
        P {
            B { Text("If you are on mobile:") }
            Text(" use the right arrow at the bottom right.")
        }
    }

    +Slide(
        name = "move-2"
    ) {
        P {
            B { Text("To move back") }
            Text(": type Left Arrow, Top Arrow, or BackSpace")
        }

        P {
            Small {  Text("Still, move forward to see the rest of the features ${Emoji.wink}") }
        }
    }
}