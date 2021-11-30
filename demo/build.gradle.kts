plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":compose-ur-pres"))
                implementation(project(":module:source-code"))
                implementation(project(":module:emojis"))
                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("org.jetbrains.compose.web.ExperimentalComposeWebApi")
                optIn("org.jetbrains.compose.web.ExperimentalComposeWebStyleApi")
                optIn("kotlin.time.ExperimentalTime")
            }
        }
    }
}
