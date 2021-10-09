package net.kodein.pres

import androidx.compose.runtime.*
import net.kodein.pres.util.zIndex
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.DOMRect
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlin.time.Duration


@Composable
internal fun OverviewPresentation(
    presentationContainer: Container,
    slideContainer: Container,
    slides: List<Slide>,
    defaultAnimation: Animation.Set,
    presentationSize: DOMRect?,
    currentState: PresentationState
) {
    var moves by remember { mutableStateOf(-1) }
    LaunchedEffect(currentState.slideIndex) { moves += 1 }

    Div({
        classes(PresStyle.css {
            width(100.percent)
            height(100.percent)
            position(Position.Absolute)
            top(0.px)
            left(0.px)
            overflow("hidden")
            backgroundColor(Color.silver)
        })
    }) {
        if (presentationSize == null) return@Div

        (-3..3)
            .filter { currentState.slideIndex + it in slides.indices }
            .forEach { delta ->
                val slideIndex = currentState.slideIndex + delta
                val slide = slides[currentState.slideIndex + delta]
                val slideState = if (delta == 0 && moves <= 0 ) currentState.slideState else slide.stateInOverview ?: slide.lastState


                key("overview-slide-$slideIndex") {
                    val slideFactor = min(presentationSize.width / slide.width, presentationSize.height / slide.height)
                    val slideFullWidth = slide.width * slideFactor
                    val slideFullHeight = slide.height * slideFactor

                    val wLoss = presentationSize.width - slideFullWidth
                    val hLoss = presentationSize.height - slideFullHeight

                    val miniSlideFactor = 0.8
                    val slideMarginFromScreenFactor = 0.018
                    val slideFromPresentationFactor = (presentationSize.width / 5) / (slideFullWidth * miniSlideFactor)

                    val smallSlideHeight = slideFullHeight * slideFromPresentationFactor
                    val smallSlideWidth = slideFullWidth * slideFromPresentationFactor

                    val miniSlideMargin = (slideFullWidth - slideFullWidth * miniSlideFactor) / 2
                    val miniDelta = max(0, abs(delta) - 1) * delta.sign
                    val miniSlideWidth = smallSlideWidth * miniSlideFactor
                    val miniSlideHeight = smallSlideHeight * miniSlideFactor

                    H3({
                        classes(PresStyle.css {
                            height(2.cssRem)
                            position(Position.Absolute)
                            property("transform-origin", "bottom center")
                            padding(0.px)
                            margin(0.px)
                            whiteSpace("nowrap")
                            overflow("hidden")
                            property("text-overflow", "ellipsis")
                            fontFamily("sans-serif")
                            color(Color.black)
                            textAlign("center")
                        })
                        style {
                            width(smallSlideWidth.px)
                            property("top",
                                (
                                        (presentationSize.height - smallSlideHeight) / 2
                                                + if (delta != 0) (smallSlideHeight - miniSlideHeight) / 2 else 0.0
                                        ).px
                                        - 2.cssRem
                            )
                            property("left", (
                                    (presentationSize.width - smallSlideWidth) / 2
                                            + smallSlideWidth * delta
                                            - (smallSlideWidth - miniSlideWidth) / 2 * (delta + miniDelta)
                                            + presentationSize.width * slideMarginFromScreenFactor * delta
                                    ).px)
                            if (delta != 0) transform { scale(miniSlideFactor) }
                        }
                    }) {
                        Text(slide.name ?: slideIndex.toString())
                    }

                    Div({
                        classes(PresStyle.css {
                            position(Position.Absolute)
                            property("box-shadow", "0px 0px 2rem 0rem black")
                        })
                        style {
                            width(slideFullWidth.px)
                            height(slideFullHeight.px)
                            overflow("hidden")
                            left((
                                wLoss / 2
                                + smallSlideWidth * delta
                                - miniSlideMargin * slideFromPresentationFactor * (delta + miniDelta)
                                + presentationSize.width * slideMarginFromScreenFactor * delta
                            ).px)
                            top((hLoss / 2).px)
                            zIndex(if (delta == 0) 2 else 1)
                            transform {
                                scale(if (delta == 0) slideFromPresentationFactor else slideFromPresentationFactor * miniSlideFactor)
                            }
                        }
                    }) {
                        Div({
                            classes(PresStyle.css {
                                position(Position.Absolute)
                            })
                            style {
                                width(presentationSize.width.px)
                                height(presentationSize.height.px)
                                left((-wLoss / 2).px)
                                top((-hLoss / 2).px)
                            }
                        }) {
                            currentState
                                .copy(
                                    slideIndex = slideIndex,
                                    slideState = slideState,
                                    slideStateCount = slide.stateCount,
                                    globalState = slides.subList(0, slideIndex).sumOf { it.stateCount } + slideState,
                                    slideAnimationDuration = Duration.ZERO,
                                    slideConfig = null,
                                )
                                .let {
                                    it.copy(slideConfig = slide.config.invoke(it))
                                }
                                .presentationContainer {
                                    SlideHandler(
                                        currentState = currentState,
                                        slideContainer = slideContainer,
                                        slide = slide,
                                        state = slideState,
                                        presentationSize = presentationSize,
                                        position = SlidePosition.CURRENT,
                                        defaultAnimation = defaultAnimation,
                                        compose = true
                                    )
                            }
                        }
                    }
                }
            }
    }
}
