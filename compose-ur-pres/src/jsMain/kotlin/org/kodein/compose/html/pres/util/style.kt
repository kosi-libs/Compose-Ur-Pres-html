package org.kodein.compose.html.pres.util

import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnitTime
import org.jetbrains.compose.web.css.StylePropertyEnum
import org.jetbrains.compose.web.css.StyleScope


public typealias Style = StyleScope.() -> Unit

public typealias CSSTimeValue = CSSSizeValue<out CSSUnitTime>

public fun StyleScope.transformOrigin(value: String) {
    property("transform-origin", value)
}

public fun StyleScope.zIndex(value: Int) {
    property("z-index", value)
}

public interface Visibility : StylePropertyEnum {
    public companion object {
        public inline val Visible: Visibility get() = Visibility("ease")
        public inline val Hidden: Visibility get() = Visibility("hidden")
        public inline val Collapse: Visibility get() = Visibility("collapse")

        public inline val Inherit: Visibility get() = Visibility("inherit")
        public inline val Initial: Visibility get() = Visibility("initial")
        public inline val Unset: Visibility get() = Visibility("unset")
    }
}

@Suppress("NOTHING_TO_INLINE")
public inline fun Visibility(value: String): Visibility = value.unsafeCast<Visibility>()

public fun StyleScope.visibility(value: Visibility) {
    property("visibility", value)
}
