import androidx.compose.runtime.*
import net.kodein.pres.*
import net.kodein.pres.util.InHeadRulesHolder
import net.kodein.pres.util.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration

object AppStyle: StyleSheet(InHeadRulesHolder())

@Composable
fun FakeSlideContent(index: Int, state: Int) {
    Div({
        classes(AppStyle.css {
            width(100.percent)
            height(100.percent)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)
        })
    }) {
        H1({
            style { paddingTop(index.em) }
        }) {
            Text("This is Slide $index, state $state")
        }
        H1 {
            Text("..")
        }
    }
}

fun main() = presentationAppInBody(
    enableRouter = true,
    container = { content ->
        overlayedPresentationContainer({
            classes(AppStyle.css {
                backgroundColor(Color("#240821"))
                backgroundImage("url('logo-bg.svg')")
                backgroundRepeat("no-repeat")
                backgroundPosition("right 0 bottom -15rem")
                backgroundSize("contain")
                color(Color.white)
            })
        }) {
            content()
            progress(Color("#651B20"))
        }
    }
) {
    +Slide(
        name = "First"
    ) {
        FakeSlideContent(1, it)
    }
    +Slide(
        name = "Second",
        states = 2,
        outTransition = Flip.timed(Duration.seconds(2))
    ) {
        FakeSlideContent(2, it)
    }
    +Slide(
        name = "Third",
        states = 3,
        inTransition = Flip.timed(Duration.seconds(2)),
        config = OverlayAttrs {
            classes(AppStyle.css {
                backgroundColor(Color.darkblue)
            })
        }
    ) {
        FakeSlideContent(3, it)
    }
    +Slide(
        name = "Fourth",
        states = 4,
    ) {
        FakeSlideContent(4, it)
    }
    +Slide(
        name = "Fifth",
        states = 5,
    ) {
        FakeSlideContent(5, it)
    }
    +Slide(
        name = "Sixth",
        states = 6,
    ) {
        FakeSlideContent(6, it)
    }
    +Slide(
        name = "Seventh",
        states = 7,
    ) {
        FakeSlideContent(7, it)
    }
    +Slide(
        name = "Eighth",
        states = 8,
    ) {
        FakeSlideContent(8, it)
    }

}