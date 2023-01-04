plugins {
    kotlin("multiplatform") version "1.7.20" apply false
    id("org.jetbrains.compose") version "1.2.2" apply false
}

allprojects {
    group = "net.kodein.pres"
    version = "1.7.0"

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}
