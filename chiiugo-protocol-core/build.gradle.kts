import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "me.naotiki"
version = "1.0-SNAPSHOT2"

repositories {
    mavenCentral()
}
fun kotlinw(target: String): String =
    "org.jetbrains.kotlin-wrappers:kotlin-$target"

val kotlinWrappersVersion = "1.0.0-pre.554"
dependencies {


}

kotlin {
    jvm()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs()
    js(IR) {
        binaries.executable()

        nodejs {
            this.runTask {

            }
        }
        generateTypeScriptDefinitions()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                //testImplementation(kotlin("test"))
                //implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.6.3")
            }


        }
        val jsMain by getting {
            dependencies {
                implementation(enforcedPlatform(kotlinw("wrappers-bom:$kotlinWrappersVersion")))
                implementation(kotlinw("node"))
            }
        }
        val jvmMain by getting {

        }
    }
}