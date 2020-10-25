@file:Suppress("UNUSED_VARIABLE")


plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.4.10"
    `maven-publish`
}

group = "fun.sketchcode.api.lib"
version = "1.2.1"

repositories {
    google()
    mavenCentral()
    jcenter()
}

kotlin {

    jvm()
    js()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(`kotlin-stdlib`)

                ktorImplementation()
                api(`ktor-extensions`)

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
    }

}


val bintrayUser: String? by project
val bintrayApiKey: String? by project

val projectName = project.name

allprojects {
    apply(plugin = "maven-publish")
    publishing {
        val vcs = "https://github.com/kotlingang/sketchcode-api-lib"

        publications.filterIsInstance<MavenPublication>().forEach { publication ->
            publication.pom {
                name.set(project.name)
                description.set(project.description)
                url.set(vcs)

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("y9san9")
                        name.set("Sketchcode Team")
                        organization.set("Kotlingang")
                        organizationUrl.set("https://sketchcode.fun")
                    }

                }
                scm {
                    url.set(vcs)
                    tag.set(project.version.toString())
                }
            }
        }

        println("u: $bintrayUser")
        if (bintrayUser != null && bintrayApiKey != null) {
            repositories {
                maven {
                    name = "bintray"
                    url = uri("https://api.bintray.com/maven/y9san9/kotlingang/$projectName/;publish=1;override=1")
                    credentials {
                        username = bintrayUser
                        password = bintrayApiKey
                    }
                }
            }

        }
    }
    println(project.name)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
