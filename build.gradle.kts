plugins {
    java
    id("io.quarkus") apply false
}

val quarkusVersion: String by project
val testcontainersVersion: String by project
val restAssuredVersion: String by project

subprojects {
    apply(plugin = "java")

    group = "com.fortytwo.demeter"
    version = "0.1.0"

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:${quarkusVersion}"))
        testImplementation("io.quarkus:quarkus-junit5")
        testImplementation("io.rest-assured:rest-assured")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    val ensureMainResourcesOutput by tasks.registering {
        doLast {
            layout.buildDirectory.dir("resources/main").get().asFile.mkdirs()
        }
    }

    tasks.named("processResources") {
        finalizedBy(ensureMainResourcesOutput)
    }
}
