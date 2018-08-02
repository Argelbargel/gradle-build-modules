package argelbargel.gradle.plugins.buildModules.manager

import org.gradle.api.logging.Logger

class BuildModule implements Comparable<BuildModule> {
    private final String name
    private final BuildModuleRepository repository
    private final File activeModulesFile
    private final File moduleDir

    BuildModule(String name, BuildModuleRepository repository, File modulesDir, File activeModulesDir) {
        this.name = name
        this.repository = repository
        this.moduleDir = new File(modulesDir, name)
        this.activeModulesFile = new File(activeModulesDir, name)
    }

    String getName() {
        return name
    }

    File getModuleDir() {
        return moduleDir
    }

    boolean isActive() {
        return activeModulesFile.exists()
    }

    void setActive(boolean active, Logger logger) {
        if (active) {
            if (!isActive()) {
                logger.info("activating module $name")
                if (!activeModulesFile.mkdirs()) {
                    logger.warn("could not activate module $name")
                }
            }
        } else if (!active && isActive()) {
            logger.info("de-activating module $name")
            if (!activeModulesFile.deleteDir()) {
                logger.warn("could not de-activate module $name")
            }
        }
    }

    boolean update(Logger logger) {
        if (isActive()) {
            try {
                logger.info("updating module $name")
                repository.update(name, moduleDir, logger)
            } catch (IOException e) {
                logger.error("error updating module $name: ${e.message}", e)
                return false
            }
            return true
        }
        return false
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
