package argelbargel.gradle.plugins.build.modules.manager

import org.ajoberstar.grgit.Grgit
import org.gradle.api.logging.Logger

class BuildModule implements Comparable<BuildModule> {
    private final File moduleDir
    private final File activeModulesFile
    private final String name
    private final String uri

    BuildModule(File modulesDir, File activeModulesDir, String name, String uri) {
        this.moduleDir = new File(modulesDir, name)
        this.activeModulesFile = new File(activeModulesDir, name)
        this.name = name
        this.uri = uri
    }

    String getName() {
        return name
    }

    String getUrl() {
        return uri
    }

    boolean isActive() {
        return activeModulesFile.exists()
    }

    void setActive(boolean active, Logger logger) {
        if (active && !isActive()) {
            if (!moduleDir.exists()) {
                cloneModule(logger)
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

    void cloneModule(Logger logger) {
        logger.warn("module $name does not exist, cloning module...")
        Grgit.clone(dir: moduleDir.absolutePath, uri: uri, remote: "origin")
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
