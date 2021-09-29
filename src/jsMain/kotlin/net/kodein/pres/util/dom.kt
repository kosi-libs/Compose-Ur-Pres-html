package net.kodein.pres.util

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener


fun FEventListener(handler: () -> Unit) = FEventListenerHandler(handler)

class FEventListenerHandler(private val handler: () -> Unit) : EventListener {
    operator fun invoke() { handler() }
    override fun handleEvent(event: Event) { handler() }
    override fun toString(): String = "EventListenerHandler($handler)"
}

object KeyCodes {
    const val ENTER = 13
    const val ESCAPE = 27
    const val LEFT = 37
    const val UP = 38
    const val RIGHT = 39
    const val DOWN = 40
    const val SPACE = 32
}