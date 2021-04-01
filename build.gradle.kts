plugins {
    idea
    java
    application
    id("com.github.johnrengelman.shadow") version ("6.1.0")
    id("com.github.ben-manes.versions") version ("0.38.0")
    id("io.freefair.lombok") version ("5.3.0")
}

group = "fr.raksrinana"
description = "RSNDiscord"

dependencies {
    implementation(libs.jda) {
        exclude(module = "opus-java")
    }
    implementation(libs.lavaplayerD)
    implementation(libs.lavaplayerArm)
    implementation(libs.jump3r)

    implementation(libs.slf4j)
    implementation(libs.logback) {
        exclude(group = "edu.washington.cs.types.checker", module = "checker-framework")
    }

    implementation(libs.unirest)
    implementation(libs.picocli)
    implementation(libs.bundles.jackson)
    implementation(libs.httpclient)
    implementation(libs.lang3)
    implementation(libs.reflections)
    implementation(libs.emojiJava)
    implementation(libs.imgscalr)
    implementation(libs.thumbnailator)
    implementation(libs.twittered)

    compileOnly(libs.jetbrainsAnnotations)
}

repositories {
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
    maven {
        url = uri("https://projectlombok.org/edge-releases")
    }
    mavenCentral()
    jcenter()
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
            compilerArgs.add("--module-path")
            compilerArgs.add(classpath.asPath)
            classpath = files()
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

    mainClassName = className
    mainModule.set(moduleName)
    mainClass.set(className)
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

lombok {
    version.set("edge-SNAPSHOT")
}
