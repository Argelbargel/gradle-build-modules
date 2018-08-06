package argelbargel.gradle.plugins.buildModules.manager

import static argelbargel.gradle.plugins.TestUtility.createSettings
import static argelbargel.gradle.plugins.TestUtility.pluginUnderTestDependencies
import static argelbargel.gradle.plugins.buildModules.common.BuildModuleConstants.*
import static argelbargel.gradle.plugins.buildModules.manager.BuildModuleManagerExtension.DEFAULT_MODULES_FILE
import static argelbargel.gradle.plugins.buildModules.manager.BuildModuleManagerPlugin.ACTIVE_MODULES_DIR
import static argelbargel.gradle.plugins.buildModules.manager.BuildModuleManagerPlugin.DEFAULT_MODULES_DIR
import static org.apache.commons.lang3.StringUtils.EMPTY

class BuildModuleManagerTestUtility {
    static final String ROOT_PROJECT_BUILD_SCRIPT = """
            buildscript {
                ${pluginUnderTestDependencies()}
            }

            import argelbargel.gradle.plugins.buildModules.manager.BuildModuleRepository

            class DummyRepository extends BuildModuleRepository {
                DummyRepository(String definition) {
                    super(definition)        
                }
            
                protected void initialize(String definition, String targetPath, Logger logger) {
                    def targetDir = new File(targetPath)
                    if (!targetDir.exists()) {
                        targetDir.mkdirs()
                        new File(targetPath, "${DEFAULT_MODULE_PROPERTIES_FILE}").text = \"\"\"
                            ${PROPERTY_MODULE_NAME}=\${definition}
                            ${PROPERTY_MODULE_GROUP}=test
                        \"\"\"
                        new File(targetPath, "${MODULE_SETTINGS_FILE}").createNewFile()
                    }                    
                }
                
                protected void update(String definition, String targetPath, Logger logger) {
                    new File(targetPath, ".updated").text = definition
                }
            }

            buildModules {
                repository = DummyRepository
            }
        """

    static final String MODULE_PROJECT_BUILD_SCRIPT = """
            buildscript {
                ${pluginUnderTestDependencies()}
            }

            apply plugin: 'base'
            apply plugin: 'argelbargel.build-modules.module'
        """

    static void createRootProject(File rootDir, String settings = EMPTY, String build = ROOT_PROJECT_BUILD_SCRIPT) {
        newFile(rootDir, 'build.gradle').text = build
        newFile(rootDir, PROJECT_SETTINGS_FILE).text = createSettings("""
            ${settings}
            apply plugin: 'argelbargel.build-modules.manager'
        """)
    }

    static File createEmptyModuleProject(File rootDir, String name, boolean activate = true) {
        return createModuleProject(rootDir.canonicalFile, name, EMPTY, EMPTY, activate)
    }

    static File createModuleProject(File rootDir, String name, String settings = EMPTY, String buildScript = MODULE_PROJECT_BUILD_SCRIPT, boolean activate = true) {
        File moduleDir = new File(rootDir, "${DEFAULT_MODULES_DIR}/${name}")
        newFile(moduleDir, MODULE_SETTINGS_FILE).text = settings
        newFile(moduleDir, 'build.gradle').text = buildScript
        newFile(moduleDir, DEFAULT_MODULE_PROPERTIES_FILE).text = """
            ${PROPERTY_MODULE_NAME}=${name}
            ${PROPERTY_MODULE_GROUP}=test
        """
        addModule(rootDir.canonicalFile, name, activate)
        return moduleDir.canonicalFile
    }

    static void addModule(File rootDir, String name, activate = false) {
        newFile(rootDir, DEFAULT_MODULES_FILE).text += "${name}=${name}\n"
        if (activate) {
            activateModule(rootDir, name)
        }
    }

    static File createSubProject(File parentProjectDir, String name, String buildScript = EMPTY) {
        File subProjectDir = new File(parentProjectDir, name)
        newFile(subProjectDir, 'build.gradle').text = buildScript
        return subProjectDir.canonicalFile
    }

    static void activateModule(File rootDir, String name) {
        new File(rootDir, "${ACTIVE_MODULES_DIR}/${name}").mkdirs()
    }

    private static File newFile(File root, String path) {
        File file = new File(root, path)
        file.parentFile.mkdirs()
        if (!file.exists()) {
            file.createNewFile()
        }
        return file.canonicalFile
    }

    private BuildModuleManagerTestUtility() { /* utility class */ }
}
