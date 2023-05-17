plugins {
    kodein.library.mpp
    alias(libs.plugins.compose)
}

kotlin.kodein {
    jsEnvBrowserOnly()
    js {
        sources.mainDependencies {
            implementation(kotlin.compose.html.core)
            implementation(kotlin.compose.runtime)
            implementation(libs.cssInComposable)
        }
    }
}

kotlin.sourceSets.all {
    languageSettings {
        optIn("org.jetbrains.compose.web.ExperimentalComposeWebApi")
        optIn("org.jetbrains.compose.web.ExperimentalComposeWebStyleApi")
        optIn("kotlin.time.ExperimentalTime")
    }
}
