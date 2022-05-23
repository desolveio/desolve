plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":common"))
    implementation("com.github.docker-java:docker-java-core:3.2.13")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.2.13")
    implementation("org.eclipse.jgit:org.eclipse.jgit:4.6.0.201612231935-r")
}