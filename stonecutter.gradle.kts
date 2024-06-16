plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.6-SNAPSHOT" apply false
}
stonecutter active "1.19.4" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
}

stonecutter registerChiseled tasks.register("chiseledPublishMods", stonecutter.chiseled) {
    group = "project"
    ofTask("publishMods")
}
