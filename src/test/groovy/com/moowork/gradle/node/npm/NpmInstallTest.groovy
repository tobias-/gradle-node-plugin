package com.moowork.gradle.node.npm


import com.moowork.gradle.node.task.AbstractTaskTest

class NpmInstallTest
    extends AbstractTaskTest
{
    def "too low version 5.x"()
    {
        given:
        def version = "5.6.9"

        when:
        version

        then:
        !NpmInstallTask.isVersionWithCiCommand( version )
    }

    def "good enough version 5.x"()
    {
        given:
        def version = "5.7.0"

        when:
        version

        then:
        NpmInstallTask.isVersionWithCiCommand( version )
    }

    def "too low major version"()
    {
        given:
        def version = "5.6.0"

        when:
        version

        then:
        !NpmInstallTask.isVersionWithCiCommand( version )
    }

    def "high enough major version"()
    {
        given:
        def version = "6.0.0"

        when:
        version

        then:
        NpmInstallTask.isVersionWithCiCommand( version )
    }

    def "high enough major dev version"()
    {
        given:
        def version = "6.0.0-dev"

        when:
        version

        then:
        NpmInstallTask.isVersionWithCiCommand( version )
    }
}
