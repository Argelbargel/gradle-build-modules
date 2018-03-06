package argelbargel.gradle.plugins.buildModules.module

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

class SubstitutionUtility {
    private static final Set<AppliedSubstitution> appliedSubstitutions = new HashSet<>()

    static void clearSubstitutions() {
        appliedSubstitutions.clear()
    }

    static void substitute(Project project, Project module) {
        project.configurations.all { configuration ->
            AppliedSubstitution substitution = new AppliedSubstitution(configuration, module)
            if (!appliedSubstitutions.contains(substitution)) {
                appliedSubstitutions.add(substitution.apply())
            }
        }
    }


    private SubstitutionUtility() { /* utility */ }


    private static class AppliedSubstitution {
        private final Configuration configuration
        private final Project module

        AppliedSubstitution(Configuration configuration, Project module) {
            this.configuration = configuration
            this.module = module
        }

        AppliedSubstitution apply() {
            module.extensions.getByType(BuildModuleExtension).substitutions.each {
                it.execute(configuration)
            }

            return this
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            AppliedSubstitution that = (AppliedSubstitution) o
            return configuration == that.configuration && module == that.module
        }

        int hashCode() {
            return 31 * configuration.hashCode() + module.hashCode()
        }
    }
}
