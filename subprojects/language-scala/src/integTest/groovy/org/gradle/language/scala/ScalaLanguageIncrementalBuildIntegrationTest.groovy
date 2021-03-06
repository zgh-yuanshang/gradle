/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.language.scala

import org.gradle.integtests.fixtures.UnsupportedWithInstantExecution
import org.gradle.integtests.fixtures.archives.TestReproducibleArchives
import org.gradle.integtests.fixtures.jvm.TestJvmComponent
import org.gradle.integtests.language.AbstractJvmLanguageIncrementalBuildIntegrationTest
import org.gradle.language.scala.fixtures.TestScalaComponent

@TestReproducibleArchives
@UnsupportedWithInstantExecution(because = "software model")
class ScalaLanguageIncrementalBuildIntegrationTest extends AbstractJvmLanguageIncrementalBuildIntegrationTest {
    TestJvmComponent testComponent = new TestScalaComponent()
}
