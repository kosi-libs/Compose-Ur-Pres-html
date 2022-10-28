package net.kodein.pres.sourcecode

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.opacity
import org.jetbrains.compose.web.css.s
import org.jetbrains.compose.web.css.transitions
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.NodeList
import org.w3c.dom.asList
import kotlin.math.min
import org.w3c.dom.Text as DomText

private val open = Regex("«([^:]+):")
private val close = '»'

internal sealed class Token {
    class Text(val text: String) : Token() {
        override fun toString() = buildString {
            append("TEXT:\n")
            append(text.prependIndent("    "))
        }
    }
    class Open(val name: String) : Token() {
        override fun toString(): String = "OPEN: $name"
    }
    object Close : Token() {
        override fun toString() = "CLOSE"
    }
}

internal fun tokenize(code: String): Pair<List<Token>, String> {
    val list = ArrayList<Token>()
    val cleaned = StringBuilder()
    var pos = 0
    while (pos < code.length) {
        val openMatch = open.find(code, pos)
        val closePosition = code.indexOf(close, pos).takeIf { it >= 0 }
        val closest = min(openMatch?.range?.first ?: code.length, closePosition ?: code.length)
        if (closest != pos) {
            val text = code.substring(pos until closest)
            list += Token.Text(text)
            cleaned.append(text)
            pos = closest
        }
        when {
            openMatch != null && closest == openMatch.range.first -> {
                list += Token.Open(openMatch.groups[1]!!.value)
                pos += openMatch.value.length
            }
            closePosition != null && closest == closePosition -> {
                list += Token.Close
                pos += 1
            }
        }
    }
    return list to cleaned.toString()
}

internal sealed class Node {
    abstract fun toString(indent: Int): String
    class Text(val text: String): Node() {
        override fun toString(indent: Int) =  buildString {
            append("TEXT:".prependIndent("  ".repeat(indent)))
            append("\n")
            append(text.prependIndent("  ".repeat(indent) + "   |"))
        }
    }
    class Span(val name: String, val classNames: List<String>, val nodes: List<Node>): Node() {
        fun text(): String = nodes.joinToString("") {
            when (it) {
                is Text -> it.text
                is Span -> it.text()
            }
        }
        override fun toString(indent: Int): String = buildString {
            append("SPAN $name:".prependIndent("  ".repeat(indent)))
            append("\n")
            nodes.forEach {
                append(it.toString(indent + 1))
                append("\n")
            }
        }
    }

    override fun toString(): String = toString(0)
}

internal fun fromSegmentTokens(tokens: List<Token>, position: Int): Pair<List<Node>, Int> {
    val list = ArrayList<Node>()
    var pos = position
    while (pos < tokens.size) {
        when (val token = tokens[pos]) {
            is Token.Text -> {
                list.add(Node.Text(token.text))
                pos += 1
            }
            is Token.Open -> {
                val (innerSegments, endPos) = fromSegmentTokens(tokens, pos + 1)
                list.add(Node.Span(
                    name = "segment-${token.name}",
                    classNames = listOf("segment-${token.name}"),
                    nodes = innerSegments
                ))
                pos = endPos
            }
            is Token.Close -> {
                return list to (pos + 1)
            }
        }
    }
    return list to pos
}

internal fun fromSegmentTokens(tokens: List<Token>): List<Node> =
    fromSegmentTokens(tokens, 0).first


internal fun fromHljsDom(nodes: NodeList): List<Node> =
    nodes.asList().map { node ->
        when (node) {
            is DomText -> Node.Text(node.wholeText)
            is HTMLSpanElement -> Node.Span(
                name = node.className,
                classNames = node.classList.asList(),
                nodes = fromHljsDom(node.childNodes)
            )
            else -> error("Unsupported DOM node from HLJS: $${node::class.simpleName}")
        }
    }

internal fun outOfSyncError(): Nothing = error("""
            HighlightJS & code animation trees are out of sync.
            This usually happens when you are trying to animate together parts of different highlighted tokens.
            Example: print«e:ln("This» is an error!")
                     Cannot work because println & "This is an error!" are two different tokens & animations contains
                     only part of them.
            """.trimIndent())

internal fun merge(left: MutableList<Node>, right: MutableList<Node>): List<Node> {
    val list = ArrayList<Node>()

    while (left.isNotEmpty() || right.isNotEmpty()) {
        val lNode = left.firstOrNull() ?: return list
        val rNode = right.firstOrNull() ?: return list

        fun leftSpan() {
            lNode as Node.Span

            list += Node.Span(lNode.name, lNode.classNames, merge(lNode.nodes.toMutableList(), right))
            left.removeFirst()
        }

        fun rightSpan() {
            rNode as Node.Span

            list += Node.Span(rNode.name, rNode.classNames, merge(left, rNode.nodes.toMutableList()))
            right.removeFirst()
        }

        fun text() {
            lNode as Node.Text
            rNode as Node.Text

            val length = min(lNode.text.length, rNode.text.length)
            if (lNode.text.substring(0, length) != rNode.text.substring(0, length)) outOfSyncError()
            list += Node.Text(lNode.text.substring(0, length))

            left.removeFirst()
            if (length < lNode.text.length) left.add(0, Node.Text(lNode.text.substring(length)))

            right.removeFirst()
            if (length < rNode.text.length) right.add(0, Node.Text(rNode.text.substring(length)))
        }

        when {
            lNode is Node.Span && rNode is Node.Span -> if (lNode.text().length >= rNode.text().length) leftSpan() else rightSpan()
            lNode is Node.Span && rNode is Node.Text -> leftSpan()
            lNode is Node.Text && rNode is Node.Span -> rightSpan()
            lNode is Node.Text && rNode is Node.Text -> text()
        }
    }

    return list
}

internal fun merge(left: List<Node>, right: List<Node>): List<Node> {
    val lNodes = left.toMutableList()
    val rNodes = right.toMutableList()

    val list = merge(lNodes, rNodes)
    if (lNodes.isNotEmpty() || rNodes.isNotEmpty()) outOfSyncError()

    return list
}

@Composable
internal fun NodeList(
    nodes: List<Node>,
    attrs: Map<String, AttrBuilderContext<HTMLSpanElement>>,
    unDimmed: List<String>,
    unDim: Boolean = false
) {
    nodes.forEach {
        when (it) {
            is Node.Text -> Span({
                style {
                    transitions {
                        "opacity" { duration = 0.3.s }
                    }
                    if (!unDim && unDimmed.isNotEmpty()) opacity(0.05)
                }
            }) { Text(it.text) }
            is Node.Span -> Span({
                classes(*it.classNames.toTypedArray())
                attrs[it.name]?.invoke(this)
            }) { NodeList(it.nodes, attrs, unDimmed, unDim || it.name in unDimmed) }
        }
    }
}
