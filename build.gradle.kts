import org.gradle.kotlin.dsl.support.zipTo

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

    main {
        contents {
            into("applications/maximo/lib") {
                from("$buildDir/libs/${product.toLowerCase()}.jar")
            }
            into("applications/maximo/lib") {
                from(distribution.filter { it.name.startsWith("guava") })
            }
            into("applications/maximo/maximouiweb/webmodule/WEB-INF/lib") {
                from("$buildDir/libs/${product.toLowerCase()}-web.jar")
            }

            into("tools/maximo/classes") {
                includeEmptyDirs = false
                from(layout.buildDirectory.dir("classes/java/main")) {
                    include("psdi/zebralabel/en/*.class")
                }
            }
        }
    }
}

// Configure the distribution task to tar and gzip the results.
tasks.distTar {
    rootSpec
    exclude("tools/maximo/classes/psdi/zebralabel/en/images")
    exclude("tools/maximo/classes/psdi/zebralabel/en/README.md")
    exclude("tools/maximo/classes/psdi/zebralabel/en/resources/manifest.json")
    exclude("tools/maximo/en/zebralabel/script.dtd")
    compression = Compression.GZIP
    archiveExtension.set("tar.gz")
}


tasks.distZip {
    println("Root " + archiveFile.get().asFile.path)
    exclude("tools/maximo/classes/psdi/zebralabel/en/images")
    exclude("tools/maximo/classes/psdi/zebralabel/en/README.md")
    exclude("tools/maximo/classes/psdi/zebralabel/en/resources/manifest.json")
    exclude("tools/maximo/en/zebralabel/script.dtd")

}

tasks.assembleDist {
    finalizedBy("fixzip")
}

tasks.register("fixzip"){
    dependsOn("rezip", "retar")

    doLast{
        delete(layout.buildDirectory.asFile.get().path + File.separator + "distributions" + File.separator + "tmp")
    }

}

tasks.register("unzip") {
    val archiveBaseName = project.name + "-" + project.version
    val distDir = layout.buildDirectory.asFile.get().path + File.separator + "distributions"

    doLast {
        copy {
            from(zipTree(tasks.distZip.get().archiveFile.get().asFile))
            into(distDir + File.separator + "tmp")
        }
    }
}

tasks.register<Zip>("rezip"){
    dependsOn("unzip")
    val archiveBaseName = project.name + "-" + project.version
    val distDir = layout.buildDirectory.asFile.get().path + File.separator + "distributions"
    val baseDir = File(distDir + File.separator + "tmp" + File.separator + archiveBaseName )

    archiveFileName.set("$archiveBaseName.zip")

    from(baseDir){
        into("/")
    }
}

tasks.register<Tar>("retar"){
    dependsOn("unzip")
    val archiveBaseName = project.name + "-" + project.version
    val distDir = layout.buildDirectory.asFile.get().path + File.separator + "distributions"
    val baseDir = File(distDir + File.separator + "tmp" + File.separator + archiveBaseName )

    compression = Compression.GZIP
    archiveExtension.set("tar.gz")

    from(baseDir){
        into("/")
    }
}

tasks.register<Zip>("testzip"){
    val archiveBaseName = project.name + "-" + project.version
    val distDir = layout.buildDirectory.asFile.get().path + File.separator + "distributions"

    val outputFile = File(distDir + File.separator + archiveBaseName + "-new.zip")
    val baseDir = File(distDir + File.separator + "tmp" + File.separator + archiveBaseName)
    val children = baseDir.walkTopDown().filter { it.isFile }

    from(baseDir)
    to(outputFile)
}


tasks.getByName("unzip").dependsOn("assembleDist")


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

    implementation("com.google.guava:guava:31.0.1-jre")

    compileOnly("javax.servlet:javax.servlet-api:4.0.1")
    compileOnly("com.ibm.maximo:asset-management:7.6.1.2")
    compileOnly("com.ibm.maximo:webclient:7.6.1.2")
    compileOnly("com.ibm.maximo:tools:7.6.0.0")

}