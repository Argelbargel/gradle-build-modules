package argelbargel.gradle.plugins.build.modules.manager.tasks

import argelbargel.gradle.plugins.build.modules.manager.BuildModuleManagerExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

import static argelbargel.gradle.plugins.build.modules.common.BuildModuleConstants.MODULE_TASKS_GROUP
import static org.apache.commons.lang3.StringUtils.isNotBlank

class UpdateModulesTask extends DefaultTask {
    static final String NAME = 'updateModules'

    private static final String PREFIX_ACTIVATE = '+'
    private static final String PREFIX_DEACTIVATE = '-'
    private static final String ALL_MODULES = 'all'

    @Option(option = "modules",
            description = "comma-separated list of modules to (de-)activate, e.g. +module1,-module2")
    private String modules


    UpdateModulesTask() {
        group = MODULE_TASKS_GROUP
    }

    void setModules(String modules) {
        this.modules = modules
    }

    @TaskAction
    void start() {
        def extension = project.extensions.getByType(BuildModuleManagerExtension)
        if (isNotBlank(modules)) {
            parseProperty(modules, extension)
        }
    }

    void parseProperty(String property, BuildModuleManagerExtension extension) {
        def newModules = property.split(/[\\s,]+/) as List
        def defaultMode = checkMode(newModules, ALL_MODULES, null)
        extension.allModules.each {
            def active = checkMode(newModules, it.name, defaultMode)
            if (active != null) {
                it.setActive(active, project.logger)
            }
        }
    }

    static Boolean checkMode(List<String> newModules, String name, Boolean defaultValue) {
        if (newModules.contains(PREFIX_DEACTIVATE + name)) {
            return Boolean.FALSE
        } else if (newModules.contains(PREFIX_ACTIVATE + name)) {
            return Boolean.TRUE
        }

        return defaultValue
    }
}
