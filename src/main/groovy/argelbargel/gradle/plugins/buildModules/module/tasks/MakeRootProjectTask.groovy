package argelbargel.gradle.plugins.buildModules.module.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static argelbargel.gradle.plugins.buildModules.common.BuildModuleConstants.*

class MakeRootProjectTask extends DefaultTask {
    MakeRootProjectTask() {
        group = MODULE_TASKS_GROUP
    }

    @TaskAction
    void start() {
        def projectSettings = project.file("${project.projectDir}/${PROJECT_SETTINGS_FILE}")
        if (!projectSettings.exists()) {
            File moduleSettings = project.file("${project.projectDir}/${MODULE_SETTINGS_FILE}")
            if (moduleSettings.exists()) {
                moduleSettings.renameTo(projectSettings)
            } else {
                projectSettings.text << "created by ${this}"
            }
        }
    }
}
