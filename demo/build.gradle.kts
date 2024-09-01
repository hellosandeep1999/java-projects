
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property


plugins {
	java
	id("org.springframework.boot") version "2.5.4"
	id("io.spring.dependency-management") version "1.1.0"
	id("nu.studer.jooq") version "5.2.1"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("io.springfox:springfox-boot-starter:3.0.0")
	compileOnly("io.springfox:springfox-swagger-ui:3.0.0")

	implementation("mysql:mysql-connector-java")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.jooq:jooq-meta-extensions-hibernate")
	implementation("org.springframework.boot:spring-boot-starter-jooq:2.7.1")

	runtimeOnly("org.jooq:jooq")
	jooqGenerator("org.jooq:jooq-meta-extensions")


	implementation("org.flywaydb:flyway-core:6.4.3")

	// pdf files
	implementation("com.itextpdf:itextpdf:5.5.13.3")


	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")

	implementation("com.squareup.okhttp3:okhttp:4.10.0")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.apache.poi:poi-ooxml:5.2.3")


}


jooq {
	version.set("3.15.3")
	configurations {
		create("main") {
			jooqConfiguration.apply {
				logging = Logging.WARN
				jdbc = null
				generator.apply {
					name = "org.jooq.codegen.DefaultGenerator"
					database.apply {
						name = "org.jooq.meta.extensions.ddl.DDLDatabase"
						includes = ".*"
						excludes = ""
						properties.addAll(arrayOf(
								Property().withKey("scripts").withValue("src/main/resources/db/migration/*.sql"),
								Property().withKey("sort").withValue("flyway"),
								Property().withKey("unqualifiedSchema").withValue("none"),
								Property().withKey("defaultNameCase").withValue("as_is")
						))
					}
					target.apply {
						packageName = "com.example.demo.generated.jooq"
					}
				}
			}
		}
	}
}


tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.compileJava {
	dependsOn(tasks.named("generateJooq"))
}

tasks.jar {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	from({
		// Include runtime classpath entries
		configurations.runtimeClasspath.get().flatMap { file ->
			if (file.isDirectory) {
				files(file)
			} else {
				zipTree(file)
			}
		}

	})
	manifest {
		attributes["Main-Class"] = "com.example.demo.DemoApplication"
	}

	exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")

}

