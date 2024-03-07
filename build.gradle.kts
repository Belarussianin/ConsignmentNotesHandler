import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.4.21"
    id("org.jetbrains.compose")
}

group = "com.belarusianin"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

compose {
    kotlinCompilerPlugin.set(dependencies.compiler.forKotlin("1.7.20"))
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.8.0")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of("15"))
    }
    jvm {
        //compilations.all {
        //    kotlinOptions.jvmTarget = "15"
        //    kotlinOptions.languageVersion = "1.8"
        //}
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.windows_x64)

                implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.16.1")    //Xml

                implementation("org.apache.poi:poi:5.2.2")                                          //Excel 97-2003
                implementation("org.apache.poi:poi-ooxml:5.2.2")                                    //Excel 2007+

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4-native-mt")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Deb,
                TargetFormat.Exe
            )
            packageName = "ConsignmentNotesHandler"
            packageVersion = "1.0.0"
        }
        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
            //obfuscate.set(true)
        }
    }
}