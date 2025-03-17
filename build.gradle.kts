import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

group = "top.e404"
version = "1.2.0"
val epluginVersion = "1.4.0"

fun kotlinx(id: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$id:$version"
fun eplugin(id: String, version: String = epluginVersion) = "top.e404:eplugin-$id:$version"

repositories {
    mavenLocal()
    maven("https://oss.sonatype.org/content/groups/public/")
    // paper
    maven("https://repo.papermc.io/repository/maven-public/")
    // jitpack
    maven("https://jitpack.io")
    // mm
    maven("https://mvn.lumine.io/repository/maven-public/")
    // mi
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    // engine hub
    maven("https://maven.enginehub.org/repo/")
    // placeholderAPI
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

dependencies {
    // paper
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    // adventure
    compileOnly("net.kyori:adventure-api:4.12.0")
    // eplugin
    implementation(eplugin("core"))
    implementation(eplugin("menu"))
    implementation(eplugin("serialization"))
    implementation(eplugin("hook-mmoitems"))
    implementation(eplugin("hook-itemsadder"))
    implementation(eplugin("hook-modelengine"))
    implementation(eplugin("hook-placeholderapi"))
    // serialization
    implementation(kotlinx("serialization-core-jvm", "1.3.3"))
    implementation(kotlinx("serialization-json", "1.3.3"))
    // mythic lib
    compileOnly("io.lumine:MythicLib-dist:1.4")
    // mi
    compileOnly("net.Indyuce:MMOItems-API:6.9.4-SNAPSHOT")
    // itemsadder
    compileOnly("com.github.LoneDev6:api-itemsadder:3.0.0")
    // placeholderAPI
    compileOnly("me.clip:placeholderapi:2.11.6")
    // ModelEngine
    compileOnly("com.ticxo.modelengine:api:R3.2.0")
}

java {
    withSourcesJar()
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks {
    processResources {
        filteringCharset = "UTF8"
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
        exclude("META-INF/**")
        relocate("kotlin", "top.e404.wularecipe.relocate.kotlin")
        relocate("top.e404.eplugin", "top.e404.wularecipe.relocate.eplugin")

        doFirst {
            for (file in File("jar").listFiles() ?: arrayOf()) {
                println("正在删除`${file.name}`")
                file.delete()
            }
        }

        doLast {
            File("jar").mkdirs()
            for (file in File("build/libs").listFiles() ?: arrayOf()) {
                println("正在复制`${file.name}`")
                file.copyTo(File("jar/${file.name}"), true)
            }
        }
    }
}
