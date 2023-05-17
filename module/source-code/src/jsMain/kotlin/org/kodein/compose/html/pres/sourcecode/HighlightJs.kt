package org.kodein.compose.html.pres.sourcecode

import kotlinx.browser.document
import org.w3c.dom.HTMLLinkElement

@Suppress("unused")
internal external interface HighlightJs {

    interface Result {
        val value: String
    }

    fun highlight(lang: String, code: String): Result
}

@JsModule("highlight.js")
@JsNonModule
internal external val hljs: HighlightJs

public fun installHighlightJsTheme(name: String) {
    val link = document.createElement("link") as HTMLLinkElement
    link.rel = "stylesheet"
    link.href = "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.2.0/styles/base16/$name.min.css"
    document.head!!.append(link)
}
