plugins {
    id 'java-gradle-plugin'
    id 'groovy'
}

publishing {
    publications {
        maven(MavenPublication) {
            artifactId = "${project.name}"
            from components.java
            pom {
                name = 'GraphQL Model Generator Plugin'
                description = 'This plugin helps to generate java POJO models for GraphQL Request Body Generator library.'
                url = 'https://github.com/VladislavSevruk/GraphQlRequestBodyGenerator/graphql-model-generator-plugin'
                licenses {
                    license {
                        name = 'MIT License'
                        url = 'https://opensource.org/licenses/MIT'
                    }
                }
                developers {
                    developer {
                        id = 'uladzislau_seuruk'
                        name = 'Uladzislau Seuruk'
                        email = 'vladislavsevruk@gmail.com'
                    }
                }
                scm {
                    connection = 'scm:git:git://VladislavSevruk/GraphQlRequestBodyGenerator.git'
                    developerConnection = 'scm:git:ssh://VladislavSevruk/GraphQlRequestBodyGenerator.git'
                    url = 'https://github.com/VladislavSevruk/GraphQlRequestBodyGenerator/tree/master'
                }
            }
        }
    }
}

repositories {
    mavenCentral()
}

sourceSets {
    integrationTest {
        compileClasspath += sourceSets.main.output + sourceSets.test.output
        runtimeClasspath += sourceSets.main.output + sourceSets.test.output
    }
    functionalTest {
        compileClasspath += sourceSets.main.output
        runtimeClasspath += sourceSets.main.output
    }
}

tasks.register('integrationTest', Test) {
    description = 'Runs integration tests.'
    group = 'verification'
    shouldRunAfter test
}

tasks.register('functionalTest', Test) {
    description = 'Runs functional tests.'
    group = 'verification'
    shouldRunAfter integrationTest
}

configurations {
    integrationTestImplementation.extendsFrom testImplementation
    integrationTestRuntimeOnly.extendsFrom testRuntimeOnly
}

check.dependsOn integrationTest, functionalTest

integrationTest {
    testClassesDirs = sourceSets.integrationTest.output.classesDirs
    classpath = sourceSets.integrationTest.runtimeClasspath
    useJUnitPlatform()
}

functionalTest {
    testClassesDirs = sourceSets.functionalTest.output.classesDirs
    classpath = sourceSets.functionalTest.runtimeClasspath
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        simplePlugin {
            id = "${group}.${project.name}"
            implementationClass = 'com.github.vladislavsevruk.generator.model.graphql.GqlModelGeneratorPlugin'
        }
    }
    testSourceSets(sourceSets.functionalTest)
}

dependencies {
    compileOnly(
            "org.projectlombok:lombok:${lombokVersion}"
    )
    annotationProcessor(
            "org.projectlombok:lombok:${lombokVersion}"
    )
    implementation (
            "org.apache.logging.log4j:log4j-api:${log4jVersion}",
            "org.apache.logging.log4j:log4j-core:${log4jVersion}",
            "com.github.vladislavsevruk:java-class-generator:${javaClassGeneratorVersion}"
    )
    testCompileOnly(
            "org.projectlombok:lombok:${lombokVersion}"
    )
    testAnnotationProcessor(
            "org.projectlombok:lombok:${lombokVersion}"
    )
    testImplementation (
            "org.junit.jupiter:junit-jupiter-api:${junitVersion}",
            "org.junit.jupiter:junit-jupiter-params:${junitVersion}",
            "org.mockito:mockito-core:${mockitoVersion}",
            "org.mockito:mockito-junit-jupiter:${mockitoVersion}"
    )
    testRuntimeOnly (
            "org.junit.jupiter:junit-jupiter-engine:${junitVersion}"
    )
    functionalTestImplementation (
            platform("org.spockframework:spock-bom:${spockVersion}"),
            'org.spockframework:spock-core'
    )
}

jacocoTestReport {
    dependsOn test, integrationTest, functionalTest
    executionData fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec")
    reports {
        xml.enabled true
        html.enabled true
    }
}
