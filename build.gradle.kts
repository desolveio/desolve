plugins {
    kotlin("jvm") version "1.7.10"
    `maven-publish`
    idea
}

allprojects {
    group = "io.desolve.repository"
    version = "1.0.2"

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
