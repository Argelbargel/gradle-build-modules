package argelbargel.gradle.plugins.build.modules.module.tasks

import argelbargel.gradle.plugins.build.modules.module.BuildModuleExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static argelbargel.gradle.plugins.build.modules.common.BuildModuleConstants.MODULE_TASKS_GROUP
import static argelbargel.gradle.plugins.build.modules.common.BuildModuleConstants.PROJECT_SETTINGS_FILE

class MakeModuleTask extends DefaultTask {
    MakeModuleTask() {
        group = MODULE_TASKS_GROUP
    }

    @TaskAction
    void start() {
        def projectSettings = project.file("${project.projectDir}/${PROJECT_SETTINGS_FILE}")
        if (projectSettings.exists()) {
            projectSettings.renameTo(project.extensions.getByType(BuildModuleExtension).moduleSettingsFile)
        }
    }
}
