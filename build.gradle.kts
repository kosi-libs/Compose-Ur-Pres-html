plugins {
    kodein.root
}

allprojects {
    group = "org.kodein.compose.html.pres"
    version = "1.10.0"
}

task<Sync>("updateDemoDocs") {
    group = "publishing"
    dependsOn("demo:jsBrowserDistribution")
    from("$rootDir/demo/build/distributions")
    into("$rootDir/docs/demo")
}
