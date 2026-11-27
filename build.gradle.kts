import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.4.21"
    id("dev.kikugie.loom-back-compat")
    id("maven-publish")
}

version = "${sc.properties["mod.version"]}+${sc.current.version}"
base.archivesName = sc.properties["mod.id"] as String

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    else -> JavaVersion.VERSION_21
}

repositories {
    maven("https://maven.quiltmc.org/repository/release") { name = "Quilt" }
    exclusiveContent {
        forRepository { maven("https://maven.isxander.dev/releases") }
        filter { includeGroup("dev.isxander") }
    }
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    if (sc.current.parsed >= "26.1") {
        loomx.applyMojangMappings()
    } else {
        mappings("net.fabricmc:yarn:${sc.properties["deps.yarn"]}:v2")
    }

    modImplementation("net.fabricmc:fabric-loader:${sc.properties["deps.fabric_loader"]}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${sc.properties["deps.kotlin_loader"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${sc.properties["deps.fabric_api"]}")
    modImplementation("dev.isxander:yet-another-config-lib:${sc.properties["deps.yacl"]}")
    modImplementation("maven.modrinth:mOgUt4GM:${sc.properties["deps.modmenu"]}")
}

java {
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion.toInt())
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(requiredJava.majorVersion.toInt())
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(requiredJava.majorVersion))
}

tasks.processResources {
    val props = buildMap {
        fun register(key: String, value: String) {
            inputs.property(key, value)
            put(key, value)
        }
        register("version", project.version.toString())
        register("minecraft", sc.properties["mod.mc_compat"] as String)
        register("loader", Regex("\\d+\\.\\d+").find(sc.properties["deps.fabric_loader"] as String)!!.value)
        register("kotlin_loader_version", sc.properties["deps.kotlin_loader"] as String)
        register("modmenu_version", sc.properties["deps.modmenu"] as String)
        register("yacl_version", sc.properties["deps.yacl"] as String)
        register("java", requiredJava.majorVersion.toString())
    }
    filteringCharset = "UTF-8"
    filesMatching("fabric.mod.json") { expand(props) }
    filesMatching("*.mixins.json") { expand("java" to requiredJava.majorVersion.toString()) }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    description = "Builds the mod jar and copies it to build/libs/<mod version>/"
    from(loomx.modJar.flatMap { it.archiveFile })
    into(rootProject.layout.buildDirectory.dir("libs/${sc.properties["mod.version"]}"))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}