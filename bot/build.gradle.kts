plugins {
    kotlin("jvm")
}

repositories {
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":parser"))

    implementation("com.github.patrickzondervan:file-update-watcher:0fd0e41d9d")
    implementation("dev.kord:kord-core:0.8.0-M8")

    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.slf4j:slf4j-simple:1.7.36")
}
