package net.kodein.pres

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.w3c.dom.HTMLDivElement
import kotlin.time.Duration

interface SlideConfig

class Slide(
    val name: String? = null,
    val states: Int = 1,
    val stateInOverview: Int? = null,
    val width: Int = 1024,
    val height: Int = 640,
    val inTransition: Transition.TimedSet? = null,
    val outTransition: Transition.TimedSet? = null,
    val config: Any? = null,
    val content: @Composable (Int) -> Unit
)

class PresentationSlides internal constructor() {
    internal val slides = ArrayList<Slide>()

    operator fun Slide.unaryPlus() {
        slides += this
    }
}

@Composable
fun rememberSlides(build: PresentationSlides.() -> Unit): List<Slide> =
    remember { PresentationSlides().apply(build) }.slides
