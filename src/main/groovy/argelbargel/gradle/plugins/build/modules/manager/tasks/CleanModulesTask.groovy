package argelbargel.gradle.plugins.build.modules.manager.tasks

import argelbargel.gradle.plugins.build.modules.manager.BuildModuleManagerExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.util.GFileUtils

import static argelbargel.gradle.plugins.build.modules.common.BuildModuleConstants.MODULE_TASKS_GROUP

class CleanModulesTask extends DefaultTask {
    static final String NAME = "cleanModules"

    CleanModulesTask() {
        group = MODULE_TASKS_GROUP
    }

    @TaskAction
    void start() {
        def ext = project.extensions.getByType(BuildModuleManagerExtension)
        GFileUtils.deleteDirectory(ext.modulesDir)
        GFileUtils.deleteDirectory(ext.activeModulesDir)
    }
}
