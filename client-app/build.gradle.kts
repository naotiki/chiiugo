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


val appVersion = AppVersion.parseAppVersion(project.properties.getOrDefault("appVersion", "v0.0.1-dev1").toString())
version=appVersion.toString()
dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.desktop.components.animatedImage)
    implementation(compose.materialIconsExtended)


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.0")

    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.4.1")
    val exposedVersion: String ="0.41.1"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("com.h2database:h2:2.1.214")
    implementation("com.1stleg:jnativehook:2.1.0")
    implementation(project(":chiiugo-protocol-core"))
}

compose.desktop {
    application {
        buildTypes.release.proguard.isEnabled.set(false)
        mainClass = "chiiugo.app.MainKt"
        jvmArgs += listOf("-Dfile.encoding=UTF-8")
        nativeDistributions {
            modules("java.sql")
            targetFormats( TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm,TargetFormat.Dmg)
            packageName = "Chiiugo"
            description = "Chiiugo Client App"
            vendor="Naotiki"
            linux {
                debPackageVersion = appVersion.generateDebVersion()
                rpmPackageVersion = appVersion.generateRpmVersion()
                shortcut = true
                iconFile.set(file("Chiiugo.png"))
            }
            macOS{
                packageVersion=appVersion.generateMacVersion()
            }
            windows {
                perUserInstall=true
                packageVersion = appVersion.generateWindowsVersion()
                menu = true
                shortcut = true
                dirChooser = true
                iconFile.set(file("Chiiugo.ico"))
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
    group="build"
    dependsOn(
        "removeArchives",
        "packageReleaseUberJarForCurrentOS",
        "packageReleaseDistributionForCurrentOS",
        "createReleaseDistributable"//Ignore "Release" to avoid ktx-datetime and proguard bug
    )
    doLast {
        val app = file("build/compose/binaries/main-release/app")
        val zip = file(app.toPath().resolve("Chiiugo-$os-$appVersion.zip"))
        zipTo(zip, app.listFiles()!!.single())
    }
}
