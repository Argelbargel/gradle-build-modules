package argelbargel.gradle.plugins.buildModules.module

import argelbargel.gradle.plugins.buildModules.common.BuildModuleSubstitution
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.specs.Spec

final class BuildModuleSubstitutionAction implements Action<Configuration> {
    private final Project project
    private final BuildModuleSubstitution substitution
    private final Spec<Dependency> substitutionSpec

    BuildModuleSubstitutionAction(Project project, BuildModuleSubstitution substitution, Spec<Dependency> substitutionSpec) {
        this.project = project
        this.substitution = substitution
        this.substitutionSpec = substitutionSpec
    }

    void execute(Configuration target) {
        try {
            apply(target)
        } catch (Exception e) {
            project.logger.warn("could not substitute dependencies to ${this} in ${target}: ${e.message}", e)
        }
    }

    private void apply(Configuration target) {
        target.dependencies.all {
            if (shouldSubstitute(it)) {
                project.logger.info("substituting ${it} with ${this} in ${target}")
                target.dependencies.remove(it)
                target.dependencies.add(project.dependencies.project(path: project.path + substitution.path, configuration: substitution.configuration))
                target.resolutionStrategy {
                    preferProjectModules()
                }
            }
        }
    }

    final boolean shouldSubstitute(Dependency requested) {
        return requested instanceof ModuleDependency && substitutes(requested)
    }

    private boolean substitutes(ModuleDependency requested) {
        if (requested.version == null || requested.version =~ /^[+]?$/) {
            if (substitutionSpec.isSatisfiedBy(requested)) {
                return "${requested.group}:${requested.name}" as String == substitution.dependency
            }
        }

        return false
    }

    @Override
    String toString() {
        return [project.path + substitution.path, substitution.configuration].join('@')
    }

    @Override
    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        BuildModuleSubstitutionAction that = (BuildModuleSubstitutionAction) o

        if (project != that.project) return false
        if (substitution != that.substitution) return false
        if (substitutionSpec != that.substitutionSpec) return false

        return true
    }

    @Override
    int hashCode() {
        int result
        result = project.hashCode()
        result = 31 * result + substitution.hashCode()
        result = 31 * result + substitutionSpec.hashCode()
        return result
    }
}
