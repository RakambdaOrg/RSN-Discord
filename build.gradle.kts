plugins {
    idea
    java
    application
    alias(libs.plugins.names)
    alias(libs.plugins.jib)
    alias(libs.plugins.sptringbootDependencyManagement)
    alias(libs.plugins.sptringboot)
}

group = "fr.rakambda"
description = "RSNDiscord"

configurations {
    compileOnly {
        extendsFrom(configurations["annotationProcessor"])
    }
}

dependencies {
    implementation(libs.jda) {
        exclude(module = "opus-java")
        isChanging = true
    }
    implementation(libs.bundles.opus)
    implementation(libs.bundles.lavalink)

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    modules {
        module("org.springframework.boot:spring-boot-starter-logging") {
            replacedBy("org.springframework.boot:spring-boot-starter-log4j2", "Use Log4j2 instead of Logback")
        }
    }
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")
    compileOnly(libs.jetbrainsAnnotations)

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

repositories {
    mavenCentral()
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
    maven {
        name = "Lavalink"
        url = uri("https://maven.lavalink.dev/releases")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>(){
    options.compilerArgs.addAll(listOf("-Xlint:deprecation"))
}

springBoot {
    val className: String by project

    mainClass.set(className)
}

jib {
    from {
        image = "eclipse-temurin:21-jdk"
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
    container {
        val className: String by project

        creationTime.set("USE_CURRENT_TIMESTAMP")
        mainClass = className

        ports = listOf("8080")
    }
}
