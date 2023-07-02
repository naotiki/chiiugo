import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.targets.js.npm.PublicPackageJsonTask

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
    targets {
        js {
            compilations.all{
                packageJson {
                    //name="@naotiki/chiiugo"
                    private=false
                }
            }
        }
    }
    jvm()
    js(IR) {
        compilations.getByName("main"){
            packageJson {
                name="@naotiki/chiiugo"
                private=false
            }
        }
        binaries.executable()

        nodejs {

        }
        generateTypeScriptDefinitions()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                //testImplementation(kotlin("test"))
                //implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.1")
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