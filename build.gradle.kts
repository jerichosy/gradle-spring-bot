plugins {
    java

    //Spring plugin
    id("org.springframework.boot") version ("2.5.4")

    id("io.spring.dependency-management") version ("1.0.11.RELEASE")
}

group = "com.novamaday.d4j.gradle"
version = "2021.08.31"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.discord4j:discord4j-core:3.2.6")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.springframework.boot:spring-boot-starter-web:2.5.4")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.apache.commons:commons-lang3:3.17.0")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.testcontainers:testcontainers:1.20.2")
    testImplementation("org.mockito:mockito-core:3.+")
}
