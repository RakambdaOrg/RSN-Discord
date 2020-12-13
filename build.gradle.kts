plugins {
    idea
    java
    application
    id("com.github.johnrengelman.shadow") version PluginVersions.SHADOW_VERSION
    id("com.github.ben-manes.versions") version PluginVersions.VERSIONS_VERSION
    id("io.freefair.lombok") version PluginVersions.LOMBOK_VERSION
}

group = "fr.raksrinana"
description = "RSNDiscord"

dependencies {
    implementation("net.dv8tion:JDA:${DependencyVersions.JDA_VERSION}") {
        exclude(module = "opus-java")
    }
    implementation("com.sedmelluq:lavaplayer:${DependencyVersions.LAVAPLAYER_VERSION}")
    implementation("com.github.natanbc:lavaplayer-arm-natives:${DependencyVersions.LAVAPLAYER_ARM_VERSION}")
    implementation("de.sciss:jump3r:${DependencyVersions.JUMP3R_VERSION}")

    implementation("org.slf4j:slf4j-api:${DependencyVersions.SLF4J_VERSION}")
    implementation("ch.qos.logback:logback-classic:${DependencyVersions.LOGBACK_VERSION}") {
        exclude(group = "edu.washington.cs.types.checker", module = "checker-framework")
    }

    implementation("com.konghq:unirest-java:${DependencyVersions.UNIREST_VERSION}")
    implementation("info.picocli:picocli:${DependencyVersions.PICOCLI_VERSION}")
    implementation("com.fasterxml.jackson.core:jackson-core:${DependencyVersions.JACKSON_VERSION}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${DependencyVersions.JACKSON_VERSION}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${DependencyVersions.JACKSON_VERSION}")
    implementation("org.apache.httpcomponents:httpclient:${DependencyVersions.HTTPCLIENT_VERSION}")
    implementation("org.apache.commons:commons-lang3:${DependencyVersions.LANG3_VERSION}")
    implementation("org.reflections:reflections:${DependencyVersions.REFLECTIONS_VERSION}")
    implementation("com.vdurmont:emoji-java:${DependencyVersions.EMOJI_JAVA_VERSION}")
    implementation("org.imgscalr:imgscalr-lib:${DependencyVersions.IMGSCALR_VERSION}")
    implementation("net.coobird:thumbnailator:${DependencyVersions.THUMBNAILATOR_VERSION}")
    implementation("org.twitter4j:twitter4j-core:${DependencyVersions.TWITTER4J_VERSION}")
}

repositories {
    jcenter()
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
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
    modularity.inferModulePath.set(true)
}
