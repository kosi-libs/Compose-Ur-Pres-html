plugins {
    kotlin("multiplatform") version "1.5.31" apply false
    id("org.jetbrains.compose") version "1.0.0-rc6" apply false
}

allprojects {
    group = "net.kodein.pres"
    version = "1.1.0"

    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}

// https://youtrack.jetbrains.com/issue/KT-49124
rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().apply {
        resolution("@webpack-cli/serve", "1.5.2")
    }
}
