plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":common"))
    implementation("org.eclipse.jgit:org.eclipse.jgit:4.6.0.201612231935-r")
}