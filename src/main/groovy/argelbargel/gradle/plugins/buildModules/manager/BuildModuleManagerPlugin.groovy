package argelbargel.gradle.plugins.buildModules.manager

import argelbargel.gradle.plugins.buildModules.common.BuildModuleProperties
import argelbargel.gradle.plugins.buildModules.manager.tasks.CleanModulesTask
import argelbargel.gradle.plugins.buildModules.manager.tasks.ListModulesTask
import argelbargel.gradle.plugins.buildModules.manager.tasks.UpdateModulesTask
import org.apache.commons.io.FileUtils
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static argelbargel.gradle.plugins.buildModules.common.BuildModuleConstants.*
import static argelbargel.gradle.plugins.buildModules.manager.includer.BuildModuleIncluder.createIncluder
import static org.apache.commons.io.filefilter.FalseFileFilter.FALSE
import static org.apache.commons.io.filefilter.TrueFileFilter.TRUE

class BuildModuleManagerPlugin implements Plugin<Settings> {
    static final String PROPERTY_MODULES_DIR = "buildModules.dir"
    static final String DEFAULT_MODULES_DIR = "build-modules"
    static final String ACTIVE_MODULES_DIR = ".gradle/buildModules/"
    private static final String EXTENSION_NAME = "buildModules"
    private static final Logger LOGGER = LoggerFactory.getLogger(EXTENSION_NAME)


    @Override
    void apply(Settings settings) {
        settings.gradle.rootProject(new ProjectSetup())
        settings.gradle.rootProject(new TasksSetup())
        String modulePropertiesFile = settings.properties.getOrDefault(PROPERTY_MODULE_PROPERTIES_FILE, DEFAULT_MODULE_PROPERTIES_FILE)
        includeModules(new File(settings.rootDir, ACTIVE_MODULES_DIR), settings, new File(settings.rootDir, getModulesDir(settings)), modulePropertiesFile)
    }

    private static void includeModules(File activeModulesDir, Settings settings, File modulesDir, String modulePropertiesFile) {
        if (activeModulesDir.exists()) {
            def included = []

            settings.gradle.beforeProject {
                if (included.contains(it.projectDir)) {
                    it.ext.set(PROPERTY_MODULE_INCLUDED, true)
                }
            }

            FileUtils.listFilesAndDirs(activeModulesDir, FALSE, TRUE).each {
                if (it != activeModulesDir) {
                    try {
                        LOGGER.info("Including module ${it.name}...")
                        def moduleDir = new File(modulesDir, it.name)
                        includeModule(settings, moduleDir, modulePropertiesFile)
                        included.add(moduleDir)
                    } catch (Exception e) {
                        LOGGER.error("Could not include module ${it.name}: ${e.message}", e)
                    }
                }
            }
        }
    }

    private static void includeModule(Settings settings, File moduleDir, String modulePropertiesFile) {
        BuildModuleProperties properties = new BuildModuleProperties(new File(moduleDir, modulePropertiesFile))
        createIncluder(properties).include(settings, moduleDir, LOGGER)
    }

    private static String getModulesDir(Object source) {
        return source.hasProperty(PROPERTY_MODULES_DIR) ? source.getProperty(PROPERTY_MODULES_DIR) : DEFAULT_MODULES_DIR
    }


    private static class ProjectSetup implements Action<Project> {
        @Override
        void execute(Project project) {
            if (project.rootProject == project) {
                project.extensions.create(
                        EXTENSION_NAME,
                        BuildModuleManagerExtension,
                        project,
                        new File(project.rootDir, getModulesDir(project)),
                        new File(project.rootDir, ACTIVE_MODULES_DIR))

                project.tasks.create(UpdateModulesTask.NAME, UpdateModulesTask)
                project.tasks.create(ListModulesTask.NAME, ListModulesTask)
                project.tasks.create(CleanModulesTask.NAME, CleanModulesTask)
            }
        }
    }


    private static class TasksSetup implements Action<Project> {
        @Override
        void execute(Project project) {
            project.extensions.getByType(BuildModuleManagerExtension).delegatedTasks.each {
                project.tasks.create(it.name, DefaultTask, it)
            }
        }
    }
}
