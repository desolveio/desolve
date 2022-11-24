dependencies {
    implementation(project(":common"))
    implementation("org.gradle:gradle-tooling-api:7.3-20210825160000+0000")
    implementation("org.apache.maven:maven-model:3.8.5")

    implementation("com.github.docker-java:docker-java-core:3.2.13")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.2.13")

    implementation("org.eclipse.jgit:org.eclipse.jgit:6.3.0.202209071007-r")
}
