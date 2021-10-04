pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(
    ":compose-ur-pres",
    ":module:source-code",
    ":module:emojis",
    ":demo",
)
