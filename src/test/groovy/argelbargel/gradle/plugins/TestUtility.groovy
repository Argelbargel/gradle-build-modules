package argelbargel.gradle.plugins

import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.internal.classpath.ClassPath
import org.gradle.internal.classpath.DefaultClassPath
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading

import java.util.concurrent.atomic.AtomicBoolean

import static java.util.Arrays.asList

final class TestUtility {

    static void evaluate(Project project) {
        AtomicBoolean done = new AtomicBoolean(false)
        project.afterEvaluate {
            done.set(true)
            synchronized (done) {
                done.notify()
            }
        }

        (project as ProjectInternal).evaluate()

        synchronized (done) {
            while (!done.get()) {
                wait(10)
            }
        }
    }

    static void resolve(Project project) {
        evaluate(project)
        (project as ProjectInternal).configurations.findAll { it.canBeResolved }.each { it.resolve() }
    }

    static BuildResult buildScript(File projectDir, String buildScript, String... arguments) {
        new File(projectDir, "build.gradle").text = buildScript
        buildProject(projectDir, arguments)
    }

    static BuildResult buildProject(File projectDir, String... arguments) {
        GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(asList(arguments) + ['--stacktrace'])
                .build()
    }

    static String createSettings(String settings) {
        ClassPath cp = DefaultClassPath.of(PluginUnderTestMetadataReading.readImplementationClasspath())

        return """
            buildscript {
                dependencies {
                    classpath files(${cp.asFiles.collect { "'${it.toURI()}'" }})
                }
            }

            ${settings}
        """
    }


    private TestUtility() { /* Utility-Klasse */ }
}
