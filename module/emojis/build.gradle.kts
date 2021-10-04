plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    explicitApi()

    js(IR) {
        browser()
    }

    targets.all {
        compilations.all {
            kotlinOptions.allWarningsAsErrors = true
        }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":compose-ur-pres"))
                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("org.jetbrains.compose.web.ExperimentalComposeWebApi")
                optIn("kotlin.time.ExperimentalTime")
            }
        }
    }
}
