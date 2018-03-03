package argelbargel.gradle.plugins.build.modules.manager.tasks

import argelbargel.gradle.plugins.build.modules.manager.BuildModuleManagerExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static argelbargel.gradle.plugins.build.modules.common.BuildModuleConstants.MODULE_TASKS_GROUP

class ListModulesTask extends DefaultTask {
    static final String NAME = "listModules"

    ListModulesTask() {
        group = MODULE_TASKS_GROUP
    }

    @TaskAction
    void start() {
        println "=============================="
        println " Available Modules"
        println "=============================="
        project.extensions.getByType(BuildModuleManagerExtension).allModules.each {
            println " - $it"
        }
    }
}
