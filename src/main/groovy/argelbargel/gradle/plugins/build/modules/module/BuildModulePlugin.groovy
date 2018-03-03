package argelbargel.gradle.plugins.build.modules.module

import argelbargel.gradle.plugins.build.modules.module.tasks.MakeModuleTask
import argelbargel.gradle.plugins.build.modules.module.tasks.MakeRootProjectTask
import org.gradle.api.Plugin
import org.gradle.api.Project

import static argelbargel.gradle.plugins.build.modules.module.SubstitutionUtility.substitute

class BuildModulePlugin implements Plugin<Project> {
    static final String EXTENSION_NAME_MODULE = "buildModule"
    static final String TASKS_GROUP = "build modules"
    private static final String PROPERTY_ARCHIVES_BASENAME = 'archivesBaseName'

    @Override
    void apply(Project module) {
        BuildModuleExtension ext = module.extensions.create(EXTENSION_NAME_MODULE, BuildModuleExtension, module)

        if (ext.moduleSettingsFile.exists()) {
            module.tasks.create("makeRootProject", MakeRootProjectTask)
        } else {
            module.tasks.create("makeModule", MakeModuleTask)
        }
        
        module.group = ext.moduleGroup

        module.afterEvaluate {
            updateArchivesBaseName(it, ext.moduleName)
        }

        module.rootProject.allprojects { project ->
            substitute(project, module)
        }
    }


    private static void updateArchivesBaseName(Project project, String moduleName) {
        if (project.hasProperty(PROPERTY_ARCHIVES_BASENAME) && project.getProperty(PROPERTY_ARCHIVES_BASENAME).equals(project.name)) {
            project.setProperty(PROPERTY_ARCHIVES_BASENAME, moduleName)
        }
    }
}
