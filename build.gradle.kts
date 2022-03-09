plugins {
    java
    distribution
}

val archivaUserName: String by project
val archivaPassword: String by project

group = "io.sharptree"
version = "1.0.0"

val vendor = "Sharptree"
val product = "zebra-label"
val distro = "zebra-label"

project.version = "1.0.0"

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

repositories {
    mavenCentral()
    maven {
        name = "sharptree"
        url = uri("https://archiva.sharptree.dev/repository/private")
        credentials {
            username = archivaUserName
            password = archivaPassword
        }
    }
}

distributions {
    val distribution by configurations.creating {
        extendsFrom(configurations.implementation.get())
        isCanBeResolved = true
    }

    val distributionRuntime by configurations.creating {
        extendsFrom(configurations.runtimeOnly.get())
        isCanBeResolved = true
    }

    main {
        distributionBaseName.set(distro.toLowerCase())
        contents {
            into("maximo/applications/maximo/lib") {
                from("$buildDir/libs/${product.toLowerCase()}.jar")
            }
            into("maximo/applications/maximo/maximouiweb/webmodule/WEB-INF/lib") {
                from("$buildDir/libs/${product.toLowerCase()}-web.jar")
            }

            into("maximo/tools/maximo/classes") {
                includeEmptyDirs = false
                from(layout.buildDirectory.dir("classes/java/main")) {
                    include("psdi/barcode-print/en/*.class")
                }
            }
        }
    }
}

// Configure the distribution task to tar and gzip the results.
tasks.distTar{
    compression = Compression.GZIP
    archiveExtension.set("tar.gz")
}

tasks.jar {
    archiveFileName.set("${product.toLowerCase()}.jar")
}

tasks.getByName("distTar").dependsOn("jar", "jar-web")
tasks.getByName("distZip").dependsOn("jar", "jar-web")


tasks.register<Jar>("jar-web") {
    archiveFileName.set("${product.toLowerCase()}-web.jar")
    include("io/sharptree/maximo/webclient/**")

    from(project.the<SourceSetContainer>()["main"].output)
}

tasks.getByName("assemble").dependsOn("jar-web")

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to product,
                "Created-By" to vendor,
                "Implementation-Version" to project.version
            )
        )
    }

    exclude("io/sharptree/maximo/webclient/**")

    archiveBaseName.set(product.toLowerCase())

}

dependencies {

    compileOnly("javax.servlet:javax.servlet-api:4.0.1")
    compileOnly("com.ibm.maximo:asset-management:7.6.1.2")
    compileOnly("com.ibm.maximo:webclient:7.6.1.2")

    compileOnly("com.ibm.maximo:tools:7.6.0.0")

}