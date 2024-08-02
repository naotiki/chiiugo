import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import java.util.*
import kotlin.reflect.KProperty

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.intellij.platform") version "2.0.0"
    kotlin("plugin.serialization") version "1.9.23"
}

val publishProperties=Properties()
publishProperties.load(file("publish.properties").bufferedReader())
group = "me.naotiki"
version = "1.0-SNAPSHOT8"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}
dependencies{
    intellijPlatform {
        intellijIdeaCommunity("2023.1")
        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.6.3")
    implementation(project(":chiiugo-protocol-core"))
}
// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
/*intellij {
    version.set("2022.2.5")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(*//* Plugin Dependencies *//*))
}*/

intellijPlatform{
    pluginConfiguration{
        ideaVersion{
            sinceBuild = "231"
            untilBuild = provider { null }
        }
    }
    pluginVerification{
        ides{
            select {
                channels = listOf(
                    ProductRelease.Channel.RELEASE,
                    ProductRelease.Channel.RC
                )
            }
        }
    }
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

    signPlugin {
        certificateChainFile.set(file("certificate/chain.crt"))
        privateKeyFile.set(file("certificate/private.pem"))
        password.set(publishProperties.getProperty("password"))
    }

    publishPlugin {
        token.set(publishProperties.getProperty("token"))
    }
}
