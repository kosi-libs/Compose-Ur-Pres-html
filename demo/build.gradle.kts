plugins {
    kodein.mpp
    alias(libs.plugins.compose)
}

kotlin.kodein {
    jsEnvBrowserOnly()
    js {
        sources.mainDependencies {
            implementation(projects.composeUrPres)
            implementation(projects.module.sourceCode)
            implementation(projects.module.emojis)
            implementation(kotlin.compose.html.core)
            implementation(kotlin.compose.runtime)
        }
        target.binaries.executable()
    }
}

kotlin.sourceSets.all {
    languageSettings {
        optIn("org.jetbrains.compose.web.ExperimentalComposeWebApi")
//        optIn("org.jetbrains.compose.web.ExperimentalComposeWebStyleApi")
//        optIn("kotlin.time.ExperimentalTime")
    }
}
