package net.kodein.pres

import kotlinx.coroutines.delay
import net.kodein.pres.Animation.Direction
import net.kodein.pres.Animation.Direction.BACKWARD
import net.kodein.pres.Animation.Direction.FORWARD
import net.kodein.pres.util.Style
import net.kodein.pres.util.transition
import org.jetbrains.compose.web.css.*
import kotlin.time.Duration


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

    public class Fade(duration: Duration) : Animation.Set(In(duration), Out(duration)) {
        public class In(override val duration: Duration) : Animation.Appear {
            override fun prepare(direction: Direction): Style = Animation.Style {
                opacity(0.0)
            }
            override suspend fun execute(direction: Direction, setStyle: (Style) -> Unit) {
                setStyle {
                    transition {
                        "opacity"(duration.inWholeMilliseconds.ms)
                    }
                    opacity(1.0)
                }
                delay(duration)
            }
        }
        public class Out(override val duration: Duration) : Animation.Disappear {
            override suspend fun execute(direction: Direction, setStyle: (Style) -> Unit) {
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

    public class Move(duration: Duration) : Animation.Set(In(duration), Out(duration)) {
        public class In(override val duration: Duration) : Animation.Appear {
            override fun prepare(direction: Direction): Style = Animation.Style {
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
            override suspend fun execute(direction: Direction, setStyle: (Style) -> Unit) {
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
        public class Out(override val duration: Duration) : Animation.Disappear {
            override suspend fun execute(direction: Direction, setStyle: (Style) -> Unit) {
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

    public class Flip(duration: Duration) : Animation.Set(In(duration), Out(duration)) {
        public class In(override val duration: Duration) : Animation.Appear {
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
        public class Out(override val duration: Duration) : Animation.Disappear {
            override suspend fun execute(direction: Direction, setStyle: (Style) -> Unit) {
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
}
