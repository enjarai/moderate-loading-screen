plugins {
    id("fabric-loom")
    id("me.modmuss50.mod-publish-plugin") version "0.4.4"
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
    maven("https://maven.kikugie.dev/releases")
}

val javaVersion : Int = (property("deps.java") as String).toInt()

tasks.withType<JavaCompile> {
    options.release = javaVersion
}

dependencies {
    minecraft("com.mojang:minecraft:${mcVersion}")
    mappings("net.fabricmc:yarn:${mcVersion}+build.${property("deps.yarn_build")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")}")
    modImplementation("com.terraformersmc:modmenu:${property("deps.modmenu")}")

    modImplementation("nl.enjarai:cicada-lib:${property("deps.cicada")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    annotationProcessor(modImplementation("io.wispforest:owo-lib:${property("deps.owo")}")!!)
    include("io.wispforest:owo-sentinel:${property("deps.owo")}")

//    include(modRuntimeOnly("dev.kikugie:crash-pipe:0.1.0")!!)
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

publishMods {
    file = tasks.remapJar.get().archiveFile
    displayName = "${mod.version} for $mcVersion"
    version = project.version.toString()
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE
    modLoaders.add("fabric")

    val min = property("mod.target_min").toString()
    val max = property("mod.target_max").toString()

    if (providers.gradleProperty("enjaraiModrinthToken").isPresent) {
        modrinth {
            projectId = property("mod.modrinth").toString()
            accessToken = providers.gradleProperty("enjaraiModrinthToken").get()

            if (min == max) {
                minecraftVersions.add(min)
            } else {
                minecraftVersionRange {
                    start = min
                    end = max
                }
            }

            requires {
                slug = "fabric-api"
            }
            requires {
                slug = "cicada"
            }
        }
    }

    if (providers.gradleProperty("enjaraiCurseforgeToken").isPresent) {
        curseforge {
            projectId = property("mod.curseforge").toString()
            accessToken = providers.gradleProperty("enjaraiCurseforgeToken").get()

            if (min == max) {
                minecraftVersions.add(min)
            } else {
                minecraftVersionRange {
                    start = min
                    end = max
                }
            }

            requires {
                slug = "fabric-api"
            }
            requires {
                slug = "cicada"
            }
        }
    }

    if (providers.gradleProperty("enjaraiGithubToken").isPresent) {
        github {
            repository = property("mod.github").toString()
            accessToken = providers.gradleProperty("enjaraiGithubToken").get()

            commitish = "main" // property('git_branch')
            tagName = project.version.toString()
        }
    }
}
