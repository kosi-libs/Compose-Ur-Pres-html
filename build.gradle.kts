plugins {
    kotlin("multiplatform") version "1.5.30" apply false
    id("org.jetbrains.compose") version "1.0.0-alpha4-build348" apply false
}

group = "net.kodein"
version = "1.0"

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}
