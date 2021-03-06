/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import gradlebuild.integrationtests.integrationTestUsesSampleDir

plugins {
    id("gradlebuild.distribution.api-java")
}

dependencies {
    implementation(project(":baseServices"))
    implementation(project(":logging"))
    implementation(project(":processServices"))
    implementation(project(":coreApi"))
    implementation(project(":modelCore"))
    implementation(project(":core"))
    implementation(project(":fileCollections"))
    implementation(project(":plugins"))
    implementation(project(":dependencyManagement"))
    implementation(project(":publish"))
    implementation(project(":maven"))
    implementation(project(":security"))

    implementation(libs.groovy)
    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.inject)

    testImplementation(project(":ivy"))
    testImplementation(testFixtures(project(":core")))

    testRuntimeOnly(testFixtures(project(":security")))
    testRuntimeOnly(project(":distributionsPublishing")) {
        because("ProjectBuilder tests load services from a Gradle distribution.")
    }

    integTestDistributionRuntimeOnly(project(":distributionsPublishing"))
}

strictCompile {
    ignoreRawTypes() // raw types used in public API
}

classycle {
    excludePatterns.set(listOf("org/gradle/plugins/signing/**"))
}

integrationTestUsesSampleDir("subprojects/signing/src/main")
