package argelbargel.gradle.plugins.buildModules.manager.includer.projects

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.StartParameter
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.initialization.IncludedBuild
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.ObjectConfigurationAction
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.plugins.PluginManager
import org.gradle.internal.MutableActionSet

import static org.gradle.util.ConfigureUtil.configureUsing

class GradleDecorator implements Gradle {
    private final Gradle delegate
    private final SettingsDecorator settings
    private final MutableActionSet<Project> moduleProjectActions
    private Project moduleProject


    GradleDecorator(Gradle delegate, SettingsDecorator settings) {
        this.delegate = delegate
        this.settings = settings
        this.moduleProjectActions = new MutableActionSet<Project>()
        delegate.allprojects {
            if (it.path == settings.rootProject.path) {
                moduleProject = it
                moduleProjectActions.execute(moduleProject)
            }
        }
    }

    @Override
    Project getRootProject() throws IllegalStateException {
        if (moduleProject == null) {
            throw new IllegalStateException("The module project is not yet available for " + this + ".")
        }
        return moduleProject
    }

    @Override
    void rootProject(Action<? super Project> action) {
        if (moduleProject != null) {
            action.execute(moduleProject)
        } else {
            moduleProjectActions.add(action)
        }
    }

    @Override
    void allprojects(Action<? super Project> action) {
        delegate.allprojects(new FilterModuleProjects(action))
    }


    @Override
    void beforeProject(Closure closure) {
        beforeProject(configureUsing(closure))
    }

    @Override
    void beforeProject(Action<? super Project> action) {
        delegate.beforeProject(new FilterModuleProjects(action))
    }

    @Override
    void afterProject(Closure closure) {
        afterProject(configureUsing(closure))
    }

    @Override
    void afterProject(Action<? super Project> action) {
        delegate.afterProject(new FilterModuleProjects(action))
    }

    @Override
    void buildStarted(Closure closure) {
        buildStarted(configureUsing(closure))
    }

    @Override
    void buildStarted(Action<? super Gradle> action) {
        delegate.buildStarted {
            action.execute(this)
        }
    }

    @Override
    void settingsEvaluated(Closure closure) {
        settingsEvaluated(configureUsing(closure))
    }

    @Override
    void settingsEvaluated(Action<? super Settings> action) {
        delegate.settingsEvaluated {
            action.execute(settings)
        }
    }

    @Override
    void projectsLoaded(Closure closure) {
        projectsLoaded(configureUsing(closure))
    }

    @Override
    void projectsLoaded(Action<? super Gradle> action) {
        delegate.projectsLoaded {
            action.execute(this)
        }
    }

    @Override
    void projectsEvaluated(Closure closure) {
        projectsEvaluated(configureUsing(closure))
    }

    @Override
    void projectsEvaluated(Action<? super Gradle> action) {
        delegate.projectsEvaluated {
            action.execute(this)
        }
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
    Gradle getParent() {
        return delegate
    }

    @Override
    Gradle getGradle() {
        return this
    }

    @Override
    String getGradleVersion() {
        return delegate.getGradleVersion()
    }

    @Override
    File getGradleUserHomeDir() {
        return delegate.getGradleUserHomeDir()
    }

    @Override
    File getGradleHomeDir() {
        return delegate.getGradleHomeDir()
    }

    @Override
    TaskExecutionGraph getTaskGraph() {
        return delegate.getTaskGraph()
    }

    @Override
    StartParameter getStartParameter() {
        return delegate.getStartParameter()
    }

    @Override
    ProjectEvaluationListener addProjectEvaluationListener(ProjectEvaluationListener listener) {
        return delegate.addProjectEvaluationListener(listener)
    }

    @Override
    void removeProjectEvaluationListener(ProjectEvaluationListener listener) {
        delegate.removeProjectEvaluationListener(listener)
    }

    @Override
    void buildFinished(Closure closure) {
        delegate.buildFinished(closure)
    }

    @Override
    void buildFinished(Action<? super BuildResult> action) {
        delegate.buildFinished(action)
    }

    @Override
    void addBuildListener(BuildListener buildListener) {
        delegate.addBuildListener(buildListener)
    }

    @Override
    void addListener(Object listener) {
        delegate.addListener(listener)
    }

    @Override
    void removeListener(Object listener) {
        delegate.removeListener(listener)
    }

    @Override
    void useLogger(Object logger) {
        delegate.useLogger(logger)
    }

    @Override
    Collection<IncludedBuild> getIncludedBuilds() {
        return delegate.getIncludedBuilds()
    }

    @Override
    IncludedBuild includedBuild(String name) {
        return delegate.includedBuild(name)
    }

    @Override
    PluginContainer getPlugins() {
        return delegate.getPlugins()
    }

    @Override
    void apply(Closure closure) {
        apply(configureUsing(closure))
    }

    @Override
    PluginManager getPluginManager() {
        return delegate.getPluginManager()
    }

    private class FilterModuleProjects implements Action<Project> {
        private final Action<Project> delegate

        private FilterModuleProjects(Action<Project> delegate) {
            this.delegate = delegate
        }

        @Override
        void execute(Project project) {
            if (project.path == settings.rootProject.path || (project.parent != null && project.parent.path == settings.rootProject.path)) {
                delegate.execute(project)
            }
        }
    }
}
