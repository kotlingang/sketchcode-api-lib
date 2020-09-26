plugins {
    kotlin("multiplatform") version "1.4.10"
}

group = "fun.sketchcode.api.lib"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

val ktorVersion = "1.4.1"

kotlin {

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:1.3.2")
                implementation("io.ktor:ktor-client-json:1.3.2")
                implementation("io.ktor:ktor-client-logging:1.3.2")
                implementation("io.ktor:ktor-client-serialization:1.3.2")

                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.3.72")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.7")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.20.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:+")
            }
        }
    }
}