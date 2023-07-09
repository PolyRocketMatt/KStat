import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration

plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.dokka") version "1.8.20"
}

group = "com.github.polyrocketmatt"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:1.8.20")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.dokkaHtml {
    outputDirectory.set(file("docs"))
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customAssets = listOf(file("img/logo-icon.svg"))
        footerMessage = "© 2023 Matthias Kovacic"
    }
}

kotlin {
    jvmToolchain(17)
}