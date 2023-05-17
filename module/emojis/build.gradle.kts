plugins {
    kodein.library.mpp
    alias(libs.plugins.compose)
}

kotlin.kodein {
    jsEnvBrowserOnly()
    js {
        sources.mainDependencies {
            api(projects.composeUrPres)
            implementation(kotlin.compose.html.core)
            implementation(kotlin.compose.runtime)
        }
    }
}
