package net.kodein.pres

import kotlinx.coroutines.delay
import net.kodein.pres.Transition.Direction
import net.kodein.pres.Transition.Direction.BACKWARD
import net.kodein.pres.Transition.Direction.FORWARD
import net.kodein.pres.util.TimingFunction
import net.kodein.pres.util.transition
import org.jetbrains.compose.web.css.*
import kotlin.time.Duration


typealias TransitionStyle = StyleBuilder.() -> Unit

sealed interface Transition {
    enum class Direction { FORWARD, BACKWARD }

    sealed interface Appear : Transition {
        fun prepare(direction: Direction): TransitionStyle
    }

    sealed interface Disappear : Transition

    suspend fun execute(duration: Duration, direction: Direction, setStyle: (TransitionStyle) -> Unit)

    open class Set(val appear: Appear, val disappear: Disappear)

    class TimedSet(val transitions: Set, val duration: Duration)

    companion object {
        fun Style(style: TransitionStyle) = style
        fun combineStyles(first: TransitionStyle, second: TransitionStyle) = Style {
            first.invoke(this)
            second.invoke(this)
        }
    }
}

operator fun TransitionStyle.plus(second: TransitionStyle) = Transition.combineStyles(this, second)

fun Transition.Set.timed(duration: Duration) = Transition.TimedSet(this, duration)

object Fade : Transition.Set(In, Out) {
    object In : Transition.Appear {
        override fun prepare(direction: Direction) = Transition.Style {
            opacity(0.0)
        }
        override suspend fun execute(duration: Duration, direction: Direction, setStyle: (TransitionStyle) -> Unit) {
            setStyle {
                transition {
                    "opacity"(duration.inWholeMilliseconds.ms)
                }
                opacity(1.0)
            }
            delay(duration)
        }
    }
    object Out : Transition.Disappear {
        override suspend fun execute(duration: Duration, direction: Direction, setStyle: (TransitionStyle) -> Unit) {
            setStyle {
                transition {
                    "opacity"(duration.inWholeMilliseconds.ms)
                }
                opacity(0.0)
            }
            delay(duration)
        }
    }
}

object Move : Transition.Set(In, Out) {
    object In : Transition.Appear {
        override fun prepare(direction: Direction) = Transition.Style {
            opacity(0.0)
            transform {
                translateX(
                    when (direction) {
                        FORWARD -> 50.percent
                        BACKWARD -> (-50).percent
                    }
                )
            }
        }
        override suspend fun execute(duration: Duration, direction: Direction, setStyle: (TransitionStyle) -> Unit) {
            setStyle {
                transition {
                    "transform"(duration.inWholeMilliseconds.ms)
                    "opacity"(duration.inWholeMilliseconds.ms)
                }
                transform {
                    translateX(0.percent)
                }
                opacity(1.0)
            }
            delay(duration)
        }
    }
    object Out : Transition.Disappear {
        override suspend fun execute(duration: Duration, direction: Direction, setStyle: (TransitionStyle) -> Unit) {
            setStyle {
                transition {
                    "transform"(duration.inWholeMilliseconds.ms)
                    "opacity"(duration.inWholeMilliseconds.ms)
                }
                transform {
                    translateX(
                        when (direction) {
                            FORWARD -> (-50).percent
                            BACKWARD -> 50.percent
                        }
                    )
                }
                opacity(0.0)
            }
            delay(duration)
        }
    }
}

object Flip : Transition.Set(In, Out) {
    object In : Transition.Appear {
        override fun prepare(direction: Direction) = Transition.Style {
            opacity(0.0)
            transform {
                perspective(90.em)
                rotateY(
                    when (direction) {
                        FORWARD -> 180.deg
                        BACKWARD -> (-180).deg
                    }
                )
            }
        }
        override suspend fun execute(duration: Duration, direction: Direction, setStyle: (TransitionStyle) -> Unit) {
            setStyle {
                transition {
                    "transform"(duration.inWholeMilliseconds.ms)
                    "opacity"(duration.inWholeMilliseconds.ms)
                }
                transform {
                    perspective(90.em)
                }
                opacity(1.0)
            }
            delay(duration)
        }
    }
    object Out : Transition.Disappear {
        override suspend fun execute(duration: Duration, direction: Direction, setStyle: (TransitionStyle) -> Unit) {
            setStyle {
                transition {
                    "transform"(duration.inWholeMilliseconds.ms)
                    "opacity"(duration.inWholeMilliseconds.ms)
                }
                transform {
                    perspective(90.em)
                    rotateY(
                        when (direction) {
                            FORWARD -> (-180).deg
                            BACKWARD -> 180.deg
                        }
                    )
                }
                opacity(0.0)
            }
            delay(duration)
        }
    }
}
