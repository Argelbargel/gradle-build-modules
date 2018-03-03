package argelbargel.gradle.plugins.build.modules.manager.includer.projects

import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static argelbargel.gradle.plugins.TestUtility.buildProject
import static argelbargel.gradle.plugins.build.modules.manager.BuildModuleManagerTestUtility.*

class ModuleProjectIncluderTest extends Specification {
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    def "settings werden auf Modul umgebogen"() {
        given:
        createRootProject(testProjectDir.root)
        File moduleDir = createModuleProject(testProjectDir.root,
                'test',
                '''
            def sub = settings.include('sub')
            settings.project('sub').projectDir = new File("${settings.rootDir}/sub")

            println "settings.rootProject: ${settings.rootProject}"
            println "settings.rootDir: ${settings.rootDir}"
            println "settings.settingsDir: ${settings.settingsDir}"
            println "settings.project('sub'): ${settings.project('sub')}"
            println "settings.project(new File('./sub')): ${settings.project(new File("${settings.rootDir}/sub"))}"
        ''')
        createSubProject(moduleDir, 'sub')
        when:
        BuildResult result = buildProject(testProjectDir.root)
        then:
        result.output.contains("settings.rootProject: :test")
        result.output.contains("settings.rootDir: ${moduleDir}")
        result.output.contains("settings.settingsDir: ${moduleDir}")
        result.output.contains("settings.project('sub'): :test:sub")
        result.output.contains("settings.project(new File('./sub')): :test:sub")
        true
    }

    def "gradle wird auf Modul umgebogen"() {
        createRootProject(testProjectDir.root)
        File module1Dir = createModuleProject(testProjectDir.root,
                'module1',
                '''   
            settings.include('sub')

            gradle.rootProject {
                println "gradle.rootProject: ${it}"
            }
            
            gradle.allprojects {
                println "gradle.allprojects: ${it}"
            }
            
            gradle.beforeProject {
                println "gradle.beforeProject: ${it}"
            }
            
            gradle.afterProject {
                println "gradle.afterProject: ${it}"
            }
            
        ''')
        createModuleProject(testProjectDir.root, 'module2')
        createSubProject(module1Dir, 'sub')
        when:
        BuildResult result = buildProject(testProjectDir.root)
        then:
        result.output.contains("gradle.rootProject: project ':module1'")
        result.output.contains("gradle.allprojects: project ':module1'")
        result.output.contains("gradle.allprojects: project ':module1:sub'")
        result.output.contains("gradle.beforeProject: project ':module1'")
        result.output.contains("gradle.beforeProject: project ':module1:sub'")
        result.output.contains("gradle.afterProject: project ':module1'")
        result.output.contains("gradle.afterProject: project ':module1:sub'")
    }
}
