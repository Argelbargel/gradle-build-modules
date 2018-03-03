package argelbargel.gradle.plugins.build.modules.common

final class BuildModuleConstants {
    static final String PROPERTY_MODULE_PROPERTIES_FILE = 'buildModule.propertiesFile'
    static final String PROPERTY_MODULE_NAME = 'buildModule.name'
    static final String PROPERTY_MODULE_GROUP = 'buildModule.group'
    static final String PROPERTY_MODULE_CONFIGURATION = 'buildModule.configuration'
    static final String PROPERTY_MODULE_INCLUDEBUILD = 'buildModule.includeBuild'

    static final String DEFAULT_MODULE_PROPERTIES_FILE = 'gradle.properties'
    static final String MODULE_SETTINGS_FILE = "build-module.gradle"
    static final String PROJECT_SETTINGS_FILE = "settings.gradle"

    static final String MODULE_TASKS_GROUP = "build modules"

    private BuildModuleConstants() { /* contains only constants */ }
}
