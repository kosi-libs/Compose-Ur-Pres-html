package net.kodein.pres.sourcecode

import androidx.compose.runtime.*
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.emptyList
import kotlin.collections.set


public class SegmentAnimationBuilder internal constructor() {
    internal var unDimmed: Boolean = false
    internal var attrs: AttrBuilderContext<HTMLSpanElement>? = null

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
}

public class SourceAnimationBuilder internal constructor() {
    internal val attrs = HashMap<String, AttrBuilderContext<HTMLSpanElement>>()
    internal var unDimmed = ArrayList<String>()

    public operator fun String.invoke(block: SegmentAnimationBuilder.() -> Unit) {
        val builder = SegmentAnimationBuilder().apply(block)
        builder.attrs?.let { attrs[this] = it }
        if (builder.unDimmed) unDimmed.add("segment-$this")
    }
}

@Composable
public fun SourceCode(
    lang: String,
    code: String,
    anims: SourceAnimationBuilder.() -> Unit = {}
) {
    Pre {
        Code({
            classes("lang-$lang", "hljs")
        }) {
            var nodeList: List<Node> by remember { mutableStateOf(emptyList()) }
            var unDimmed: List<String> by remember { mutableStateOf(emptyList()) }

            DisposableRefEffect {
                val composition = renderComposable(it) { NodeList(nodeList, unDimmed) }
                onDispose { composition.dispose() }
            }

            DomSideEffect {
                val builder = SourceAnimationBuilder().apply(anims)

                val (tokens, cleanCode) = tokenize(code)
                val segments = fromSegmentTokens(tokens, builder.attrs)
//                println(Node.Span("SEGMENTS", {}, segments))

                val container = document.createElement("span") as HTMLElement
                container.innerHTML = hljs.highlight(lang, cleanCode).value
                val highlighted = fromHljsDom(container.childNodes)
//                println(Node.Span("HIGHLIGHTED", {}, highlighted))

                nodeList = merge(highlighted, segments)
                unDimmed = builder.unDimmed
//                println(Node.Span("MERGED", {}, nodeList))
            }
        }
    }
}
