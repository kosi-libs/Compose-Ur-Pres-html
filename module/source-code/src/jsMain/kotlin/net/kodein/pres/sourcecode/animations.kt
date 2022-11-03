package net.kodein.pres.sourcecode

import net.kodein.pres.Transition
import net.kodein.pres.Transitions.fontGrow
import net.kodein.pres.hiddenIf
import net.kodein.pres.shownIf
import net.kodein.pres.widget.InternalStrikeThroughContent
import org.jetbrains.compose.web.css.*
import org.kodein.cic.css


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
        css {
            display(DisplayStyle.InlineBlock)
            transitions { "transform" { duration = 0.3.s } }
        }
        style {
            if (condition) transform { scale(scale) }
        }
    }
}

public fun SegmentAnimationBuilder.lineHeight(condition: Boolean) {
    attrs {
        css {
            display(DisplayStyle.Block)
            transitions {
                "line-height" { duration = 0.3.s }
                "opacity" { duration = 0.3.s }
            }
        }
        style {
            lineHeight(if (condition) 1.2.em else 0.em)
            opacity(if (condition) 1 else 0)
        }
    }
}

public fun SegmentAnimationBuilder.struck(condition: Boolean, strikeColor: CSSColorValue) {
    attrs {
        css {
            display(DisplayStyle.InlineBlock)
            position(Position.Relative)
        }
    }
    content { dim, content ->
        InternalStrikeThroughContent(
            strikeColor = strikeColor,
            shown = condition,
            height = 56.percent,
            strikeAttrs = {
                if (dim) style {
                    transitions {
                        "opacity" { duration = 0.3.s }
                    }
                    opacity(DIM_OPACITY)
                }
            },
            content = content
        )
    }
}
