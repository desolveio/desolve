plugins {
    kotlin("jvm") version "1.6.21"
    `maven-publish`
    idea
}

allprojects {
    group = "io.desolve.repository"
    version = "1.0.0.4"

    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "idea")

    idea {
        module {
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }

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
