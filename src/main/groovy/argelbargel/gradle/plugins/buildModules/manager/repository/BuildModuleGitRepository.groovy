package argelbargel.gradle.plugins.buildModules.manager.repository

import argelbargel.gradle.plugins.buildModules.manager.BuildModuleRepository
import org.ajoberstar.grgit.Grgit
import org.gradle.api.logging.Logger

import java.util.regex.Matcher
import java.util.regex.Pattern

import static java.util.regex.Pattern.compile
import static org.apache.commons.lang3.StringUtils.defaultString

class BuildModuleGitRepository extends BuildModuleRepository {
    static final String DEFAULT_BRANCH = 'master'
    private static final Pattern DEFINITION_PATTERN = compile('^(?<uri>.+?)(?:;(?<branch>.+))?$')

    BuildModuleGitRepository(String definition) {
        super(definition)
    }

    @Override
    protected void initialize(String definition, String targetPath, Logger logger) throws IOException {
        def matcher = matchDefinition(definition)
        def uri = matcher.group('uri')
        logger.info("cloning ${uri} to ${targetPath}...")
        Grgit.clone(dir: targetPath, uri: uri, remote: "origin", checkout: false).close()
    }


    @Override
    void update(String definition, String targetPath, Logger logger) throws IOException {
        def matcher = matchDefinition(definition)
        def branch = defaultString(matcher.group('branch'), DEFAULT_BRANCH)
        def git = Grgit.open(dir: targetPath)
        try {
            def localBranch = git.branch.list(mode: 'LOCAL').find { it.name == branch }
            if (localBranch == null) {
                logger.info("Creating local branch $branch tracking refs/remotes/origin/${branch}...")
                git.branch.add(name: branch, startPoint: "refs/remotes/origin/${branch}", mode: 'TRACK')
            }

            if (localBranch == null || git.branch.current().name != branch) {
                logger.info("Checking out branch $branch...")
                git.checkout(branch: branch, createBranch: false)
            }
        } finally {
            git.close()
        }
    }

    private static Matcher matchDefinition(String definition) {
        def matcher = DEFINITION_PATTERN.matcher(definition)
        if (!matcher.matches()) {
            throw new IOException("invalid/empty module-definition: $definition")
        }

        return matcher
    }
}
