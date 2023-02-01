@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }

    sourceSets {
        named("jsMain") {
            dependencies {
                implementation(projects.composeUrPres)
                implementation(projects.module.sourceCode)
                implementation(projects.module.emojis)
                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }

        all {
            languageSettings {
                optIn("org.jetbrains.compose.web.ExperimentalComposeWebApi")
                optIn("org.jetbrains.compose.web.ExperimentalComposeWebStyleApi")
                optIn("kotlin.time.ExperimentalTime")
            }
        }
    }
}
