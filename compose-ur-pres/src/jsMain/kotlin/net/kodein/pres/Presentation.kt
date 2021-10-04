package net.kodein.pres

import androidx.compose.runtime.*
import kotlinx.browser.document
import kotlinx.browser.window
import net.kodein.pres.util.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposableInBody
import org.w3c.dom.DOMRect
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal object PresStyle: StyleSheet(InHeadRulesHolder())

public data class PresentationState(
    val slideIndex: Int,
    val slideState: Int,
    val slideStateCount: Int,
    val globalSlideCount: Int,
    val globalState: Int,
    val globalStateCount: Int,
    val slideAnimationDuration: Duration,
    val slideConfig: Any?
)

public fun presentationAppInBody(
    animation: Animation.Set = Animations.Move(milliseconds(600)),
    enableRouter: Boolean = false,
    presentationContainer: Container = { presentationContainer(content = it) },
    slideContainer: Container = { slideContainer(content = it) },
    slides: PresentationSlidesBuilder.() -> Unit
) {
    document.body!!.style.margin = "0"

    renderComposableInBody {
        // The presentation must be rendered into its own div and will take 100% of its size
        Div({
            classes(PresStyle.css {
                width(100.vw)
                height(100.vh)
            })
        }) {
            Presentation(
                slides = rememberSlides { slides() },
                animation = animation,
                enableRouter = enableRouter,
                presentationContainer = presentationContainer,
                slideContainer = slideContainer
            )
        }
    }
}

public typealias Container = @Composable PresentationState.(@Composable () -> Unit) -> Unit

@Composable
public fun Presentation(
    slides: List<Slide>,
    animation: Animation.Set = Animations.Move(milliseconds(600)),
    enableRouter: Boolean = false,
    presentationContainer: Container = { presentationContainer(content = it) },
    slideContainer: Container = { slideContainer(content = it) }
) {
    var currentSlideIndex by remember { mutableStateOf(0) }
    var currentSlideState by remember { mutableStateOf(0) }
    var overview by remember { mutableStateOf(false) }
    var presenter: String? by remember { mutableStateOf(null) }

    val currentSlide by rememberUpdatedState(slides.getOrNull(currentSlideIndex))

    if (enableRouter) {
        var locationChecked by remember { mutableStateOf(false) }
        if (locationChecked) {
            LaunchedEffect(currentSlideIndex, currentSlideState, overview, presenter) {
                window.location.hash = buildString {
                    append(currentSlide?.name ?: currentSlideIndex)
                    if (currentSlideState > 0) {
                        append("/")
                        append(currentSlideState)
                    }
                    if (overview || presenter != null) {
                        append("?")
                        append(listOfNotNull(
                            if (overview) "overview" else null,
                            if (presenter != null) "presenter=$presenter" else null
                        ).joinToString("&"))
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
                    if (slideIndex != null && slideIndex >= 0 && slideIndex <= slides.lastIndex) {
                        val slide = slides[slideIndex]
                        val slideState = path.getOrNull(1)?.toIntOrNull()?.takeIf { it >= 0 && it < slide.states } ?: 0

                        currentSlideIndex = slideIndex
                        currentSlideState = slideState
                    }

                    val modes = (array.getOrNull(1)?.split("&") ?: emptyList())
                        .map { it.split("=", limit = 2) }
                        .associate { it[0] to it.getOrElse(1) { "" } }
                    overview = "overview" in modes
                    presenter = modes["presenter"]
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
        var presentationSize: DOMRect? by remember { mutableStateOf(null) }

        DisposableRefEffect {
            val listener = FEventListener {
                presentationSize = DOMRect(0.0, 0.0, it.offsetWidth.toDouble(), it.offsetHeight.toDouble())
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
                    KeyCodes.LEFT, KeyCodes.UP, KeyCodes.BACKSPACE -> goPrev(overview || e.altKey)
                    KeyCodes.ESCAPE -> overview = !overview
                    KeyCodes.ENTER -> when {
                        overview -> overview = false
                        else -> goNext(e.altKey)
                    }
                    KeyCodes.ofChar('p') -> {
                        presenter = when (presenter) {
                            null -> "with-notes"
                            "with-notes" -> "big"
                            "big" -> null
                            else -> null
                        }
                    }
                    else -> {}
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
            globalStateCount = slides.sumOf { it.states },
            slideAnimationDuration = (
                    if (lastMoveWasForward) (currentSlide?.inAnimation ?: animation).appear.duration
                    else (currentSlide?.outAnimation ?: animation).disappear.duration
            ),
            slideConfig = currentSlide?.config
        )

        if (overview) {
            OverviewPresentation(
                presentationContainer = presentationContainer,
                slideContainer = slideContainer,
                slides = slides,
                defaultAnimation = animation,
                presentationSize = presentationSize,
                currentState = currentState
            )
        }
        if (!overview) {
            FullScreenPresentation(
                presentationContainer = presentationContainer,
                slideContainer = slideContainer,
                slides = slides,
                defaultAnimation = animation,
                presentationSize = presentationSize,
                currentState = currentState
            )
        }
    }
}

