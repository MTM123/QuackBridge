import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    kotlin("jvm") version "1.7.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.spongepowered.gradle.plugin") version "2.0.2"
}

group = "lv.mtm123"
version = "1.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.15") {
        exclude(module = "opus-java")
    }
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
}

sponge {
    apiVersion("7.4.0")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    license("MIT")
    plugin("quackbridge") {
        displayName("QuackBridge")
        entrypoint("lv.mtm123.quackbridge.QuackBridge")
        description("Bridges discord chat and minecraft chat")
        contributor("MTM123") {
            description("Lead Developer")
        }

        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

val javaTarget = 8
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
}

val shadowJar by tasks.getting(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
    val relocations = listOf(
        "okhttp3",
        "org.jetbrains.kotlin",
        "net.dv8tion",
        "com.fasterxml.jackson",
        "kotlin",
        "org.apache.commons.collections4",
        "com.neovisionaries",
        "gnu.trove",
        "okio",
        "com.iwebpp",
    )
    val targetPackage = "lv.mtm123.quackbridge"

    minimize()

    relocations.forEach {
        relocate(it, "$targetPackage.$it")
    }
    
    val exclusions = listOf(
        "org/slf4j/**",
        "javax/annotation/**",
        "org/jetbrains/**",
        "**/module-info.class",
    )
    
    exclusions.forEach {
        exclude(it)
    }
}

tasks["build"].dependsOn(shadowJar)

tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}