package net.kodein.pres.demo

import net.kodein.pres.emojis.Emoji
import net.kodein.pres.Slide
import org.jetbrains.compose.web.dom.*


val move = listOf(
    Slide(
        name = "move-1"
    ) {
        H1 {
            Text("This is ComposeUrPres")
        }

        P {
            B { Text("To advance the presentation") }
            Text(": type Right Arrow, Bottom Arrow, Space, or Enter")
        }
    },

    Slide(
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
)