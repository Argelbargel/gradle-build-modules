package argelbargel.gradle.plugins.buildModules.common

import java.util.regex.Matcher
import java.util.regex.Pattern

import static argelbargel.gradle.plugins.buildModules.common.BuildModuleConstants.*
import static java.lang.Boolean.parseBoolean
import static java.util.Collections.singleton
import static java.util.regex.Pattern.compile
import static java.util.regex.Pattern.quote
import static org.apache.commons.lang3.StringUtils.EMPTY
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.gradle.api.artifacts.Dependency.DEFAULT_CONFIGURATION

final class BuildModuleProperties {
    private static final Pattern MODULE_SUBSTITUE_PATTERN = compile('^buildModule(?:\\.(?<path>[.\\w-]+))?' + quote('.substitutes') + '(?:@(?<configuration>[\\w]+))?(?:\\.\\d+)?$')

    private static Properties loadProperties(File propertiesFile) {
        if (!propertiesFile.exists()) {
            throw new FileNotFoundException("module properties at " + propertiesFile + " do not exist")
        }

        Properties properties = new Properties()
        propertiesFile.withInputStream {
            properties.load(it)
        }

        ensureRequiredProperties(properties, propertiesFile, PROPERTY_MODULE_NAME, PROPERTY_MODULE_GROUP)
        return properties
    }

    private static void ensureRequiredProperties(Properties properties, File file, String... names) {
        names.each {
            if (isBlank(properties.getProperty(it, EMPTY))) {
                throw new IllegalStateException("module must define ${it} in ${file}")
            }
        }
    }


    private final Map<String, Object> delegate

    BuildModuleProperties(File propertiesFile) {
        this(loadProperties(propertiesFile) as Map<String, Object>)
    }

    BuildModuleProperties(Map<String, Object> delegate) {
        this.delegate = delegate
    }

    String getName() {
        return delegate.get(PROPERTY_MODULE_NAME)
    }

    String getGroup() {
        return delegate.get(PROPERTY_MODULE_GROUP)
    }

    String getConfiguration() {
        delegate.getOrDefault(PROPERTY_MODULE_CONFIGURATION, DEFAULT_CONFIGURATION) as String
    }

    Collection<BuildModuleSubstitution> getSubstitutions() {
        Collection<BuildModuleSubstitution> substitutions = parseSubstitutionProperties(delegate, configuration)
        if (substitutions.isEmpty()) {
            return singleton(new BuildModuleSubstitution("${group}:${name}", EMPTY, configuration))
        }

        return substitutions
    }

    boolean isIncludeBuild() {
        return parseBoolean(delegate.getOrDefault(PROPERTY_MODULE_INCLUDEBUILD, 'false') as String)
    }

    private static Collection<BuildModuleSubstitution> parseSubstitutionProperties(Map<String, Object> properties, String defaultConfiguration) {
        Collection<BuildModuleSubstitution> substitutions = new LinkedHashSet<>()
        properties.keySet().each {
            Matcher m = MODULE_SUBSTITUE_PATTERN.matcher(it)
            if (m.matches()) {
                substitutions.add(new BuildModuleSubstitution(properties.get(it) as String, m.group('path') ?: EMPTY, m.group('configuration') ?: defaultConfiguration))
            }
        }
        return substitutions
    }
}
