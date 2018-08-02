package argelbargel.gradle.plugins.buildModules.module

import org.gradle.api.Plugin
import org.gradle.api.Project

import static argelbargel.gradle.plugins.buildModules.module.SubstitutionUtility.clearSubstitutions
import static argelbargel.gradle.plugins.buildModules.module.SubstitutionUtility.substitute

class BuildModulePlugin implements Plugin<Project> {
    static final String EXTENSION_NAME_MODULE = "buildModule"
    private static final String PROPERTY_ARCHIVES_BASENAME = 'archivesBaseName'

    @Override
    void apply(Project module) {
        BuildModuleExtension ext = module.extensions.create(EXTENSION_NAME_MODULE, BuildModuleExtension, module)

        module.group = ext.moduleGroup

        module.afterEvaluate {
            updateArchivesBaseName(it, ext.moduleName)
        }

        module.rootProject.allprojects { project ->
            substitute(project, module)
        }

        module.gradle.projectsEvaluated {
            clearSubstitutions()
        }
    }


    private static void updateArchivesBaseName(Project project, String moduleName) {
        if (project.hasProperty(PROPERTY_ARCHIVES_BASENAME) && project.getProperty(PROPERTY_ARCHIVES_BASENAME).equals(project.name)) {
            project.setProperty(PROPERTY_ARCHIVES_BASENAME, moduleName)
        }
    }
}
