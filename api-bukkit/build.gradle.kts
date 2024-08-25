import java.net.URI
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    idea
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    api(project(":api"))
    //api("dev.aurelium:slate:1.1.7") {
    //    exclude("org.yaml", "snakeyaml")
    //    exclude("org.spongepowered", "configurate-yaml")
    //}
    // api(files("../../Slate/build/libs/Slate-1.1.7.jar"))
    api("org.spongepowered:configurate-yaml:4.1.2") {
        exclude("org.yaml", "snakeyaml")
    }
    api("net.kyori:adventure-text-minimessage:4.16.0")
    api("net.kyori:adventure-platform-bukkit:4.3.2")
    //
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    //
    compileOnly("dev.folia:folia-api:1.21-R0.1-SNAPSHOT")
    //compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.10.10")
    compileOnly("com.mojang:authlib:1.5.25")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks {
    javadoc {
        title = "AuraSkills API Bukkit (${project.version})"
        source = sourceSets.main.get().allSource + project(":api").sourceSets.main.get().allSource
        classpath = files(sourceSets.main.get().compileClasspath, project(":api").sourceSets.main.get().output)
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
            overview("javadoc/overview.html")
            encoding("UTF-8")
            charset("UTF-8")
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
}

if (project.hasProperty("sonatypeUsername") && project.hasProperty("sonatypePassword")) {
    publishing {
        repositories {
            maven {
                val releasesRepoUrl = URI.create("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = URI.create("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                credentials {
                    username = project.property("sonatypeUsername").toString()
                    password = project.property("sonatypePassword").toString()
                }
            }
        }

        publications.create<MavenPublication>("mavenJava") {
            groupId = "dev.aurelium"
            artifactId = "auraskills-api-bukkit"
            version = project.version.toString()

            pom {
                name.set("AuraSkills API Bukkit")
                description.set("API for AuraSkills, the ultra-versatile RPG skills plugin for Minecraft")
                url.set("https://wiki.aurelium.dev/auraskills")
                licenses {
                    license {
                        name.set("The GNU General Public License, Version 3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    }
                }
                developers {
                    developer {
                        id.set(project.property("developerId").toString())
                        name.set(project.property("developerUsername").toString())
                        email.set(project.property("developerEmail").toString())
                        url.set(project.property("developerUrl").toString())
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Archy-X/AuraSkills.git")
                    developerConnection.set("scm:git:git://github.com/Archy-X/AuraSkills.git")
                    url.set("https://github.com/Archy-X/AuraSkills/tree/master")
                }
            }

            from(components["java"])
        }
    }

    signing {
        useGpgCmd()
        sign(publishing.publications.getByName("mavenJava"))
        isRequired = true
    }
}
