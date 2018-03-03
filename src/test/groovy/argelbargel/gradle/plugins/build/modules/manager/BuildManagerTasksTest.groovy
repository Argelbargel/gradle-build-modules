package argelbargel.gradle.plugins.build.modules.manager

import org.gradle.testkit.runner.BuildResult
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static argelbargel.gradle.plugins.TestUtility.buildProject
import static argelbargel.gradle.plugins.build.modules.manager.BuildModuleManagerTestUtility.createEmptyModuleProject
import static argelbargel.gradle.plugins.build.modules.manager.BuildModuleManagerTestUtility.createRootProject
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class BuildManagerTasksTest extends Specification {
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    @Before
    void createMultiModuleProject() {
        createRootProject(testProjectDir.root)
        createEmptyModuleProject(testProjectDir.root, 'module1', false)
        createEmptyModuleProject(testProjectDir.root, 'module2', true)
        createEmptyModuleProject(testProjectDir.root, 'module3', false)
        createEmptyModuleProject(testProjectDir.root, 'module4', true)
    }


    def "listModules listet alle Module auf, aktivierte Module zuerst"() {
        when:
        BuildResult result = buildProject(testProjectDir.root, 'listModules', '-q')

        then:
        result.tasks(SUCCESS).collect { it.path } == [':listModules']
        result.output.normalize() == '''==============================
 Available Modules
==============================
 - module2 (active)
 - module4 (active)
 - module1
 - module3
'''
    }

    def "updateModules (de-)aktiviert Module"() {
        when:
        BuildResult result = buildProject(testProjectDir.root, 'updateModules', '--modules=+module1,-module2', 'listModules', '-q')

        then:
        result.tasks(SUCCESS).collect { it.path } == [':updateModules', ':listModules']
        result.output.normalize() == '''==============================
 Available Modules
==============================
 - module1 (active)
 - module4 (active)
 - module2
 - module3
'''
    }

    def "updateModules ignoriert Module ohne Modifier"() {
        when:
        BuildResult result = buildProject(testProjectDir.root, 'updateModules', '--modules=module1,-module2', 'listModules', '-q')

        then:
        result.tasks(SUCCESS).collect { it.path } == [':updateModules', ':listModules']
        result.output.normalize() == '''==============================
 Available Modules
==============================
 - module4 (active)
 - module1
 - module2
 - module3
'''
    }

    def "updateModules +all aktiviert alle Module"() {
        when:
        BuildResult result = buildProject(testProjectDir.root, 'updateModules', '--modules=+all', 'listModules', '-q')

        then:
        result.tasks(SUCCESS).collect { it.path } == [':updateModules', ':listModules']
        result.output.normalize() == '''==============================
 Available Modules
==============================
 - module1 (active)
 - module2 (active)
 - module3 (active)
 - module4 (active)
'''
    }

    def "updateModules -all deaktiviert alle Module"() {
        when:
        BuildResult result = buildProject(testProjectDir.root, 'updateModules', '--modules=-all', 'listModules', '-q')

        then:
        result.tasks(SUCCESS).collect { it.path } == [':updateModules', ':listModules']
        result.output.normalize() == '''==============================
 Available Modules
==============================
 - module1
 - module2
 - module3
 - module4
'''
    }

    def "updateModules +all lässt sich mit einzelnen Modulen kombinieren"() {
        when:
        BuildResult result = buildProject(testProjectDir.root, 'updateModules', '--modules=+all,-module2', 'listModules', '-q')

        then:
        result.tasks(SUCCESS).collect { it.path } == [':updateModules', ':listModules']
        result.output.normalize() == '''==============================
 Available Modules
==============================
 - module1 (active)
 - module3 (active)
 - module4 (active)
 - module2
'''
    }

    def "updateModules -all lässt sich mit einzelnen Modulen kombinieren"() {
        when:
        BuildResult result = buildProject(testProjectDir.root, 'updateModules', '--modules=-all,+module3', 'listModules', '-q')

        then:
        result.tasks(SUCCESS).collect { it.path } == [':updateModules', ':listModules']
        result.output.normalize() == '''==============================
 Available Modules
==============================
 - module3 (active)
 - module1
 - module2
 - module4
'''
    }

    def "cleanModules entfernt die Module-Ordner, belässt aber die build-module.properties"() {
        given:
        new File(testProjectDir.root, BuildModuleManagerPlugin.ACTIVE_MODULES_DIR).isDirectory()
        new File(testProjectDir.root, BuildModuleManagerPlugin.DEFAULT_MODULES_DIR).isDirectory()

        when:
        BuildResult result = buildProject(testProjectDir.root, 'cleanModules', 'listModules', '-q')

        then:
        !new File(testProjectDir.root, BuildModuleManagerPlugin.ACTIVE_MODULES_DIR).isDirectory()
        !new File(testProjectDir.root, BuildModuleManagerPlugin.DEFAULT_MODULES_DIR).isDirectory()
        result.tasks(SUCCESS).collect { it.path } == [':cleanModules', ':listModules']
        result.output.normalize() == '''==============================
 Available Modules
==============================
 - module1
 - module2
 - module3
 - module4
'''
    }

}
