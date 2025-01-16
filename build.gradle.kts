plugins {
    id("java")
    id("com.gradleup.shadow") version "8.+"
    id("com.modrinth.minotaur") version "2.+"
}

group = "ovh.paulem.btm"
version = "2.6.5.1"

repositories {
    mavenCentral()
    mavenLocal()

    maven { url = uri("https://jitpack.io") }
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }
    maven { url = uri("https://repo.jeff-media.com/public/") }

    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/central") }

    maven {
        url = uri("https://maven.paulem.ovh/releases")
    }
}

dependencies {
    implementation("org.bstats:bstats-bukkit:3.+")
    implementation("com.jeff_media:SpigotUpdateChecker:3.+")
    implementation("com.github.Anon8281:UniversalScheduler:0.+")
    implementation("com.github.fierioziy.particlenativeapi:ParticleNativeAPI-core:4.+")

    compileOnly("org.jetbrains:annotations:24.+")
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
}

tasks.withType<JavaCompile>().configureEach {
    JavaVersion.VERSION_1_8.toString().also {
        sourceCompatibility = it
        targetCompatibility = it
    }
    options.encoding = "UTF-8"
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    relocate("org.bstats", "ovh.paulem.btm.libs.bstats")
    relocate("com.jeff_media.updatechecker", "ovh.paulem.btm.libs.updatechecker")
    relocate("com.github.Anon8281.universalScheduler", "ovh.paulem.btm.libs.universalScheduler")
    relocate("com.github.fierioziy.particlenativeapi", "ovh.paulem.btm.libs.particleapi")

    archiveClassifier.set("")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("plugin.yml") {
        expand(mapOf("version" to project.version))
    }
}

tasks.modrinth {
    dependsOn(tasks.build)
}

modrinth {
    token.set(providers.gradleProperty("MODRINTH_TOKEN").getOrElse(""))
    projectId.set("vdNwyPFz")
    versionNumber.set(project.version.toString())
    versionName.set("Better Mending " + project.version.toString())
    versionType.set("release")
    changelog.set("Fixed config reload".replace("\n", "<br>"))
    uploadFile.set(tasks.shadowJar)
    gameVersions.addAll(listOf("1.21.4", "1.21.3", "1.21.2", "1.21.1", "1.21", "1.20.6", "1.20.5", "1.20.4", "1.20.3", "1.20.2", "1.20.1", "1.20", "1.19.4", "1.19.3", "1.19.2", "1.19.1", "1.19", "1.18.2", "1.18.1", "1.18", "1.17.1", "1.17", "1.16.5", "1.16.4", "1.16.3", "1.16.2", "1.16.1", "1.16", "1.15.2", "1.15.1", "1.15", "1.14.4", "1.14.3", "1.14.2", "1.14.1", "1.14", "1.13.2", "1.13.1", "1.13", "1.12.2", "1.12.1", "1.12", "1.11.2", "1.11.1", "1.11", "1.10.2", "1.10.1", "1.10", "1.9.4", "1.9.3", "1.9.2", "1.9.1", "1.9"))
    loaders.addAll(listOf("bukkit", "folia", "paper", "purpur", "spigot"))
}