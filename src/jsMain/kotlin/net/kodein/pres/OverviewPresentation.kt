package net.kodein.pres

import androidx.compose.runtime.*
import net.kodein.pres.util.transition
import net.kodein.pres.util.zIndex
import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLDivElement
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlin.time.Duration


@Composable
internal fun OverviewPresentation(
    slides: List<Slide>,
    defaultTransition: Transition.TimedSet,
    container: DOMRect?,
    currentState: PresentationState,
    containerAttrs: (AttrsBuilder<HTMLDivElement>.(Duration) -> Unit)? = null,
    overSlides: @Composable (PresentationState) -> Unit,
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
        if (container == null) return@Div

        (-3..3)
            .filter { currentState.slideIndex + it in slides.indices }
            .forEach { delta ->
                val slideIndex = currentState.slideIndex + delta
                val slide = slides[currentState.slideIndex + delta]
                val slideState = if (delta == 0 && moves <= 0 ) currentState.slideState else slide.stateInOverview ?: (slide.states - 1)


                key("overview-slide-$slideIndex") {
                    val slideFactor = min(container.width / slide.width, container.height / slide.height)
                    val slideFullWidth = slide.width * slideFactor
                    val slideFullHeight = slide.height * slideFactor

                    val wLoss = container.width - slideFullWidth
                    val hLoss = container.height - slideFullHeight

                    val miniSlideFactor = 0.8
                    val slideMarginFromScreenFactor = 0.018
                    val slideFromContainerFactor = (container.width / 5) / (slideFullWidth * miniSlideFactor)

                    val smallSlideHeight = slideFullHeight * slideFromContainerFactor
                    val smallSlideWidth = slideFullWidth * slideFromContainerFactor

                    val miniSlideMargin = (slideFullWidth - slideFullWidth * miniSlideFactor) / 2
                    val miniDelta = max(0, abs(delta) - 1) * delta.sign
                    val miniSlideWidth = smallSlideWidth * miniSlideFactor
                    val miniSlideHeight = smallSlideHeight * miniSlideFactor

                    H3({
                        classes(PresStyle.css {
                            height(2.cssRem)
                            position(Position.Absolute)
//                            transition {
//                                "top"(0.3.s)
//                                "left"(0.3.s)
//                                "transform"(0.3.s)
//                            }
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
                                        (container.height - smallSlideHeight) / 2
                                                + if (delta != 0) (smallSlideHeight - miniSlideHeight) / 2 else 0.0
                                        ).px
                                        - 2.cssRem
                            )
                            property("left", (
                                    (container.width - smallSlideWidth) / 2
                                            + smallSlideWidth * delta
                                            - (smallSlideWidth - miniSlideWidth) / 2 * (delta + miniDelta)
                                            + container.width * slideMarginFromScreenFactor * delta
                                    ).px)
                            if (delta != 0) transform { scale(miniSlideFactor) }
                        }
                    }) {
                        Text(slide.name ?: slideIndex.toString())
                    }

                    Div({
                        classes(PresStyle.css {
                            position(Position.Absolute)
//                            transition {
//                                "left"(0.3.s)
//                                "transform"(0.3.s)
//                            }
                            property("box-shadow", "0px 0px 2rem 0rem black")
                        })
                        style {
                            width(slideFullWidth.px)
                            height(slideFullHeight.px)
                            overflow("hidden")
                            left((
                                wLoss / 2
                                + smallSlideWidth * delta
                                - miniSlideMargin * slideFromContainerFactor * (delta + miniDelta)
                                + container.width * slideMarginFromScreenFactor * delta
                            ).px)
                            top((hLoss / 2).px)
                            zIndex(if (delta == 0) 2 else 1)
                            transform {
                                scale(if (delta == 0) slideFromContainerFactor else slideFromContainerFactor * miniSlideFactor)
                            }
                        }
                    }) {
                        Div({
                            classes(PresStyle.css {
                                position(Position.Absolute)
                            })
                            style {
                                width(container.width.px)
                                height(container.height.px)
                                left((-wLoss / 2).px)
                                top((-hLoss / 2).px)
                            }
                            containerAttrs?.invoke(this, Duration.ZERO)
                        }) {
                            Div({
                                classes(PresStyle.css {
                                    position(Position.Absolute)
                                })
                                style {
                                    width(container.width.px)
                                    height(container.height.px)
                                    left(0.px)
                                    top(0.px)
                                }
                                slide.containerAttrs?.invoke(this)
                            }) {
                                SlideContainer(
                                    slide = slide,
                                    state = slideState,
                                    container = container,
                                    position = SlidePosition.CURRENT,
                                    defaultTransition = defaultTransition,
                                    compose = true
                                )
                            }
                        }
                        overSlides(currentState.copy(
                            slideIndex = slideIndex,
                            slideState = slideState,
                            slideStateCount = slide.states,
                            globalState = slides.subList(0, slideIndex).sumOf { it.states } + slideState
                        ))
                    }

                }
            }

    }
}