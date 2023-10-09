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

    // NOT USED - Workaround to make Dokka happy in MPP JS Only projects
    // https://github.com/Kotlin/dokka/issues/3122
    jvm()
}
