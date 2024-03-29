package org.kodein.compose.html.pres.sourcecode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.bottom
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.cursor
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.right
import org.jetbrains.compose.web.css.transform
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.kodein.compose.html.css.css
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement
import kotlin.collections.set

public typealias ContentDecorator<T> = @Composable ElementScope<T>.(Boolean, ContentBuilder<T>) -> Unit

public class SegmentAnimationBuilder internal constructor() {
    internal var unDimmed: Boolean = false
    internal var attrs: AttrBuilderContext<HTMLSpanElement>? = null
    internal var content: ContentDecorator<HTMLSpanElement>? = null

    public fun unDimmed(unDimmed: Boolean = true) {
        this.unDimmed = unDimmed
    }

    public fun attrs(attrs: AttrBuilderContext<HTMLSpanElement>) {
        if (this.attrs == null) this.attrs = attrs
        else {
            val a = this.attrs!!
            this.attrs = {
                a.invoke(this)
                attrs.invoke(this)
            }
        }
    }

    public fun content(content: ContentDecorator<HTMLSpanElement>) {
        require(this.content == null) { "Content is already defined" }
        this.content = content
    }
}

public class SourceAnimationBuilder internal constructor() {
    internal val contents = HashMap<String, Pair<ContentDecorator<HTMLSpanElement>?, AttrBuilderContext<HTMLSpanElement>?>>()
    internal var unDimmed = ArrayList<String>()

    public operator fun String.invoke(block: SegmentAnimationBuilder.() -> Unit) {
        val builder = SegmentAnimationBuilder().apply(block)
        contents["segment-$this"] = Pair(builder.content, builder.attrs)
        if (builder.unDimmed) unDimmed.add("segment-$this")
    }
}

@Composable
public fun SourceCode(
    lang: String,
    code: String,
    copyButton: Boolean = false,
    anims: SourceAnimationBuilder.() -> Unit = {}
) {
    Pre({
        css {
            position(Position.Relative)
            ".copyButton" {
                display(DisplayStyle.None)
            }
            (self + hover) {
                ".copyButton" {
                    display(DisplayStyle.Block)
                }
            }
        }
    }) {
        Code({
            classes("lang-$lang", "hljs")
        }) {
            var nodeList: List<Node> by remember { mutableStateOf(emptyList()) }
            var builder by remember { mutableStateOf(SourceAnimationBuilder().apply(anims)) }

            DisposableEffect(null) {
                val composition = renderComposable(scopeElement) { NodeList(nodeList, builder.contents, builder.unDimmed) }
                onDispose { composition.dispose() }
            }

            LaunchedEffect(lang, code) {
                val (tokens, cleanCode) = tokenize(code)
                val segments = fromSegmentTokens(tokens)

                val container = document.createElement("span") as HTMLElement
                container.innerHTML = hljs.highlight(lang, cleanCode).value
                val highlighted = fromHljsDom(container.childNodes)

                nodeList = merge(highlighted, segments)
//                unDimmed = builder.unDimmed
            }

            SideEffect {
                builder = SourceAnimationBuilder().apply(anims)
            }
        }
        if (copyButton) {
            Button({
                classes("copyButton")
                css {
                    position(Position.Absolute)
                    bottom(0.px)
                    right(0.px)
                    property("border", "none")
                    backgroundColor(Color.transparent)
                    fontSize(2.cssRem)
                    cursor("pointer")

                    (self + active) {
                        transform { scale(1.2) }
                    }
                }
                onClick {
                    window.navigator.clipboard.writeText(code)
                    it.stopPropagation()
                }
            }) {
                Text("\uD83D\uDCCB")
            }
        }
    }
}
