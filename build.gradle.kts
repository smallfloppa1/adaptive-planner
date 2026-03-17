plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    jacoco
}

group = "com.floppahost"
version = "0.0.1-SNAPSHOT"
description = "adaptive-planner"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Platforms / BOMs
    implementation(platform(libs.spring.dotenv.bom))

    // Implementation
    implementation(libs.flyway.database.postgresql)
    implementation(libs.mapstruct)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.telegram.client)
    implementation(libs.telegram.starter)

    // Compile Only
    compileOnly(libs.lombok)

    // Runtime Only
    runtimeOnly(libs.postgresql)

    // Development Only
    developmentOnly(libs.spring.boot.devtools)
    developmentOnly(libs.springboot4.dotenv)

    // Annotation Processors
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.lombok.mapstruct.binding)
    annotationProcessor(libs.mapstruct.processor)

    // Test Implementation Platforms
    testImplementation(platform(libs.testcontainers.bom))

    // Test Implementation
    testImplementation(libs.spring.boot.starter.data.jpa.test)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)

    // Test Runtime Only
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

//    classDirectories.setFrom(
//        files(classDirectories.files.map {
//            fileTree(it) {
//                exclude("**/AdaptivePlannerApplication.class")
//                exclude("**/*MapperImpl.class")
//            }
//        })
//    )
}