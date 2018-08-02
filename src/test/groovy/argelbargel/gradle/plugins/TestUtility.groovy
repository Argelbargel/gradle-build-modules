package argelbargel.gradle.plugins

import org.gradle.internal.classpath.ClassPath
import org.gradle.internal.classpath.DefaultClassPath
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading

import static java.util.Arrays.asList

final class TestUtility {


    static BuildResult buildProject(File projectDir, String... arguments) {
        GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(asList(arguments) + ['--stacktrace'])
                .build()
    }

    static String createSettings(String settings) {
        return """
            buildscript {
                ${pluginUnderTestDependencies()}
            }

            ${settings}
        """
    }

    static String pluginUnderTestDependencies() {
        ClassPath cp = DefaultClassPath.of(PluginUnderTestMetadataReading.readImplementationClasspath())

        return """
                dependencies {
                    classpath files(${cp.asFiles.collect { "'${it.toURI()}'" }})
                }
        """
    }


    private TestUtility() { /* Utility-Klasse */ }
}
