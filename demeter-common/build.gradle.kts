plugins {
    `java-library`
}

dependencies {
    api("io.quarkus:quarkus-hibernate-orm-panache")
    api("io.quarkus:quarkus-jdbc-postgresql")
    api("io.quarkus:quarkus-oidc")
    api("io.quarkus:quarkus-smallrye-jwt")
    api("io.quarkus:quarkus-rest-jackson")
    api("io.quarkus:quarkus-hibernate-validator")
    api("io.quarkus:quarkus-smallrye-openapi")
    api("io.quarkus:quarkus-flyway")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.quarkus:quarkus-junit5-mockito")
}
