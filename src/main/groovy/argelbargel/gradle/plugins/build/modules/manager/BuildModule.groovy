package argelbargel.gradle.plugins.build.modules.manager

import org.slf4j.Logger


class BuildModule implements Comparable<BuildModule> {
    private final File moduleDir
    private final File activeModulesFile
    private final String name
    private final BuildModuleRepository repository

    BuildModule(File modulesDir, File activeModulesDir, String name, BuildModuleRepository repository) {
        this.moduleDir = new File(modulesDir, name)
        this.activeModulesFile = new File(activeModulesDir, name)
        this.name = name
        this.repository = repository
    }

    String getName() {
        return name
    }

    boolean isActive() {
        return activeModulesFile.exists()
    }

    void setActive(boolean active, Logger logger) {
        if (active && !isActive()) {
            if (!moduleDir.exists()) {
                logger.warn("module $name does not exist, cloning module...")
                repository.cloneModule(moduleDir, logger)
            }

            logger.info("activating module $name")
            if (!activeModulesFile.mkdirs()) {
                logger.warn("could not activate module $name")
            }
        } else if (!active && isActive()) {
            logger.info("de-activating module $name")
            if (!activeModulesFile.deleteDir()) {
                logger.warn("could not de-activate module $name")
            }
        }
    }

    String toString() {
        StringBuilder sb = new StringBuilder(name)
        if (active) {
            sb.append(' (active)')
        }
        return sb.toString()
    }

    @Override
    int compareTo(BuildModule o) {
        return o.active <=> active ?: name <=> o.name
    }
}
