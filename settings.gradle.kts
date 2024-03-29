buildscript {
    repositories {
        mavenLocal()
        maven(url = "https://raw.githubusercontent.com/kosi-libs/kodein-internal-gradle-plugin/mvn-repo")
    }
    dependencies {
        classpath("org.kodein.internal.gradle:kodein-internal-gradle-settings:8.3.0")
    }
}

apply { plugin("org.kodein.settings") }

rootProject.name = "Kosi-CUP"

include(
    ":compose-ur-pres",
    ":module:source-code",
    ":module:emojis",
    ":demo",
)
