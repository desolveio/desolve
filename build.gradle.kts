plugins {
    kotlin("jvm") version "1.6.20"
    `maven-publish`
}

allprojects {
    group = "io.github.devrawr.repository"
    version = "1.0-SNAPSHOT"

    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("http://143.176.140.30:3717/repo") {
            isAllowInsecureProtocol = true
        }
    }

    dependencies {
        implementation("com.github.patrickzondervan:depenject:42af9d6a0b")
        testImplementation(kotlin("test"))
    }
}
