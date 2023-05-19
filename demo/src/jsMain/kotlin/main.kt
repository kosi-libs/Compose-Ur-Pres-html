import org.kodein.compose.html.pres.*
import org.kodein.compose.html.pres.sourcecode.installHighlightJsTheme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.kodein.compose.html.css.css


fun main() {
    installHighlightJsTheme("darcula")

    presentationAppInBody(
        enableRouter = true,
        syncId = "demo",
        presentationContainer = { attrs, content ->
            defaultOverlayedPresentationContainer(
                containerAttrs = {
                    css {
                        backgroundColor(Color("#230720"))
                        backgroundImage("url('logo-bg.svg')")
                        backgroundRepeat("no-repeat")
                        backgroundPosition("right 0 bottom -15rem")
                        backgroundSize("contain")
                        color(Color.white)
                        fontFamily("sans-serif")
                        fontSize(1.5.em)
                        textAlign("center")
                        "ul" {
                            textAlign("start")
                        }
                        "pre" {
                            textAlign("left")
                        }
                        "a" {
                            color(Color("#E8441F"))
                            textDecoration("none")
                        }
                    }
                    attrs?.invoke(this)
                },
                overlayAttrs = {
                    style {
                        transitions {
                            "background-color" { duration = slideAnimationDuration.inWholeMilliseconds.ms }
                        }
                    }
                }
                ) {
                content()
                progress(Color("#651B20"))
            }
        }
    ) {
        +move
        +states
        +animations
        +sourceCode
        +modes
        +inner
        +Slide("conclusion") {
            H1 { Text("That's it!") }
            P {
                Text("Go compose amazing presentations with ")
                B { Text("ComposeUrPres") }
                Text(" !")
            }
        }
    }
}
