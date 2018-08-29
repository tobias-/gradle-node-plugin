package com.moowork.gradle.node.npm

/**
 * npm install that only gets executed if gradle decides so.*/
class NpmInstallTask
    extends NpmTask
{
    public final static String NAME = 'npmInstall'

    NpmInstallTask()
    {
        this.group = 'Node'
        this.description = 'Install node packages from package.json.'
        dependsOn( [NpmSetupTask.NAME] )

        this.project.afterEvaluate {
            getInputs().file( new File( (File) this.project.node.nodeModulesDir, 'package.json' ) )
            getOutputs().dir( new File( (File) this.project.node.nodeModulesDir, 'node_modules' ) )
        }
    }

    static boolean isVersionWithCiCommand( String npmVersion )
    {
        def versionParts = npmVersion.split( "\\.", 3 ).collect {
            try
            {
                it.toInteger()
            }
            catch ( Exception ignored )
            {
                0
            }
        }
        return versionParts.size() >= 2 &&
                (
                        versionParts[0] > 5 ||
                                (versionParts[0] == 5 && versionParts[1] >= 7)
                )
    }

    void configureInstallCommand( boolean npmVersionsFromPackageLock, String npmVersion )
    {

        def packageLockFile = new File( (File) this.project.node.nodeModulesDir, 'package-lock.json' )
        if ( npmVersionsFromPackageLock && packageLockFile.exists() && isVersionWithCiCommand( npmVersion ) )
        {
            setNpmCommand( 'ci' )

            this.project.afterEvaluate {

                getInputs().file( packageLockFile )
            }
        }
        else
        {
            setNpmCommand( 'install' )
        }
    }
}
