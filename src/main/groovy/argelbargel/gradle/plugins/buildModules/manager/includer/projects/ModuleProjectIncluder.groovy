package argelbargel.gradle.plugins.buildModules.manager.includer.projects

import argelbargel.gradle.plugins.buildModules.common.BuildModuleProperties
import argelbargel.gradle.plugins.buildModules.manager.includer.BuildModuleIncluder
import org.gradle.api.initialization.Settings
import org.slf4j.Logger

import static argelbargel.gradle.plugins.buildModules.common.BuildModuleConstants.MODULE_SETTINGS_FILE
import static argelbargel.gradle.plugins.buildModules.common.BuildModuleConstants.PROJECT_SETTINGS_FILE

class ModuleProjectIncluder extends BuildModuleIncluder {
    ModuleProjectIncluder(BuildModuleProperties properties) {
        super(properties)
    }

    @Override
    protected void include(Settings settings, File projectDir, BuildModuleProperties properties, Logger logger) {
        if (new File(projectDir, PROJECT_SETTINGS_FILE).exists()) {
            logger.warn("WARNING: the project at ${projectDir} contains a file named ${PROJECT_SETTINGS_FILE} and will thus not be included as a build-module!")
        } else {
            settings.include(properties.name)
            def moduleProject = settings.project(":${properties.name}")
            moduleProject.projectDir = projectDir

            def moduleSettingsScript = new File(projectDir, MODULE_SETTINGS_FILE)
            if (moduleSettingsScript.exists()) {
                new SettingsDecorator(moduleProject, settings, moduleSettingsScript).apply from: moduleSettingsScript
            }

            // damit die Abhängigkeitsersetzungen im BuildModulePlugin funktionieren, müssen wir hier festlegen,
            // dass das Haupt-Projekt von seinen Modulen abhängt
            settings.gradle.projectsLoaded {
                // die IDEA triggert diesen Callback auch für buildSrc, daher müssen wir prüfen, ob das Projekt existiert
                if (it.rootProject.findProject(moduleProject.path) != null) {
                    it.rootProject.evaluationDependsOn(moduleProject.path)
                }
            }
        }
    }
}
