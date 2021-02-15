import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.noarg") version ("1.4.30")
    kotlin("plugin.serialization") version "1.4.30"
    application
}

group = "me.lewik"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

configure<org.jetbrains.kotlin.noarg.gradle.NoArgExtension> {
    annotation("timetable.NoArg")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("org.slf4j:slf4j-log4j12:1.7.30")
    implementation("org.optaplanner:optaplanner-core:8.2.0.Final")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}
