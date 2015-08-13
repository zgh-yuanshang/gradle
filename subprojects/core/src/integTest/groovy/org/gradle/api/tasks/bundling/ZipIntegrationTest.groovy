/*
 * Copyright 2013 the original author or authors.
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

package org.gradle.api.tasks.bundling

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.integtests.fixtures.executer.UnexpectedBuildFailure
import org.gradle.test.fixtures.archive.ZipTestFixture

import spock.lang.Unroll

class ZipIntegrationTest extends AbstractIntegrationSpec {

    def ensureDuplicatesIncludedWithoutWarning() {
        given:
        createTestFiles()
        buildFile << '''
            task zip(type: Zip) {
                from 'dir1'
                from 'dir2'
                from 'dir3'
                destinationDir = buildDir
                archiveName = 'test.zip'
            }
            '''
        when:
        run 'zip'

        then:
        def theZip = new ZipTestFixture(file('build/test.zip'))
        theZip.hasDescendants('file1.txt', 'file1.txt', 'file2.txt')
    }

    def ensureDuplicatesCanBeExcluded() {
        given:
        createTestFiles()
        buildFile << '''
            task zip(type: Zip) {
                from 'dir1'
                from 'dir2'
                from 'dir3'
                destinationDir = buildDir
                archiveName = 'test.zip'
                eachFile { it.duplicatesStrategy = 'exclude' }
            }
            '''
        when:
        run 'zip'

        then:
        def theZip = new ZipTestFixture(file('build/test.zip'))
        theZip.hasDescendants('file1.txt', 'file2.txt')
    }

    def renamedFileWillBeTreatedAsDuplicateZip() {
        given:
        createTestFiles()
        buildFile << '''
                task zip(type: Zip) {
                    from 'dir1'
                    from 'dir2'
                    destinationDir = buildDir
                    rename 'file2.txt', 'file1.txt'
                    archiveName = 'test.zip'
                    eachFile { it.duplicatesStrategy = 'exclude' }
                }
                '''
        when:
        run 'zip'

        then:
        def theZip = new ZipTestFixture(file('build/test.zip'))
        theZip.hasDescendants('file1.txt')
        theZip.assertFileContent('file1.txt', "dir1/file1.txt")
    }

    def zip64Support() {
        given:
        createTestFiles()
        buildFile << '''
            task zip(type: Zip) {
                from 'dir1'
                from 'dir2'
                destinationDir = buildDir
                archiveName = 'test.zip'
                zip64 = true
            }
            '''
        when:
        run 'zip'

        then:
        def theZip = new ZipTestFixture(file('build/test.zip'))
        theZip.hasDescendants('file1.txt', 'file2.txt')
    }

    @Unroll
    def "ZIP task use #encoding encoding"() {
        given:
        createTestFilesWithEncoding(filename, encoding)
        def encodingStr = encoding > '' ? "'$encoding'" : null
        buildFile << """
            task zip(type: Zip) {
                from 'dir1'
                from 'dir2'
                from 'dir3'
                destinationDir = buildDir
                archiveName = 'test.zip'
                encoding = $encodingStr
            }
            """
        run 'zip'
        def theZip = new ZipTestFixture(file('build/test.zip'), encoding)

        expect:
        theZip."$method" "${filename}1.txt", "${filename}2.txt", "${filename}3.txt"

        where:
        method                      | encoding        | filename
        'hasDescendants'            | null            | 'file'
        'hasDescendants'            | 'UTF-8'         | '中文'
        'hasDescendants'            | 'ISO-8859-1'    | 'ÈÇ'
        'doesNotContainDescendants' | 'US-ASCII'      | '测试'
    }

    def ensureExceptionWillBeThrownUsingUnsupportedEncoding() {
        given:
        def encoding = 'unsupported encoding'
        createTestFilesWithEncoding('file', encoding)
        buildFile << """
            task zip(type: Zip) {
                from 'dir1'
                destinationDir = buildDir
                archiveName = 'test.zip'
                encoding = '$encoding'
            }
            """

        when:
        run 'zip'

        then:
        UnexpectedBuildFailure ex = thrown()
        ex.cause.cause.cause.message == encoding
    }

    private def createTestFiles() {
        createDir('dir1', {
            file('file1.txt').text = "dir1/file1.txt"
        })
        createDir('dir2', {
            file('file2.txt').text = "dir2/file2.txt"
        })
        createDir('dir3', {
            file('file1.txt').text = "dir3/file1.txt"
        })
    }

    private def createTestFilesWithEncoding(String filename, String encoding) {
        try {
            filename = new String(filename.getBytes(encoding), encoding)
        } catch (ex) { }

        (1..3).each { idx ->
            createDir("dir$idx", {
                file("${filename}${idx}.txt").text = "dir$idx/${filename}${idx}.txt"
            })
        }
    }

}
