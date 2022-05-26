plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":common"))

    implementation("com.github.docker-java:docker-java-core:3.2.13")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.2.13")

    implementation("org.eclipse.jgit:org.eclipse.jgit:6.1.0.202203080745-r")
}
