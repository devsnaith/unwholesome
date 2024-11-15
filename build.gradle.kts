plugins {
    id("pl.allegro.tech.build.axion-release") version "1.18.7"
    eclipse
    java
}

version = scmVersion.version
tasks.register<Copy>("librariesToJar") {
    from(configurations.runtimeClasspath)
    into("$buildDir/libs/libraries")
}


tasks.jar.configure {
    exclude("meow.conf")
    exclude("Icon.ico")
    exclude("Audios/")
}

tasks.jar {
    dependsOn("librariesToJar")
    manifest {
        attributes["Built-By"]      = "https://github.com/devsnaith/"
        attributes["Main-Class"]    = "com.github.devsnaith.unwholesome.Unwholesome"
        attributes["Class-Path"]    = configurations.runtimeClasspath.get().joinToString(" ") { "libraries/"+it.name }
    }
    doLast{
        copy {
            from(sourceSets.main.get().output.resourcesDir.toString() + "/Audios")
            into("$buildDir/libs/Audios")
        }
        copy {
            from(sourceSets.main.get().output.resourcesDir.toString() + "/meow.conf")
            into("$buildDir/libs")
        }
    }
}

repositories {
    maven(url="https://clojars.org/repo")
    mavenCentral() 
}

dependencies {
    implementation("net.java.dev.jna:jna-platform:5.13.0")
    implementation("net.beadsproject:beads:3.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
