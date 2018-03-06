package argelbargel.gradle.plugins.buildModules.manager.includer.projects

import org.apache.commons.io.FilenameUtils
import org.gradle.StartParameter
import org.gradle.api.Action
import org.gradle.api.UnknownProjectException
import org.gradle.api.initialization.ConfigurableIncludedBuild
import org.gradle.api.initialization.ProjectDescriptor
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.ObjectConfigurationAction
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.plugins.PluginManager
import org.gradle.caching.configuration.BuildCacheConfiguration
import org.gradle.plugin.management.PluginManagementSpec
import org.gradle.util.ConfigureUtil
import org.gradle.vcs.SourceControl


final class SettingsDecorator implements Settings {
    private final Settings delegate
    private final ProjectDescriptor module

    SettingsDecorator(ProjectDescriptor module, Settings delegate) {
        this.module = module
        this.delegate = delegate
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @Override
    void include(String... projectPaths) {
        delegate.include(*projectPaths.collect { pathRelativeToModule(it) })
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @Override
    void includeFlat(String... projectNames) {
        throw new UnsupportedOperationException("module $module may only include projects/directories below the module-directory $rootDir")
    }

    @Override
    File getSettingsDir() {
        return module.projectDir
    }

    @Override
    File getRootDir() {
        return rootProject.projectDir
    }

    @Override
    ProjectDescriptor getRootProject() {
        return module
    }

    @Override
    ProjectDescriptor project(String path) throws UnknownProjectException {
        return delegate.project(pathRelativeToModule(path))
    }

    @Override
    ProjectDescriptor findProject(String path) {
        return delegate.findProject(pathRelativeToModule())
    }

    @Override
    ProjectDescriptor project(File projectDir) throws UnknownProjectException {
        ensureRootDirContainsProjectDir(projectDir)
        return delegate.project(projectDir)
    }

    @Override
    ProjectDescriptor findProject(File projectDir) {
        if (!FilenameUtils.directoryContains(rootDir.canonicalPath, projectDir.canonicalPath)) {
            return null
        }

        return delegate.findProject(projectDir)
    }

    @Override
    void apply(Closure closure) {
        apply(ConfigureUtil.configureUsing(closure))
    }

    @Override
    void apply(Action<? super ObjectConfigurationAction> action) {
        delegate.apply(((ObjectConfigurationAction) action).to(this) as Action)
    }

    @Override
    void apply(Map<String, ?> options) {
        if (!options.containsKey('to')) {
            options.to = this
        }

        delegate.apply(options)
    }

    @Override
    Settings getSettings() {
        return this
    }

    @Override
    Gradle getGradle() {
        return new GradleDecorator(delegate.gradle, this)
    }

    @Override
    ScriptHandler getBuildscript() {
        return delegate.getBuildscript()
    }

    @Override
    StartParameter getStartParameter() {
        return delegate.getStartParameter()
    }

    @Override
    void includeBuild(Object rootProject) {
        delegate.includeBuild(rootProject)
    }

    @Override
    void includeBuild(Object rootProject, Action<ConfigurableIncludedBuild> configuration) {
        delegate.includeBuild(rootProject, configuration)
    }

    @Override
    BuildCacheConfiguration getBuildCache() {
        return delegate.getBuildCache()
    }

    @Override
    void buildCache(Action<? super BuildCacheConfiguration> action) {
        delegate.buildCache(action)

    }

    @Override
    void pluginManagement(Action<? super PluginManagementSpec> pluginManagementSpec) {
        delegate.pluginManagement(pluginManagementSpec)
    }

    @Override
    PluginManagementSpec getPluginManagement() {
        return delegate.getPluginManagement()
    }

    @Override
    void sourceControl(Action<? super SourceControl> configuration) {
        delegate.sourceControl(configuration)
    }

    @Override
    SourceControl getSourceControl() {
        return delegate.getSourceControl()
    }

    @Override
    void enableFeaturePreview(String s) {
        delegate.enableFeaturePreview(s)
    }

    @Override
    PluginContainer getPlugins() {
        return delegate.getPlugins()
    }

    @Override
    PluginManager getPluginManager() {
        return delegate.getPluginManager()
    }

    private String pathRelativeToModule(String path) {
        module.path + (path.startsWith(':') ? path : ":${path}")
    }

    private void ensureRootDirContainsProjectDir(File projectDir) {
        if (!FilenameUtils.directoryContains(rootDir.canonicalPath, projectDir.canonicalPath)) {
            throw new UnsupportedOperationException("module $module may only include projects/directories below the module-directory $rootDir, thus ${projectDir.canonicalPath} is not allowed")
        }
    }
}
