package argelbargel.gradle.plugins.buildModules.module

import argelbargel.gradle.plugins.buildModules.common.BuildModuleProperties
import argelbargel.gradle.plugins.buildModules.common.BuildModuleSubstitution
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.specs.AndSpec
import org.gradle.api.specs.Spec

import static argelbargel.gradle.plugins.buildModules.common.BuildModuleConstants.*

class BuildModuleExtension {
    private final Project project
    private File propertiesFile
    private BuildModuleProperties propertiesCache
    private final AndSpec<Dependency> substitutionSpec

    BuildModuleExtension(Project project) {
        this.project = project
        setPropertiesFile(project.properties.getOrDefault(PROPERTY_MODULE_PROPERTIES_FILE, DEFAULT_MODULE_PROPERTIES_FILE) as String)
        substitutionSpec = new AndSpec<>()
    }

    final void setPropertiesFile(String fileName) {
        this.propertiesFile = new File(project.projectDir, fileName)
        propertiesCache = null
    }

    String getModuleName() {
        return properties().name
    }

    String getModuleGroup() {
        return properties().group
    }

    File getModuleSettingsFile() {
        return project.file("${project.projectDir}/${MODULE_SETTINGS_FILE}")
    }

    void substitutesWhen(Closure<Boolean> closure) {
        substitutionSpec & closure
    }

    void substitutesWhen(Spec<Dependency> spec) {
        substitutionSpec & spec
    }


    Collection<BuildModuleSubstitutionAction> getSubstitutions() {
        return createSubstitutionActions(project, properties().substitutions, substitutionSpec)
    }

    private static Collection<BuildModuleSubstitutionAction> createSubstitutionActions(Project project, Collection<BuildModuleSubstitution> substitutions, Spec<Dependency> substitutionSpec) {
        return substitutions.collect { new BuildModuleSubstitutionAction(project, it, substitutionSpec) }
    }

    private final BuildModuleProperties properties() {
        if (propertiesCache == null) {
            propertiesCache = new BuildModuleProperties(propertiesFile)
        }

        return propertiesCache
    }
}
