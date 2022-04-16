plugins {
    idea
    java
    application
    alias(libs.plugins.shadow)
    alias(libs.plugins.names)
    alias(libs.plugins.lombok)
    alias(libs.plugins.jib)
    alias(libs.plugins.gitProperties)
}

group = "fr.raksrinana"
description = "RSNDiscord"

dependencies {
    implementation(libs.jda) {
        exclude(module = "opus-java")
        setChanging(true)
    }
    implementation(libs.bundles.opus)
    implementation(libs.lavaplayer)
    implementation(libs.lpCross)
    implementation(libs.jump3r)

    implementation(libs.slf4j)
    implementation(libs.bundles.log4j2)

    implementation(libs.bundles.unirest)
    implementation(libs.picocli)
    implementation(libs.bundles.jackson)
    implementation(libs.httpclient)
    implementation(libs.lang3)
    implementation(libs.reflections)
    implementation(libs.twittered)
    implementation(libs.kittehIrc)
    implementation(libs.rome)
    implementation(libs.jsoup)

    compileOnly(libs.jetbrainsAnnotations)
}

repositories {
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
    mavenCentral()
}

tasks {
    processResources {
        expand(project.properties)
    }

    compileJava {
        val moduleName: String by project
        inputs.property("moduleName", moduleName)

        options.encoding = "UTF-8"
        options.isDeprecation = true

        doFirst {
            val compilerArgs = options.compilerArgs

            val path = classpath.asPath.split(";")
                .filter { it.endsWith(".jar") }
                .joinToString(";")
            compilerArgs.add("--module-path")
            compilerArgs.add(path)
            classpath = files()
        }
    }

    jar {
        manifest {
            attributes["Multi-Release"] = "true"
        }
    }

    test {
        useJUnitPlatform()
    }

    shadowJar {
        archiveBaseName.set(project.name)
        archiveClassifier.set("shaded")
        archiveVersion.set("")
    }

    wrapper {
        val wrapperVersion: String by project
        gradleVersion = wrapperVersion
    }
}

application {
    val moduleName: String by project
    val className: String by project

    mainModule.set(moduleName)
    mainClass.set(className)
}

java {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18

    modularity.inferModulePath.set(false)
}

jib {
    from {
        image = "eclipse-temurin:18-jdk"
        platforms {
            platform {
                os = "linux"
                architecture = "arm64"
            }
            platform {
                os = "linux"
                architecture = "amd64"
            }
        }
    }
    to {
        image = "mrcraftcod/rsn-discord"
        auth {
            username = project.findProperty("dockerUsername").toString()
            password = project.findProperty("dockerPassword").toString()
        }
    }
    container {
        creationTime = "USE_CURRENT_TIMESTAMP"
    }
}
