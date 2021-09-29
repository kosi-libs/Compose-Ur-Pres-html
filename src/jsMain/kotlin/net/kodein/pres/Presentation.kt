package net.kodein.pres

import androidx.compose.runtime.*
import kotlinx.browser.window
import net.kodein.pres.util.*
import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposableInBody
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLDivElement
import kotlin.time.Duration

class PresentationSlides internal constructor() {
    internal val slides = ArrayList<Slide>()

    operator fun Slide.unaryPlus() {
        slides += this
    }
}

@Composable
fun rememberSlides(build: PresentationSlides.() -> Unit): List<Slide> =
    remember { PresentationSlides().apply(build) }.slides

internal object PresStyle: StyleSheet(InHeadRulesHolder())

data class PresentationState(
    val slideIndex: Int,
    val slideState: Int,
    val slideStateCount: Int,
    val globalSlideCount: Int,
    val globalState: Int,
    val globalStateCount: Int
)

fun presentationAppInBody(
    transition: Transition.TimedSet = Move.timed(Duration.milliseconds(600)),
    enableRouter: Boolean = false,
    containerAttrs: (AttrsBuilder<HTMLDivElement>.(Duration) -> Unit)? = null,
    overSlides: @Composable (PresentationState) -> Unit = {},
    slides: PresentationSlides.() -> Unit
) {
    setBodyNoMargin()

    renderComposableInBody {
        // The presentation must be rendered into its own div and will take 100% of its size
        Div({
            classes(AppStyle.css {
                width(100.vw)
                height(100.vh)
            })
        }) {
            Presentation(
                slides = rememberSlides { slides() },
                transition = transition,
                enableRouter = enableRouter,
                containerAttrs = containerAttrs,
                overSlides = overSlides
            )
        }
    }
}

@Composable
fun Presentation(
    slides: List<Slide>,
    transition: Transition.TimedSet = Move.timed(Duration.milliseconds(600)),
    enableRouter: Boolean = false,
    containerAttrs: (AttrsBuilder<HTMLDivElement>.(Duration) -> Unit)? = null,
    overSlides: @Composable (PresentationState) -> Unit = {}
) {
    var currentSlideIndex by remember { mutableStateOf(0) }
    var currentSlideState by remember { mutableStateOf(0) }
    var overview by remember { mutableStateOf(false) }

    val currentSlide by rememberUpdatedState(slides.getOrNull(currentSlideIndex))

    if (enableRouter) {
        var locationChecked by remember { mutableStateOf(false) }
        if (locationChecked) {
            LaunchedEffect(currentSlideIndex, currentSlideState, overview) {
                window.location.hash = buildString {
                    append(currentSlide?.name ?: currentSlideIndex)
                    if (currentSlideState > 0) {
                        append("/")
                        append(currentSlideState)
                    }
                    if (overview) {
                        append("?overview")
                    }
                }
            }
        }

        DisposableEffect(null) {
            val listener = FEventListener l@ {
                val hash = window.location.hash.removePrefix("#")
                if (hash.isNotEmpty()) {
                    val array = window.location.hash.removePrefix("#").split("?")
                    val path = array[0].split("/")

                    val slideName = decodeURIComponent(path[0])
                    val slideIndex = slides.indexOfFirst { it.name == slideName } .takeIf { it >= 0 } ?: slideName.toIntOrNull()
                    if (slideIndex != null && slideIndex >= 0 && slideIndex < slides.lastIndex) {
                        val slide = slides[slideIndex]
                        val slideState = path.getOrNull(1)?.toIntOrNull()?.takeIf { it >= 0 && it < slide.states } ?: 0

                        currentSlideIndex = slideIndex
                        currentSlideState = slideState
                    }

                    val modes = array.getOrNull(1)?.split(",") ?: emptyList()
                    overview = "overview" in modes
                }
                locationChecked = true
            }
            listener()
            window.addEventListener("hashchange", listener)
            onDispose { window.removeEventListener("hashchange", listener) }
        }
    }

    Div({
        classes(PresStyle.css {
            width(100.percent)
            height(100.percent)
            position(Position.Relative)
            outline("none")
            overflow("hidden")
        })
        tabIndex(0)
    }) {
        var size: DOMRect? by remember { mutableStateOf(null) }

        DisposableRefEffect {
            val listener = FEventListener {
                size = DOMRect(0.0, 0.0, it.offsetWidth.toDouble(), it.offsetHeight.toDouble())
            }
            window.addEventListener("resize", listener)
            listener()
            window.setTimeout({
                listener()
            }, 10)
            onDispose { window.removeEventListener("resize", listener) }
        }

        var lastMoveWasForward by remember { mutableStateOf(true) }

        DisposableRefEffect {
            fun goNext(fast: Boolean) {
                when {
                    fast && currentSlideIndex < slides.lastIndex -> {
                        currentSlideIndex += 1
                        currentSlideState = 0
                        lastMoveWasForward = true
                    }
                    !fast && currentSlide != null && currentSlideState < (currentSlide!!.states - 1) -> currentSlideState += 1
                    currentSlideIndex < slides.lastIndex -> {
                        currentSlideIndex += 1
                        currentSlideState = 0
                        lastMoveWasForward = true
                    }
                }
            }

            fun goPrev(fast: Boolean) {
                when {
                    fast && currentSlideIndex > 0 -> {
                        currentSlideIndex -= 1
                        currentSlideState = 0
                        lastMoveWasForward = false
                    }
                    !fast && currentSlideState > 0 -> currentSlideState -= 1
                    currentSlideIndex > 0 -> {
                        currentSlideIndex -= 1
                        currentSlideState = slides[currentSlideIndex].states - 1
                        lastMoveWasForward = false
                    }
                }
            }

            it.onkeydown = { e ->
                when (e.keyCode) {
                    KeyCodes.RIGHT, KeyCodes.DOWN, KeyCodes.SPACE -> goNext(overview || e.altKey)
                    KeyCodes.LEFT, KeyCodes.UP -> goPrev(overview || e.altKey)
                    KeyCodes.ESCAPE -> overview = !overview
                    KeyCodes.ENTER -> when {
                        overview -> overview = false
                        else -> goNext(e.altKey)
                    }
                }
            }
            it.focus()
            onDispose {}
        }

        val currentState = PresentationState(
            slideIndex = currentSlideIndex,
            slideState = currentSlideState,
            slideStateCount = currentSlide?.states ?: 0,
            globalSlideCount = slides.size,
            globalState = slides.subList(0, currentSlideIndex).sumOf { it.states } + currentSlideState,
            globalStateCount = slides.sumOf { it.states }
        )

        if (overview) {
            OverviewPresentation(
                slides = slides,
                defaultTransition = transition,
                container = size,
                currentState = currentState,
                containerAttrs = containerAttrs,
                overSlides = overSlides
            )
        }
        if (!overview) {
            FullScreenPresentation(
                slides = slides,
                defaultTransition = transition,
                size = size,
                lastMoveWasForward = lastMoveWasForward,
                currentState = currentState,
                containerAttrs = containerAttrs,
                overSlides = overSlides
            )
        }
    }
}

