plugins {
    kotlin("jvm") version "1.6.20"
    `maven-publish`
}

allprojects {
    group = "io.desolve.repository"
    version = "1.0.01"

    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        testImplementation(kotlin("test"))

        api("io.insert-koin:koin-core:3.2.0")
        testApi("io.insert-koin:koin-test:3.2.0")
    }

    publishing {
        repositories {
            maven {
                name = "desolve"
                url = uri("${property("desolve_artifactory_contextUrl")}/gradle-release")

                credentials {
                    username = property("desolve_artifactory_user") as String
                    password = property("desolve_artifactory_password") as String
                }
            }
        }

        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])
            }
        }
    }
}
