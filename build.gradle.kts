import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "top.e404"
version = "1.0.6"
val epluginVersion = "1.1.0"

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
    // engine hub
    maven("https://maven.enginehub.org/repo/")
    // ady
    maven("https://repo.tabooproject.org/repository/releases/")
    // placeholderAPI
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

dependencies {
    // paper
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    // adventure
    implementation("net.kyori:adventure-api:4.12.0")
    // eplugin
    implementation(eplugin("core"))
    implementation(eplugin("menu"))
    implementation(eplugin("serialization"))
    implementation(eplugin("hook-mmoitems"))
    implementation(eplugin("hook-itemsadder"))
    implementation(eplugin("hook-adyeshach"))
    implementation(eplugin("hook-orangeengine"))
    implementation(eplugin("hook-placeholderapi"))
    // serialization
    implementation(kotlinx("serialization-core-jvm", "1.3.3"))
    implementation(kotlinx("serialization-json", "1.3.3"))
    // mythic lib
    compileOnly("io.lumine:MythicLib-dist:1.4")
    // mi
    compileOnly("net.Indyuce:MMOItems:6.7.3")
    // itemsadder
    compileOnly("com.github.LoneDev6:api-itemsadder:3.0.0")
    // ady
    compileOnly("ink.ptms.adyeshach:all:2.0.0-snapshot-10")
    // placeholderAPI
    compileOnly("me.clip:placeholderapi:2.11.1")
    // oe
    compileOnly(fileTree("libs"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    processResources {
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
