package org.kodein.compose.html.pres

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.w3c.dom.DOMRect

public interface SlideConfig

public data class Slide(
    public val name: String? = null,
    public val stateCount: Int = 1,
    public val stateInOverview: Int? = null,
    public val width: Int = 1024,
    public val height: Int = 640,
    public val inAnimation: Animation.Set? = null,
    public val outAnimation: Animation.Set? = null,
    public val config: PresentationState.() -> Any? = { null },
    public val notes: @Composable (Int) -> Unit = {},
    public val content: @Composable (Int) -> Unit
) {
    public val lastState: Int get() = stateCount - 1
    public val rect: DOMRect = DOMRect(width = width.toDouble(), height = height.toDouble())
}

public class SlidesBuilder internal constructor() {
    private val slides = ArrayList<Slide>()

    public operator fun Slide.unaryPlus() {
        slides += this
    }

    public operator fun Slides.unaryPlus() {
        this@SlidesBuilder.slides += this@unaryPlus.slides
    }

    internal fun animatedWith(animation: Animation.Set?) =
        if (animation == null) slides
        else slides.mapIndexed { index, slide ->
            slide.copy(
                inAnimation = if (index == 0 || slide.inAnimation != null) slide.inAnimation else animation,
                outAnimation = if (index == slides.lastIndex || slide.outAnimation != null) slide.outAnimation else animation
            )
        }

}

public class Slides private constructor(
    public val slides: List<Slide>,
) {
    public constructor(animation: Animation.Set? = null, build: SlidesBuilder.() -> Unit)
        : this(SlidesBuilder().apply(build).animatedWith(animation))
}

@Composable
public fun rememberSlides(build: SlidesBuilder.() -> Unit): List<Slide> =
    remember { Slides(build = build).slides }
