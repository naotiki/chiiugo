plugins {
    kotlin("multiplatform") version "1.8.21"
    kotlin("plugin.serialization") version "1.8.21"
}

group = "me.naotiki"
version = "1.0-SNAPSHOT"

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
    js(IR) {
        binaries.executable()

        nodejs {

        }
        this.binaries
        generateTypeScriptDefinitions()
    }
    sourceSets{
        val commonMain by getting{
           dependencies {
               //testImplementation(kotlin("test"))
               //implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
               implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
               implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.5.1")
               implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.1")
           }


        }
        val jsMain by getting{
            dependencies {
                implementation(enforcedPlatform(kotlinw("wrappers-bom:$kotlinWrappersVersion")))
                implementation(kotlinw("node"))
            }
        }
        val jvmMain by getting{

        }
    }
}