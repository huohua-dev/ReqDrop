plugins {
    java
}

repositories {
    mavenCentral()
}

val montoya = "net.portswigger.burp.extensions:montoya-api:2026.4"

dependencies {
    // Burp supplies the API at runtime -> compileOnly, never bundled into the jar.
    compileOnly(montoya)
    // The Montoya-backed adapter is in main; test doubles do not need it, but keep it
    // available to the test compile classpath for any future Montoya-touching test.
    testCompileOnly(montoya)

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Target Java 17 bytecode regardless of the (>=17) build JDK. Records + switch
// expressions require language level 16/14, comfortably covered by 17.
tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("reqdrop")
    archiveVersion.set("")
}
