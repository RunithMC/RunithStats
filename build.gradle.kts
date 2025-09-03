plugins {
    id("java")
    id("com.gradleup.shadow") version "9.1.0"
}

group = "net.runith"
version = "1.0-SNAPSHOT"
description = "A stats plugin for runithmc"

repositories {
    mavenCentral()
    maven("https://maven.elmakers.com/repository")
    maven("https://repo.hpfxd.com/releases")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("com.hpfxd.pandaspigot:pandaspigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
    compileOnly("me.NoChance.PvPManager:pvpmanager:3.18.44")
    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation("org.mongodb:mongodb-driver-sync:5.5.1")
    implementation("org.mongodb:mongodb-driver-core:5.5.1")

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    testCompileOnly("org.projectlombok:lombok:1.18.38")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.38")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.shadowJar {}
tasks.test {
    useJUnitPlatform()
}

allprojects {
    apply<JavaPlugin>()

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
}