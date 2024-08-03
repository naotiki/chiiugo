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
version = "1.0-SNAPSHOT9"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}
dependencies{
    intellijPlatform {
        intellijIdeaCommunity("2024.1")
        // FIXME MP-6784 Plugin-Verifier-SuspendLambda-references-reported-as-Kotlin-internal-API-usage
        // https://youtrack.jetbrains.com/issue/MP-6784/Plugin-Verifier-SuspendLambda-references-reported-as-Kotlin-internal-API-usage
        pluginVerifier("1.371")
        zipSigner()
        instrumentationTools()
    }

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

kotlin{
    jvmToolchain(17)
}

intellijPlatform{
    signing{
        certificateChainFile.set(file("certificate/chain.crt"))
        privateKeyFile.set(file("certificate/private.pem"))
        password.set(publishProperties.getProperty("password"))
    }
    publishing{
        token.set(publishProperties.getProperty("token"))
    }
    pluginConfiguration{
        ideaVersion{
            sinceBuild = "241"
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
