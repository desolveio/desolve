plugins {
    kotlin("jvm")
}

repositories {
    maven("https://m2.dv8tion.net/releases")
    maven("http://143.176.140.30:3717/repo") {
        isAllowInsecureProtocol = true
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":common"))
    implementation(project(":parser"))
    implementation("dev.kord:kord-core:0.8.0-M8")
    implementation("io.github.devrawr.watcher:directory-watcher:1.0-SNAPSHOT")
    implementation("io.github.devrawr.lunar:lunar-client-metadata:1.0-SNAPSHOT")
    implementation("org.slf4j:slf4j-api:1.7.5")
    implementation("org.slf4j:slf4j-simple:1.7.5")
}