val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.2.3"
//    kotlin("plugin.serialization") version "2.1.10"
}

group = "pl.polsl.sikor-falf"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    //Core
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty")
    //Serializaton
    implementation("io.ktor:ktor-server-content-negotiation:3.2.3")
    implementation("io.ktor:ktor-serialization-jackson:3.2.3")
//    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.3")
//    implementation("io.ktor:ktor-server-content-negotiation:3.2.3")
    //Authorization
    implementation("io.ktor:ktor-server-auth:3.2.3")
    implementation("io.ktor:ktor-server-auth-jwt:3.2.3")
    //Anon lib
    compileOnly(files("../libs/libarx-3.9.1.jar"))
    //Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")
    //Unused
    implementation("io.ktor:ktor-server-thymeleaf:3.2.3")
    //Core and testing
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-config-yaml")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    //Database
    implementation("org.jetbrains.exposed:exposed-core:0.53.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.53.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.53.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.53.0")
    //Password encrypt
    implementation("org.mindrot:jbcrypt:0.4")
    //Postgres dependency
    implementation("org.postgresql:postgresql:42.7.3")
}
