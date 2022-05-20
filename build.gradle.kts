@file:Suppress("KDocUnresolvedReference")

plugins {
    java
    distribution
}


group = "io.sharptree"
version = "1.1.1"

val vendor = "Sharptree"
val product = "zebra-label"
val distro = "zebra-label"

project.version = "1.1.1"

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}


configurations.register("installation").configure {
    extendsFrom(configurations.implementation.get())
    isTransitive = false
}


repositories {
    mavenCentral()
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
    compression = Compression.GZIP
    archiveExtension.set("tar.gz")
}


tasks.distZip {
    exclude("tools/maximo/classes/psdi/zebralabel/en/images")
    exclude("tools/maximo/classes/psdi/zebralabel/en/README.md")
    exclude("tools/maximo/classes/psdi/zebralabel/en/resources/manifest.json")
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
    val distDir = layout.buildDirectory.asFile.get().path + File.separator + "distributions"

    doLast {
        copy {
            from(zipTree(tasks.distZip.get().archiveFile.get().asFile))
            into(distDir + File.separator + "tmp")
        }
        copy {
            from(zipTree(configurations.getByName("installation").filter { it.name.startsWith("semver") }.singleFile))
            into(distDir + File.separator + "tmp/${project.name}-${version}/tools/maximo/classes")
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
        exclude("maximo/**")
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
        exclude("maximo/**")
    }
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

    /*
     * Library used for validating host names and IP addresses.
     */
    implementation("com.google.guava:guava:31.0.1-jre")

    /*
     * The javax.servlet-api is required to compile DataBean classes, but is otherwise provided by WebSphere / WebLogic.
     */
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")

    /**
     * Maximo's libraries are needed for compiling the application.
     *
     * asset-management - the businessobjects.jar
     * webclient - classes from the maximouiweb/WEB-INF/classes folder
     * tools - classes from the [SMP_HOME]/maximo/tools/maximo/classes folder
     *
     *
     * If you are not a Sharptree developer, but have access to a Maximo instance you can zip the required into jar files and
     * places them in the libs directory on this project.  The comment the non-local dependencies.
     */

    compileOnly(fileTree( "libs") { listOf("*.jar") })
    
    compileOnly("com.google.code.gson:gson:2.2.4")

    /**
     * Semantic versioning for checking script versions.
     */
    implementation("com.vdurmont:semver4j:3.1.0")

}