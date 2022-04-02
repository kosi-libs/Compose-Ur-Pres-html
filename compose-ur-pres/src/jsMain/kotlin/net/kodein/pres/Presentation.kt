package net.kodein.pres

import androidx.compose.runtime.*
import kotlinx.browser.document
import kotlinx.browser.window
import net.kodein.pres.util.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposableInBody
import org.w3c.dom.BroadcastChannel
import org.w3c.dom.DOMRect
import kotlin.js.json
import kotlin.time.Duration.Companion.milliseconds

internal object PresStyle: StyleSheet(InHeadRulesHolder())

public fun presentationAppInBody(
    animation: Animation.Set = Animations.Move(600.milliseconds),
    enableRouter: Boolean = false,
    enableSync: Boolean = true,
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
                enableSync = enableSync,
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
    animation: Animation.Set = Animations.Move(600.milliseconds),
    enableRouter: Boolean = false,
    enableSync: Boolean = true,
    presentationContainer: Container = { presentationContainer(content = it) },
    slideContainer: Container = { slideContainer(content = it) }
) {
    var current by remember { mutableStateOf(SlideState(0, 0)) }
    var overview by remember { mutableStateOf(false) }
    var presenter by remember { mutableStateOf(false) }

    val currentSlide by rememberUpdatedState(slides.getOrNull(current.index))

    if (enableRouter) {
        var locationChecked by remember { mutableStateOf(false) }
        if (locationChecked) {
            LaunchedEffect(current.index, current.state, overview, presenter) {
                window.location.hash = buildString {
                    append(currentSlide?.name ?: current.index)
                    if (current.state > 0) {
                        append("/")
                        append(current.state)
                    }
                    if (overview || presenter) {
                        append("?")
                        append(listOfNotNull(
                            if (overview) "overview" else null,
                            if (presenter) "presenter" else null
                        ).joinToString("&"))
                    }
                }
            }
        }

        DisposableEffect(null) {
            val listener = FEventListener {
                val hash = window.location.hash.removePrefix("#")
                if (hash.isNotEmpty()) {
                    val array = window.location.hash.removePrefix("#").split("?")
                    val path = array[0].split("/")

                    val slideName = decodeURIComponent(path[0])
                    val slideIndex = slides.indexOfFirst { it.name == slideName } .takeIf { it >= 0 } ?: slideName.toIntOrNull()
                    if (slideIndex != null && slideIndex >= 0 && slideIndex <= slides.lastIndex) {
                        val slide = slides[slideIndex]
                        val slideState = path.getOrNull(1)?.toIntOrNull()?.takeIf { it >= 0 && it < slide.stateCount } ?: 0

                        current = SlideState(slideIndex, slideState)
                    }

                    val modes = (array.getOrNull(1)?.split("&") ?: emptyList())
                        .map { it.split("=", limit = 2) }
                        .associate { it[0] to it.getOrElse(1) { "" } }
                    overview = "overview" in modes
                    presenter = "presenter" in modes
                }
                locationChecked = true
            }
            listener()
            window.addEventListener("hashchange", listener)
            onDispose { window.removeEventListener("hashchange", listener) }
        }

        if (!locationChecked) return
    }

    if (enableSync) {
        val channel = remember { BroadcastChannel("pres-pos") }

        var noBroadcast by remember { mutableStateOf(false) }

        DisposableEffect(null) {
            channel.onmessage = { e ->
                val new = e.data.let { SlideState(it.asDynamic().i as Int, it.asDynamic().s as Int) }
                if (current != new) {
                    noBroadcast = true
                    current = new
                }
                Unit
            }
            onDispose {
                channel.onmessage = {}
            }
        }

        LaunchedEffect(current) {
            if (!noBroadcast) channel.postMessage(json("i" to current.index, "s" to current.state))
            noBroadcast = false
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
                lastMoveWasForward = true
                if (fast) {
                    if (current.index < slides.lastIndex) {
                        current = SlideState(current.index + 1, 0)
                    }
                } else {
                    current = current.next(slides)
                }
            }

            fun goPrev(fast: Boolean) {
                lastMoveWasForward = false
                if (fast) {
                    if (current.index > 0) {
                        current = SlideState(current.index - 1, 0)
                    }
                } else {
                    current = current.prev(slides)
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
                    KeyCodes.ofChar('p') -> presenter = !presenter
                    else -> {}
                }
            }

            it.onclick = onclick@ { e ->
                if (overview) return@onclick onDispose {}

                when(e.button) {
                    MouseButtonCodes.MAIN -> goNext(e.altKey)
                    else -> {}
                }
            }

            it.focus()
            onDispose {}
        }

        when {
            overview -> OverviewPresentation(
                presentationContainer = presentationContainer,
                slideContainer = slideContainer,
                slides = slides,
                defaultAnimation = animation,
                presentationSize = presentationSize,
                currentState = current,
                lastMoveWasForward = lastMoveWasForward,
                presenter = presenter
            )
            presenter -> PresenterPresentation(
                presentationContainer = presentationContainer,
                slideContainer = slideContainer,
                slides = slides,
                defaultAnimation = animation,
                presentationSize = presentationSize,
                currentState = current,
                lastMoveWasForward = lastMoveWasForward
            )
            else -> FullScreenPresentation(
                presentationContainer = presentationContainer,
                slideContainer = slideContainer,
                slides = slides,
                defaultAnimation = animation,
                presentationSize = presentationSize,
                currentState = current,
                lastMoveWasForward = lastMoveWasForward
            )
        }
    }
}
