package net.kodein.pres

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.w3c.dom.HTMLDivElement
import kotlin.time.Duration

class Slide(
    val name: String? = null,
    val states: Int = 1,
    val stateInOverview: Int? = null,
    val width: Int = 1024,
    val height: Int = 640,
    val inTransition: Transition.TimedSet? = null,
    val outTransition: Transition.TimedSet? = null,
    val containerAttrs: AttrBuilderContext<HTMLDivElement>? = null,
    val content: @Composable (Int) -> Unit
)