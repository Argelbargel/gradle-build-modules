package argelbargel.gradle.plugins.buildModules.manager

import argelbargel.gradle.plugins.buildModules.manager.repository.BuildModuleGitRepository
import argelbargel.gradle.plugins.buildModules.manager.repository.BuildModuleNoopRepository

class BuildModuleRepositories {
    static final Class<BuildModuleRepository> GIT = BuildModuleGitRepository
    static final Class<BuildModuleRepository> NOOP = BuildModuleNoopRepository

    private BuildModuleRepositories() { /* no instances required */ }
}
