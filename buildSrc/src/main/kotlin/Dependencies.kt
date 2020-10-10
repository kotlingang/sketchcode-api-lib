@file:Suppress("ObjectPropertyName")

import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler


const val kotlinVersion = "1.4.10"
const val ktorVersion = "1.4.1"

const val ktor = "io.ktor:ktor-client-core:$ktorVersion"
const val ktorJson = "io.ktor:ktor-client-json:$ktorVersion"
const val ktorLogging = "io.ktor:ktor-client-logging:$ktorVersion"
const val ktorSerialization = "io.ktor:ktor-client-serialization:$ktorVersion"

const val ktorEngineJvm = "io.ktor:ktor-client-cio:$ktorVersion"

fun KotlinDependencyHandler.ktorImplementation() = arrayOf(ktor, ktorJson, ktorLogging, ktorSerialization)
    .forEach { implementation(it) }

val KotlinDependencyHandler.`ktor-extensions` get() = project(":ktor-extensions")

val KotlinDependencyHandler.`kotlin-stdlib` get() = kotlin("stdlib")

const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.7"
const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0"

const val slf4jLogger = "org.slf4j:slf4j-simple:1.6.1"
