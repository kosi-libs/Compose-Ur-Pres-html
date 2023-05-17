package org.kodein.compose.html.pres

import kotlinx.coroutines.delay
import org.kodein.compose.html.pres.Animation.Direction
import org.kodein.compose.html.pres.Animation.Direction.BACKWARD
import org.kodein.compose.html.pres.Animation.Direction.FORWARD
import org.kodein.compose.html.pres.util.Style
import org.jetbrains.compose.web.css.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


public sealed interface Animation {
    public val duration: Duration

    public enum class Direction { FORWARD, BACKWARD }

    public sealed interface Appear : Animation {
        public fun prepare(direction: Direction): Style
    }

    public sealed interface Disappear : Animation

    public suspend fun execute(direction: Direction, setStyle: (Style) -> Unit)

    public open class Set(public val appear: Appear, public val disappear: Disappear)

    public companion object {
        public fun Style(style: Style): Style = style
        public fun combineStyles(first: Style, second: Style): Style = Style {
            first.invoke(this)
            second.invoke(this)
        }
    }
}

public operator fun Style.plus(second: Style): Style = Animation.combineStyles(this, second)

public object Animations {

    public class Fade(duration: Duration = 0.5.seconds) : Animation.Set(In(duration), Out(duration)) {
        public class In(override val duration: Duration = 0.6.seconds) : Animation.Appear {
            override fun prepare(direction: Direction): Style = Animation.Style {
                opacity(0.0)
            }
            override suspend fun execute(direction: Direction, setStyle: (Style) -> Unit) {
                setStyle {
                    transitions {
                        "opacity" { duration = this@In.duration.inWholeMilliseconds.ms }
                    }
                    opacity(1.0)
                }
                delay(duration)
            }
        }
        public class Out(override val duration: Duration = 0.5.seconds) : Animation.Disappear {
            override suspend fun execute(direction: Direction, setStyle: (Style) -> Unit) {
                setStyle {
                    transitions {
                        "opacity" { duration = this@Out.duration.inWholeMilliseconds.ms }
                    }
                    opacity(0.0)
                }
                delay(duration)
            }
        }
    }

    public class Move(duration: Duration = 0.6.seconds, towards: Towards = Towards.Right) : Animation.Set(In(duration, towards), Out(duration, towards)) {
        public enum class Towards { Right, Bottom }

        public class In(override val duration: Duration = 0.6.seconds, private val towards: Towards = Towards.Right) :
            Animation.Appear {
            override fun prepare(direction: Direction): Style = Animation.Style {
                opacity(0.0)
                transform {
                    val value = when (direction) {
                        FORWARD -> 50.percent
                        BACKWARD -> (-50).percent
                    }
                    when (towards) {
                        Towards.Right -> translateX(value)
                        Towards.Bottom -> translateY(value)
                    }
                }
            }
            override suspend fun execute(direction: Direction, setStyle: (Style) -> Unit) {
                setStyle {
                    transitions {
                        "transform" { duration = this@In.duration.inWholeMilliseconds.ms }
                        "opacity" { duration = this@In.duration.inWholeMilliseconds.ms }
                    }
                    transform {
                        translateX(0.percent)
                        translateY(0.percent)
                    }
                    opacity(1.0)
                }
                delay(duration)
            }
        }
        public class Out(override val duration: Duration = 0.6.seconds, private val towards: Towards = Towards.Right) :
            Animation.Disappear {
            override suspend fun execute(direction: Direction, setStyle: (Style) -> Unit) {
                setStyle {
                    transitions {
                        "transform" { duration = this@Out.duration.inWholeMilliseconds.ms }
                        "opacity" { duration = this@Out.duration.inWholeMilliseconds.ms }
                    }
                    transform {
                        val value = when (direction) {
                            FORWARD -> (-50).percent
                            BACKWARD -> 50.percent
                        }
                        when (towards) {
                            Towards.Right -> translateX(value)
                            Towards.Bottom -> translateY(value)
                        }
                    }
                    opacity(0.0)
                }
                delay(duration)
            }
        }
    }

    public class Flip(duration: Duration = 1.seconds) : Animation.Set(In(duration), Out(duration)) {
        public class In(override val duration: Duration = 1.seconds) : Animation.Appear {
            override fun prepare(direction: Direction): Style = Animation.Style {
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
            override suspend fun execute(direction: Direction, setStyle: (Style) -> Unit) {
                setStyle {
                    transitions {
                        "transform" { duration = this@In.duration.inWholeMilliseconds.ms }
                        "opacity" {
                            duration = this@In.duration.inWholeMilliseconds.ms
                            timingFunction = AnimationTimingFunction.EaseInOut
                        }
                    }
                    transform {
                        perspective(90.em)
                    }
                    opacity(1.0)
                }
                delay(duration)
            }
        }
        public class Out(override val duration: Duration = 1.seconds) : Animation.Disappear {
            override suspend fun execute(direction: Direction, setStyle: (Style) -> Unit) {
                setStyle {
                    transitions {
                        "transform" { duration = this@Out.duration.inWholeMilliseconds.ms }
                        "opacity" { duration = this@Out.duration.inWholeMilliseconds.ms }
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
}
