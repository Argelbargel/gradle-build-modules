package argelbargel.gradle.plugins.build.modules.common

import spock.lang.Specification

import static org.apache.commons.lang3.StringUtils.EMPTY

class BuildModulePropertiesTest extends Specification {
    def "Substitutions werden korrekt geparst"() {
        Map<String, String> properties = new LinkedHashMap<>()

        given:
        properties.put('buildModule.substitutes', 'test:main')
        properties.put('buildModule.substitutes.2', 'test:main:secondary')
        properties.put('buildModule.sub.substitutes', 'test:sub')
        properties.put('buildModule.sub.substitutes.2', 'test:sub:secondary')
        properties.put('buildModule.substitutes@config', 'test:config')
        properties.put('buildModule.substitutes@config.2', 'test:config:secondary')
        properties.put('buildModule.sub.substitutes@config', 'test:sub:config')
        properties.put('buildModule.sub.substitutes@config.2', 'test:sub:config:secondary')
        properties.put('buildModule.another-sub.substitutes', 'test:another-sub')

        when:
        Collection<BuildModuleSubstitution> substitutions = new BuildModuleProperties(properties).substitutions

        then:
        substitutions[0] == new BuildModuleSubstitution('test:main')
        substitutions[1] == new BuildModuleSubstitution('test:main:secondary')
        substitutions[2] == new BuildModuleSubstitution('test:sub', 'sub')
        substitutions[3] == new BuildModuleSubstitution('test:sub:secondary', 'sub')
        substitutions[4] == new BuildModuleSubstitution('test:config',  EMPTY,'config')
        substitutions[5] == new BuildModuleSubstitution('test:config:secondary',  EMPTY,'config')
        substitutions[6] == new BuildModuleSubstitution('test:sub:config',  'sub','config')
        substitutions[7] == new BuildModuleSubstitution('test:sub:config:secondary',  'sub','config')
        substitutions[8] == new BuildModuleSubstitution('test:another-sub',  'another-sub')
    }
}
