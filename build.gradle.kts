@file:Suppress("UNUSED_VARIABLE")


plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.4.10"
    `maven-publish`
}

group = "fun.sketchcode.api.lib"
version = "1.0"

repositories {
    google()
    mavenCentral()
    jcenter()
}

kotlin {

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(`kotlin-stdlib`)

                ktorImplementation()
                implementation(`ktor-extensions`)

                implementation(coroutines)
                implementation(serialization)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(ktorEngineJvm)
                implementation(slf4jLogger)
            }
        }

        configure(listOf(targets["metadata"], jvm())) {
            mavenPublication {
                val targetPublication = this@mavenPublication
                tasks.withType<AbstractPublishToMaven>()
                        .matching { it.publication == targetPublication }
                        .all { onlyIf { findProperty("isMainHost") == "true" } }
            }
        }
    }

}

publishing {
    repositories {
        maven {
            url = uri("$buildDir/maven")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
