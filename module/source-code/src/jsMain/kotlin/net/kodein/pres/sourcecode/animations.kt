package net.kodein.pres.sourcecode

import net.kodein.pres.Transition
import net.kodein.pres.Transitions.fontGrow
import net.kodein.pres.hiddenIf
import net.kodein.pres.shownIf
import org.jetbrains.compose.web.css.*


public fun SegmentAnimationBuilder.hiddenIf(condition: Boolean, transition: Transition) {
    attrs { hiddenIf(condition, transition) }
}

public fun SegmentAnimationBuilder.shownIf(condition: Boolean, transition: Transition) {
    attrs { shownIf(condition, transition) }
}

public fun SegmentAnimationBuilder.fontGrow(condition: Boolean): Unit = shownIf(condition, fontGrow)

public fun SegmentAnimationBuilder.zoomed(condition: Boolean, scale: Double = 1.25) {
    unDimmed(condition)
    attrs {
        style {
            display(DisplayStyle.InlineBlock)
            transitions { "transform" { duration = 0.3.s } }
            if (condition) transform { scale(scale) }
        }
    }
}

public fun SegmentAnimationBuilder.lineHeight(condition: Boolean) {
    attrs {
        style {
            display(DisplayStyle.Block)
            transitions {
                "line-height" { duration = 0.3.s }
                "opacity" { duration = 0.3.s }
            }
            lineHeight(if (condition) 1.2.em else 0.em)
            opacity(if (condition) 1 else 0)
        }
    }
}
