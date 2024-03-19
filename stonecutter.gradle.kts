plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.5-SNAPSHOT" apply false
}
stonecutter active "1.19.4" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
}
