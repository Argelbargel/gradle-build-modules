package argelbargel.gradle.plugins.buildModules.manager

import org.gradle.testkit.runner.BuildResult
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static argelbargel.gradle.plugins.TestUtility.buildProject
import static argelbargel.gradle.plugins.buildModules.manager.BuildModuleManagerTestUtility.*
import static org.apache.commons.lang3.StringUtils.EMPTY

class BuildManagerPluginTest extends Specification {
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()
    private File moduleDir

    @Before
    void createMultiModuleProject() {
        createRootProject(testProjectDir.root)
        moduleDir = createModuleProject(testProjectDir.root, 'module', EMPTY, MODULE_PROJECT_BUILD_SCRIPT + """
            println buildModule.included
        """)
    }

    def "für eingebundene Module ist buildModule.included true"() {
        when:
        BuildResult result = buildProject(testProjectDir.root, 'build', '-q')

        then:
        result.output.normalize() == 'true\n'
    }

    def "für standalone ausgeführten Module ist buildModule.included false"() {
        when:
        BuildResult result = buildProject(moduleDir, 'build', '-u', '-q')

        then:
        result.output.normalize() == 'false\n'
    }
}
