package argelbargel.gradle.plugins.buildModules.manager

import org.gradle.api.logging.Logger


abstract class BuildModuleRepository {
    private final String definition

    protected BuildModuleRepository(String definition) {
        this.definition = definition
    }

    final void update(String name, File moduleDir, Logger logger) throws IOException {
        if (!moduleDir.exists()) {
            logger.warn("module $name does not exist, initializing...")
            initialize(definition, moduleDir.absolutePath, logger)
        }

        update(definition, moduleDir.absolutePath, logger)
    }

    protected abstract void initialize(String definition, String targetPath, Logger logger) throws IOException
    protected abstract void update(String definition, String targetPath, Logger logger) throws IOException
}