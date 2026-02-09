plugins {
    id("io.quarkus")
}

val testcontainersVersion: String by project

dependencies {
    implementation(project(":demeter-common"))
    implementation(project(":demeter-productos"))
    implementation(project(":demeter-inventario"))
    implementation(project(":demeter-ventas"))
    implementation(project(":demeter-costos"))
    implementation(project(":demeter-usuarios"))
    implementation(project(":demeter-ubicaciones"))
    implementation(project(":demeter-empaquetado"))
    implementation(project(":demeter-precios"))
    implementation(project(":demeter-analytics"))
    implementation(project(":demeter-fotos"))
    implementation(project(":demeter-chatbot"))

    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-smallrye-health")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.testcontainers:testcontainers:${testcontainersVersion}")
    testImplementation("org.testcontainers:postgresql:${testcontainersVersion}")
    testImplementation("org.testcontainers:junit-jupiter:${testcontainersVersion}")
}
