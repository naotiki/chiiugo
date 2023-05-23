import org.gradle.kotlin.dsl.support.zipTo
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.desktop.application.tasks.AbstractJPackageTask

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.8.10"
}
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
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}
val appVersion = project.properties.getOrDefault("appVersion", "0.0.1-dev").toString()
version=appVersion
dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.desktop.components.animatedImage)


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.5.0")

    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.4.1")
    val exposedVersion: String ="0.41.1"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("com.h2database:h2:2.1.214")
}

compose.desktop {
    application {
        mainClass = "MainKt"
        jvmArgs += listOf("-Dfile.encoding=UTF-8")
        nativeDistributions {
            targetFormats( TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "Chiiugo"
            description = "ちぃうご(Chiiugo) Client App"
            linux {
                debPackageVersion = appVersion.trimStart('v')
                rpmPackageVersion = appVersion.replace("-", "_")
                shortcut = true
            }
            windows {
                packageVersion = appVersion.replace("[^0-9.]".toRegex(), "")
                console = !buildTypes.release.proguard.isEnabled.getOrElse(false)
                menu = true
                shortcut = true
                dirChooser = true
            }
        }
    }
}
tasks.withType(AbstractJPackageTask::class) {
    doLast {
        val artifact = this@withType.outputs.files.singleFile.listFiles()!!.single()
        println(artifact.absolutePath)
    }
}
tasks.register<Delete>("removeArchives") {
    delete(fileTree("build/compose/binaries/main-release/app") {
        include("**/*.zip")
    })
    delete(fileTree("build/compose/jars") {
        include("*.jar")
    })
}
tasks.withType(org.gradle.jvm.tasks.Jar::class) {
    mustRunAfter("removeArchives")
}
val os = System.getProperty("os.name").replace(" ", "_")
tasks.register("superReleaseBuild") {

    dependsOn(
        "removeArchives",
        "packageReleaseUberJarForCurrentOS",
        "packageReleaseDistributionForCurrentOS",
        "createReleaseDistributable"
    )
    doLast {
        val app = file("build/compose/binaries/main-release/app")
        val zip = file(app.toPath().resolve("Chiiugo-$os-$appVersion.zip"))
        zipTo(zip, app.listFiles()!!.single())
    }
}
