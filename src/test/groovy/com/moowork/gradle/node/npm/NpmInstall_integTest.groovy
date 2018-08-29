package com.moowork.gradle.node.npm

import com.moowork.gradle.AbstractIntegTest
import groovy.json.JsonSlurper
import org.gradle.testkit.runner.TaskOutcome

class NpmInstall_integTest
    extends AbstractIntegTest
{
    def 'install packages with npm'()
    {
        given:
        writeBuild( '''
            plugins {
                id 'com.moowork.node'
            }

            node {
                version = "0.10.33"
                npmVersion = "2.1.6"
                download = true
                workDir = file('build/node')
            }
        ''' )
        writeEmptyPackageJson()

        when:
        def result = buildTask( 'npmInstall' )

        then:
        result.outcome == TaskOutcome.SUCCESS

        when:
        result = buildTask( 'npmInstall' )

        then:
        result.outcome == TaskOutcome.UP_TO_DATE
    }

    def 'install packages with npm ci'()
    {
        given:
        writeBuild( '''
            plugins {
                id 'com.moowork.node'
            }

            node {
                version = "10.7.0"
                npmVersion = "6.4.0"
                download = true
                workDir = file('build/node')
                npmVersionsFromPackageLock = true
            }
        ''' )
        writePackageJson( """ {
            "name": "example",
            "dependencies": {"left-pad": "^1.1.0"}
        }
        """ )
        writePackageLock( """ {
          "name": "example",
          "requires": true,
          "lockfileVersion": 1,
          "dependencies": {
            "left-pad": {
              "version": "1.1.0",
              "resolved": "https://registry.npmjs.org/left-pad/-/left-pad-1.1.0.tgz",
              "integrity": "sha1-R6La9YHt5FQzTe5sYDbK4A2RLk0="
            }
          }
        }
        """ )
        when:
        def result = buildTask( 'npmInstall' )
        def leftPadPackage = createFile( "node_modules/left-pad/package.json" )

        then:
        result.outcome == TaskOutcome.SUCCESS
        new JsonSlurper().parseText( leftPadPackage.text ).version == "1.1.0"

        when:
        result = buildTask( 'npmInstall' )

        then:
        result.outcome == TaskOutcome.UP_TO_DATE
    }

    def 'install packages with npm install'()
    {
        given:
        writeBuild( '''
            plugins {
                id 'com.moowork.node'
            }

            node {
                version = "8.9.3"
                npmVersion = "5.5.1"
                download = true
                workDir = file('build/node')
                npmVersionsFromPackageLock = false
            }
        ''' )
        writePackageJson( """ {
            "name": "example",
            "dependencies": {"left-pad": "^1.1.0"}
        }
        """ )
        writePackageLock( """ {
          "name": "example",
          "requires": true,
          "lockfileVersion": 1,
          "dependencies": {
            "left-pad": {
              "version": "1.1.0",
              "resolved": "https://registry.npmjs.org/left-pad/-/left-pad-1.1.0.tgz",
              "integrity": "sha1-R6La9YHt5FQzTe5sYDbK4A2RLk0="
            }
          }
        }
        """ )
        when:
        def result = buildTask( 'npmInstall' )
        def leftPadPackage = createFile( "node_modules/left-pad/package.json" )

        then:
        result.outcome == TaskOutcome.SUCCESS
        new JsonSlurper().parseText( leftPadPackage.text ).version == "1.1.3"

        when:
        result = buildTask( 'npmInstall' )

        then:
        result.outcome == TaskOutcome.UP_TO_DATE
    }

    def 'install packages with npm and postinstall task requiring npm and node'()
    {
        given:
        writeBuild( '''
            plugins {
                id 'com.moowork.node'
            }
            node {
                version = "0.10.33"
                npmVersion = "2.1.6"
                download = true
                workDir = file('build/node')
            }
        ''' )
        writePackageJson(""" {
            "name": "example",
            "dependencies": {},
            "versionOutput" : "node --version",
            "postinstall" : "npm run versionOutput"
        }
        """)

        when:
        def result = buildTask( 'npmInstall' )

        then:
        result.outcome == TaskOutcome.SUCCESS

        when:
        result = buildTask( 'npmInstall' )

        then:
        result.outcome == TaskOutcome.UP_TO_DATE
    }

    def 'install packages with npm in different directory'()
    {
        given:
        writeBuild( '''
            plugins {
                id 'com.moowork.node'
            }

            node {
                version = "0.10.33"
                npmVersion = "2.1.6"
                download = true
                workDir = file('build/node')
                nodeModulesDir = file('subdirectory')
            }
        ''' )
        writeFile( 'subdirectory/package.json', """{
            "name": "example",
            "dependencies": {
            }
        }""" )

        when:
        def result = build( 'npmInstall' )

        then:
        result.task( ':npmInstall' ).outcome == TaskOutcome.SUCCESS
    }
}
