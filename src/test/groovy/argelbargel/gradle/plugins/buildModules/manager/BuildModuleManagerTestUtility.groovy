package argelbargel.gradle.plugins.buildModules.manager

import static argelbargel.gradle.plugins.TestUtility.createSettings
import static argelbargel.gradle.plugins.buildModules.common.BuildModuleConstants.*
import static argelbargel.gradle.plugins.buildModules.manager.BuildModuleManagerExtension.DEFAULT_MODULES_FILE
import static argelbargel.gradle.plugins.buildModules.manager.BuildModuleManagerPlugin.ACTIVE_MODULES_DIR
import static argelbargel.gradle.plugins.buildModules.manager.BuildModuleManagerPlugin.DEFAULT_MODULES_DIR
import static org.apache.commons.lang3.StringUtils.EMPTY

class BuildModuleManagerTestUtility {
    static void createRootProject(File rootDir, String settings = EMPTY, String build = EMPTY) {
        newFile(rootDir, PROJECT_SETTINGS_FILE).text = createSettings("""
            ${settings}
            apply plugin: 'argelbargel.build.module-manager'
        """)
        newFile(rootDir, 'build.gradle').text = build
    }

    static File createEmptyModuleProject(File rootDir, String name, activate = true) {
        return createModuleProject(rootDir, name, EMPTY, EMPTY, activate)
    }

    static File createModuleProject(File rootDir, String name, String settings = EMPTY, String buildScript = EMPTY, activate = true) {
        File moduleDir = new File(rootDir, "${DEFAULT_MODULES_DIR}/${name}")
        newFile(moduleDir, MODULE_SETTINGS_FILE).text = settings
        newFile(moduleDir, 'build.gradle').text = buildScript
        newFile(moduleDir, DEFAULT_MODULE_PROPERTIES_FILE).text = """
            ${PROPERTY_MODULE_NAME}=${name}
            ${PROPERTY_MODULE_GROUP}=test
        """
        newFile(rootDir, DEFAULT_MODULES_FILE).text += "${name}=#\n"
        if (activate) {
            activateModule(rootDir, name)
        }
        return moduleDir
    }

    static File createSubProject(File parentProjectDir, String name, String buildScript = EMPTY) {
        File subProjectDir = new File(parentProjectDir, name)
        newFile(subProjectDir, 'build.gradle').text = buildScript
        return subProjectDir
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
        return file
    }

    private BuildModuleManagerTestUtility() { /* utility class */ }
}
