package argelbargel.gradle.plugins.build.modules.manager.includer

import argelbargel.gradle.plugins.build.modules.common.BuildModuleProperties
import argelbargel.gradle.plugins.build.modules.manager.includer.builds.ModuleBuildIncluder
import argelbargel.gradle.plugins.build.modules.manager.includer.projects.ModuleProjectIncluder
import org.gradle.api.initialization.Settings
import org.slf4j.Logger

abstract class BuildModuleIncluder {
    static BuildModuleIncluder createIncluder(BuildModuleProperties properties) {
        return properties.includeBuild ?
                new ModuleBuildIncluder(properties)
                : new ModuleProjectIncluder(properties)
    }

    private final BuildModuleProperties properties

    protected BuildModuleIncluder(BuildModuleProperties properties) {
        this.properties = properties
    }

    final void include(Settings settings, File moduleDir, Logger logger) {
        logger.info("including module at ${moduleDir} using ${this.class.simpleName}...")
        include(settings, moduleDir, properties, logger)
    }

    protected abstract void include(Settings settings, File projectDir, BuildModuleProperties properties, Logger logger)
}