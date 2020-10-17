@file:Suppress("UNUSED_VARIABLE")


plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    `maven-publish`
}

group = "fun.sketchcode.api.lib"
version = "1.0"

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(`kotlin-stdlib`)

                ktorImplementation()
            }
        }
    }
}