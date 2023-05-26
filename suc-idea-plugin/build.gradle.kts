import java.util.*
import kotlin.reflect.KProperty

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("org.jetbrains.intellij") version "1.13.3"
    kotlin("plugin.serialization") version "1.8.10"
}

val publishProperties=Properties()
publishProperties.load(file("publish.properties").bufferedReader())
group = "me.naotiki"
version = "1.0-SNAPSHOT4"
sourceSets{
    kotlin{
        main{
            kotlin{
                srcDir("../suc-protocol")
            }
        }
    }
}
repositories {
    mavenCentral()
}
dependencies{
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.0")
    implementation(project(":chiiugo-protocol-core"))
}
// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2.5")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("232.*")
    }

    signPlugin {
        certificateChainFile.set(file("certificate/chain.crt"))
        privateKeyFile.set(file("certificate/private.pem"))
        password.set(publishProperties.getProperty("password"))
    }

    publishPlugin {
        token.set(publishProperties.getProperty("token"))
    }
}
