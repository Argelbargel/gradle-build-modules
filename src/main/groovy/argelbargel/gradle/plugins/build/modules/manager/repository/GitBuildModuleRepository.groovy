package argelbargel.gradle.plugins.build.modules.manager.repository

import argelbargel.gradle.plugins.build.modules.manager.BuildModuleRepository
import org.ajoberstar.grgit.Grgit
import org.slf4j.Logger

class GitBuildModuleRepository implements BuildModuleRepository {
    private String remoteUri

    GitBuildModuleRepository(String uri) {
        remoteUri = uri
    }

    @Override
    void cloneModule(File moduleDir, Logger logger) throws IOException {
        Grgit.clone(dir: moduleDir.absolutePath, uri: remoteUri, remote: "origin")
    }
}
