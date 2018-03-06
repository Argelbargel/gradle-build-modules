package argelbargel.gradle.plugins.buildModules.manager

import org.slf4j.Logger

interface BuildModuleRepository {
    void cloneModule(File moduleDir, Logger logger) throws IOException
}
