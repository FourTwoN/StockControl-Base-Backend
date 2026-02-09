pluginManagement {
    val quarkusVersion: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("io.quarkus") version quarkusVersion
    }
}

rootProject.name = "demeter-backend"

include(
    "demeter-common",
    "demeter-productos",
    "demeter-inventario",
    "demeter-ventas",
    "demeter-costos",
    "demeter-usuarios",
    "demeter-ubicaciones",
    "demeter-empaquetado",
    "demeter-precios",
    "demeter-analytics",
    "demeter-fotos",
    "demeter-chatbot",
    "demeter-app"
)
