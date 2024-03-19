plugins {
    id("fabric-loom")
}

class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
}

val mod = ModData()
val mcVersion = stonecutter.current.version
val mcDep = property("mod.mc_dep").toString()

version = "${mod.version}+$mcVersion"
group = mod.group
base { archivesName.set(mod.id) }

repositories {
    maven("https://maven.enjarai.dev/releases")
    maven("https://maven.enjarai.dev/mirrors")
    maven("https://maven.terraformersmc.com")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.wispforest.io")
}

dependencies {
    minecraft("com.mojang:minecraft:${mcVersion}")
    mappings("net.fabricmc:yarn:${mcVersion}+build.${property("deps.yarn_build")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("com.terraformersmc:modmenu:${property("deps.modmenu")}")

    include(modImplementation("nl.enjarai:cicada-lib:${property("deps.cicada")}") {
        exclude(group = "net.fabricmc.fabric-api")
    })
    annotationProcessor(modImplementation("io.wispforest:owo-lib:${property("deps.owo")}")!!)
    include("io.wispforest:owo-sentinel:${property("deps.owo")}")
}

loom {
    runConfigs["client"].apply {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true")
        runDir = "../../run"
    }
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"

        dependsOn(tasks.named("build"))
    }
}

tasks.processResources {
    inputs.property("id", mod.id)
    inputs.property("name", mod.name)
    inputs.property("version", mod.version)
    inputs.property("mcdep", mcDep)

    val map = mapOf(
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "mcdep" to mcDep
    )

    filesMatching("fabric.mod.json") { expand(map) }
}

java {
    withSourcesJar()
}
