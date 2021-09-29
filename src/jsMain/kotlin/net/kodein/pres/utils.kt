package net.kodein.pres

import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.vw
import org.jetbrains.compose.web.css.width
import org.w3c.dom.HTMLDivElement


fun setBodyNoMargin() {
    document.body!!.style.margin = "0"
}
