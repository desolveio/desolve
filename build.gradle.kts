plugins {
    kotlin("jvm") version "1.6.20"
    `maven-publish`
}

allprojects {
    group = "io.desolve.repository"
    version = "1.0.02"

    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")

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

    publishing {
        repositories {
            maven {
                name = "desolve"
                url = uri("${property("desolve.artifactory_contextUrl")}/gradle-release")

                credentials {
                    username = property("desolve.artifactory_user") as String
                    password = property("desolve.artifactory_password") as String
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
