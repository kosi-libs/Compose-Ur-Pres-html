plugins {
    kotlin("multiplatform") version "1.6.10" apply false
    id("org.jetbrains.compose") version "1.0.1" apply false
}

allprojects {
    group = "net.kodein.pres"
    version = "1.4.0"

    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}
