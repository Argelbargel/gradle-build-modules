package argelbargel.gradle.plugins.build.modules.common

import static org.apache.commons.lang3.StringUtils.EMPTY
import static org.apache.commons.lang3.StringUtils.isNotBlank
import static org.gradle.api.artifacts.Dependency.DEFAULT_CONFIGURATION

final class BuildModuleSubstitution {
    private static final String PROJECT_PATH_SEP = ':'

    private final String dependency
    private final String path
    private final String configuration

    BuildModuleSubstitution(String dependency, String path = EMPTY, String configuration = DEFAULT_CONFIGURATION) {
        this.dependency = dependency
        this.path = path.replaceAll('\\.', PROJECT_PATH_SEP)
        this.configuration = configuration
    }

    String getDependency() {
        return dependency
    }

    String getPath() {
        return isNotBlank(path) ? "${PROJECT_PATH_SEP}${path}" : path
    }

    String getConfiguration() {
        return configuration
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        BuildModuleSubstitution that = (BuildModuleSubstitution) o

        return getDependency() == that.getDependency() && getPath() == that.getPath() && getConfiguration() == that.getConfiguration()
    }

    int hashCode() {
        return dependency.hashCode() * +31 * path.hashCode() + 31 * configuration.hashCode()
    }

    @Override
    String toString() {
        return "substitutes ${dependency} with ${path}@${configuration}"
    }
}
