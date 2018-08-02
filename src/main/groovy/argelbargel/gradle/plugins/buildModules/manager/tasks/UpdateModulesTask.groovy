package argelbargel.gradle.plugins.buildModules.manager.tasks

import argelbargel.gradle.plugins.buildModules.manager.BuildModuleManagerExtension
import groovy.swing.SwingBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

import java.awt.*
import java.util.List

import static argelbargel.gradle.plugins.buildModules.common.BuildModuleConstants.MODULE_TASKS_GROUP
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
        } else {
            showDialog(extension, project.logger)
        }
    }

    void parseProperty(String property, BuildModuleManagerExtension extension) {
        def newModules = property.split(/[\s,]+/) as List
        def defaultMode = checkMode(newModules, ALL_MODULES, null)
        extension.allModules.each {
            def active = checkMode(newModules, it.name, defaultMode)
            if (active != null) {
                it.setActive(active, project.logger)
            }

            if (it.isActive()) {
                it.setActive(it.update(project.logger), project.logger)
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

    private static void showDialog(BuildModuleManagerExtension extension, Logger logger) {
        def dialog = new SwingBuilder().dialog(modal: true,
                title: 'Module auswählen',
                alwaysOnTop: true,
                resizable: false,
                locationRelativeTo: null,
                pack: false,
                show: false,
        ) {

            def moduleCheckBoxes = []
            def modules = [:]
            borderLayout()
            hbox(constraints: BorderLayout.NORTH) {
                button(defaultButton: false, text: 'Alle', actionPerformed: {
                    moduleCheckBoxes.each { it.selected = true }
                })

                button(defaultButton: false, text: 'Keines', actionPerformed: {
                    moduleCheckBoxes.each { it.selected = false }
                })
            }

            vbox {
                extension.allModules.each { module ->
                    moduleCheckBoxes += checkBox(module.name,
                            selected: module.active,
                            stateChanged: { modules[module] = it.source.selected }
                    )
                }
            }

            hbox(constraints: BorderLayout.SOUTH) {
                button(defaultButton: true, text: 'OK', actionPerformed: {
                    dispose()
                    modules.each { module, active -> module.setActive(active, logger) }
                    extension.allModules.each { it.update(logger) }
                })
            }
        }


        dialog.pack()
        dialog.setLocationRelativeTo(null)
        dialog.setVisible(true)
    }
}
