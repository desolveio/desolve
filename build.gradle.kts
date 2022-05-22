plugins {
    kotlin("jvm") version "1.6.20"
}


allprojects {
    group = "io.github.devrawr.repository"
    version = "1.0-SNAPSHOT"

    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("http://localhost:3713/repo") {
            isAllowInsecureProtocol = true
        }
    }

    dependencies {
        implementation("com.github.patrickzondervan:depenject:42af9d6a0b")
    }
}
