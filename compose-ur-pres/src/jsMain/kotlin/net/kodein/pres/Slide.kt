package net.kodein.pres

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

public interface SlideConfig

public class Slide(
    public val name: String? = null,
    public val stateCount: Int = 1,
    public val stateInOverview: Int? = null,
    public val width: Int = 1024,
    public val height: Int = 640,
    public val inAnimation: Animation.Set? = null,
    public val outAnimation: Animation.Set? = null,
    public val config: PresentationState.() -> Any? = { null },
    public val content: @Composable (Int) -> Unit
) {
    public val lastState: Int get() = stateCount - 1
}

public class PresentationSlidesBuilder internal constructor() {
    internal val slides = ArrayList<Slide>()

    public operator fun Slide.unaryPlus() {
        slides += this
    }

    public operator fun Iterable<Slide>.unaryPlus() {
        slides += this
    }
}

public fun buildSlides(build: PresentationSlidesBuilder.() -> Unit): List<Slide> =
    PresentationSlidesBuilder().apply(build).slides

@Composable
public fun rememberSlides(build: PresentationSlidesBuilder.() -> Unit): List<Slide> =
    remember { buildSlides(build) }
