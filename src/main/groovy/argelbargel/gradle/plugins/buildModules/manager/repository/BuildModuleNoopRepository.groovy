package argelbargel.gradle.plugins.buildModules.manager.repository

import argelbargel.gradle.plugins.buildModules.manager.BuildModuleRepository
import org.gradle.api.logging.Logger

final class BuildModuleNoopRepository extends BuildModuleRepository {
    BuildModuleNoopRepository(String definition) {
        super(definition)
    }

    @Override
    protected void initialize(String definition, String targetPath, Logger logger) throws IOException { /* NOOP */ }

    @Override
    protected void update(String definition, String targetPath, Logger logger) throws IOException { /* NOOP */ }
}
