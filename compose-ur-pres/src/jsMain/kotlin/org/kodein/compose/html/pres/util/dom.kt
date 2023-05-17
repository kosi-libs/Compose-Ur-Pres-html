package org.kodein.compose.html.pres.util

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener


internal class FEventListener(private val handler: () -> Unit) : EventListener {
    operator fun invoke() { handler() }
    override fun handleEvent(event: Event) { handler() }
    override fun toString(): String = "EventListenerHandler($handler)"
}

internal object KeyCodes {
    const val BACKSPACE = 8
    const val ENTER = 13
    const val ESCAPE = 27
    const val LEFT = 37
    const val UP = 38
    const val PAGE_UP = 33
    const val RIGHT = 39
    const val DOWN = 40
    const val PAGE_DOWN = 34
    const val SPACE = 32

    fun ofChar(c: Char) = c.uppercaseChar().code
}

internal object MouseButtonCodes {
    const val MAIN: Short = 0
}