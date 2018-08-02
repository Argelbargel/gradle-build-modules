package argelbargel.gradle.plugins.buildModules.manager.includer.projects

import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.dsl.ScriptHandler

class ScriptHandlerDecorator implements ScriptHandler {
    private final ScriptHandler delegate
    private final File sourceFile

    ScriptHandlerDecorator(ScriptHandler delegate, File sourceFile) {
        this.delegate = delegate
        this.sourceFile = sourceFile
    }

    @Override
    File getSourceFile() {
        return sourceFile
    }

    @Override
    URI getSourceURI() {
        return sourceFile.toURI()
    }

    @Override
    RepositoryHandler getRepositories() {
        return delegate.getRepositories()
    }

    @Override
    void repositories(Closure configureClosure) {
        delegate.repositories(configureClosure)
    }

    @Override
    DependencyHandler getDependencies() {
        return delegate.getDependencies()
    }

    @Override
    void dependencies(Closure configureClosure) {
        delegate.dependencies(configureClosure)
    }

    @Override
    ConfigurationContainer getConfigurations() {
        return delegate.getConfigurations()
    }

    @Override
    ClassLoader getClassLoader() {
        return delegate.getClassLoader()
    }
}
