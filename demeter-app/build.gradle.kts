plugins {
    id("io.quarkus")
}

val testcontainersVersion: String by project

val availableModules = linkedSetOf(
    "common",
    "productos",
    "inventario",
    "ventas",
    "costos",
    "usuarios",
    "ubicaciones",
    "empaquetado",
    "precios",
    "analytics",
    "fotos",
    "chatbot"
)

val selectedModules: Set<String> = run {
    val raw = providers.gradleProperty("demeter.modules").orNull?.trim()
    if (raw.isNullOrEmpty() || raw.equals("all", ignoreCase = true)) {
        availableModules
    } else {
        val requested = raw
            .split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .toSet()

        require(requested.isNotEmpty()) {
            "Property demeter.modules is empty. Use 'all' or a comma-separated list."
        }

        val unknown = requested - availableModules
        require(unknown.isEmpty()) {
            "Unknown modules in demeter.modules: ${unknown.joinToString(", ")}. " +
                "Valid values: ${availableModules.joinToString(", ")}"
        }

        requested + "common"
    }
}

logger.lifecycle("[demeter-app] Active modules: ${selectedModules.joinToString(", ")}")

dependencies {
    selectedModules.forEach { module ->
        val modulePath = ":demeter-$module"
        implementation(project(modulePath))
    }

    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-smallrye-health")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.quarkus:quarkus-test-security")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.testcontainers:testcontainers:${testcontainersVersion}")
    testImplementation("org.testcontainers:postgresql:${testcontainersVersion}")
    testImplementation("org.testcontainers:junit-jupiter:${testcontainersVersion}")
}

tasks.register("printDemeterModules") {
    group = "help"
    description = "Print the modules selected for demeter-app."
    doLast {
        println("demeter.modules=${selectedModules.joinToString(",")}")
    }
}
