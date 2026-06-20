import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask
import net.paulem.buildscript.NewGithubChangelog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.freefair.lombok)
    alias(libs.plugins.shadow)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.minecraft.server)
}

group = "net.paulem.btm"
version = "3.0.0"

// ------------------------ REPOSITORIES ------------------------
repositories {
    mavenCentral()

    maven { url = uri("https://jitpack.io") }
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }

    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/central") }

    maven("https://repo.extendedclip.com/releases/")
    maven("https://repo.oraxen.com/releases")

    maven("https://maven.paulem.net/releases")
}

// ------------------------ DEPENDENCIES ------------------------
dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(libs.annotations)

    implementation(libs.adventure)
    implementation(libs.adventure.text.minimessage)
    implementation(libs.adventure.text.legacy)

    implementation(libs.spigotUpdateChecker) {
        exclude(group = "com.github.Anon8281", module = "UniversalScheduler")
        exclude(group = "com.jeff_media.updatechecker.universalScheduler")
    }
    implementation(libs.universalScheduler)

    compileOnly(libs.placeholderapi)
    compileOnly(libs.oraxen)

    compileOnly(libs.validation.api)
    annotationProcessor(libs.validation.api)

    implementation(libs.exp4j)
}

// ------------------------ SHADOW JAR ------------------------
artifacts.archives(tasks.shadowJar)

tasks.shadowJar {
    archiveClassifier.set("")

    exclude("META-INF/**")
    exclude("LICENSE.txt")
    exclude("License-ASM.txt")

    relocate("com.jeff_media.updatechecker", "net.paulem.btm.libs.updatechecker")

    // Use UniversalScheduler from SpigotUpdateChecker instead of the one from implementation
    exclude("com/github/Anon8281/universalScheduler/*Scheduler/**")
    exclude("com/github/Anon8281/universalScheduler/scheduling/**")
    exclude("com/github/Anon8281/universalScheduler/utils/**")
    exclude("com/github/Anon8281/universalScheduler/UniversalScheduler.**")

    relocate("com.github.Anon8281.universalScheduler", "net.paulem.btm.libs.updatechecker.universalScheduler")

    relocate("net.kyori.adventure", "net.paulem.btm.libs.adventure")

    minimize()
}

// ------------------------ RESOURCES PROCESS ------------------------
tasks.processResources {
    inputs.property("version", version)

    filesMatching("plugin.yml") {
        expand(mapOf("version" to version))
    }
}

// ------------------------ PAPER TEST SYSTEM ------------------------
val paperDir = rootDir.resolve("servers").resolve("paper")

listOf("1.9.4", "1.12.2", "1.13.2", "1.14.4", "1.15.2", "1.16.5", "1.17.1", "1.18.2", "1.19.4", "1.20.1", "1.20.4", "1.20.6", "1.21", "1.21.4", "1.21.5", "1.21.11", "26.1.2").forEach { version ->
    tasks.register<LaunchMinecraftServerTask>("paper-$version") {
        group = "minecraft"
        description = "Launches a Paper $version server"

        dependsOn(tasks.build)

        doFirst {
            copies(version, paperDir)
        }

        serverDirectory.set(paperDir.resolve(version).absolutePath)
        jarUrl.set(LaunchMinecraftServerTask.JarUrl.Paper(version))
        agreeEula.set(true)
        jvmArgument.set(listOf("-Djava.awt.headless=true", "-Xms512M", "-Xmx2G", "-XX:+UseG1GC", "-Dpaper.playerbreeding=true"))
    }
}

val foliaDir = rootDir.resolve("servers").resolve("folia")

listOf("1.21.4").forEach { version ->
    tasks.register<LaunchMinecraftServerTask>("folia-$version") {
        group = "minecraft"
        description = "Launches a Folia $version server"

        dependsOn(tasks.build)

        doFirst {
            copies(version, foliaDir)
        }

        serverDirectory.set(foliaDir.resolve(version).absolutePath)
        jarUrl.set(LaunchMinecraftServerTask.JarUrl.Folia(version))
        agreeEula.set(true)
    }
}

private fun copies(version: String, workDir: File) {
    delete {
        delete(fileTree(workDir.resolve("$version/plugins")).filter {
            it.isFile() && it.extension == "jar" && it.parentFile == workDir.resolve("$version/plugins")
        })
    }

    // Copy the ops.json file to the server directory
    copy {
        from(rootDir.resolve("resources").resolve("ops.json"))
        into(workDir.resolve(version))
    }

    // Copy the jar file to the plugins directory
    copy {
        from(tasks.shadowJar.get().archiveFile.get().asFile.absolutePath)
        into(workDir.resolve("$version/plugins"))
    }

    // Copy the plugins to the plugins directory
    copy {
        from(fileTree(rootDir.resolve("resources")).filter {
            it.isFile() && it.extension == "jar" && it.nameWithoutExtension.startsWith("pl-")
        })
        into(workDir.resolve("$version/plugins"))
    }
}

// ------------------------ MODRINTH ------------------------
tasks.modrinth {
    dependsOn(tasks.build)
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("vdNwyPFz")
    versionNumber.set(project.version.toString())
    versionName.set("Better Mending ${project.version}")
    versionType.set("release")
    changelog.set(NewGithubChangelog.getChangelog())
    uploadFile.set(tasks.shadowJar.get().archiveFile.get().asFile)
    gameVersions.addAll(listOf("1.21.11", "1.21.10", "1.21.9", "1.21.8", "1.21.7", "1.21.6", "1.21.5", "1.21.4", "1.21.3", "1.21.2", "1.21.1", "1.21", "1.20.6", "1.20.5", "1.20.4", "1.20.3", "1.20.2", "1.20.1", "1.20", "1.19.4", "1.19.3", "1.19.2", "1.19.1", "1.19", "1.18.2", "1.18.1", "1.18", "1.17.1", "1.17", "1.16.5"))
    loaders.addAll(listOf("bukkit", "folia", "paper", "purpur", "spigot"))
}

// ------------------------ MISC ------------------------
tasks.register<Task>("changelog") {
    doLast {
        val changelog = NewGithubChangelog.getChangelog()
        println(changelog)
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_16)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(16)
}

tasks.build {
    mustRunAfter(tasks.clean)
    dependsOn(tasks.clean)

    dependsOn(tasks.shadowJar)
    dependsOn(tasks.named("sourcesJar"))
}

kotlin {
    jvmToolchain(21)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Sources jar
tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}