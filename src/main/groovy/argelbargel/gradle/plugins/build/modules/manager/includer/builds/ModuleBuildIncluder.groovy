package argelbargel.gradle.plugins.build.modules.manager.includer.builds

import argelbargel.gradle.plugins.build.modules.common.BuildModuleProperties
import argelbargel.gradle.plugins.build.modules.manager.includer.BuildModuleIncluder
import org.gradle.api.initialization.Settings
import org.slf4j.Logger

import static org.apache.commons.lang3.StringUtils.isNotBlank
import static org.gradle.api.artifacts.Dependency.DEFAULT_CONFIGURATION

class ModuleBuildIncluder extends BuildModuleIncluder {
    ModuleBuildIncluder(BuildModuleProperties properties) {
        super(properties)
    }

    @Override
    protected void include(Settings settings, File projectDir, BuildModuleProperties properties, Logger logger) {
        if (settings.gradle.includedBuilds.find { it.projectDir == projectDir } != null) {
            logger.info("ignoring already included project at ${projectDir}")
            return
        }

        settings.includeBuild(projectDir) { build ->
            properties.substitutions.each { module, configuration ->
                if (isNotBlank(configuration) && configuration != DEFAULT_CONFIGURATION) {
                    throw new UnsupportedOperationException("${getClass().simpleName} does not support substitution of project-configurations other than default")
                }
                build.dependencySubstitution { s ->
                    s.substitute s.module(module) with s.project(':')
                }
            }
        }
    }
}
