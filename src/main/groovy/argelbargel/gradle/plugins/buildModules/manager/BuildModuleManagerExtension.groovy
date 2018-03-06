package argelbargel.gradle.plugins.buildModules.manager

import argelbargel.gradle.plugins.buildModules.manager.repository.GitBuildModuleRepository
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

import static org.gradle.internal.reflect.DirectInstantiator.instantiate

class BuildModuleManagerExtension {
    static final String PROPERTY_MODULES_FILE = 'buildModules.propertiesFile'
    static final String DEFAULT_MODULES_FILE = "build-modules.properties"

    private final File modulesDir
    private final File activeModulesDir
    private final DelegatedTaskReferenceContainer delegatedTaskContainer

    private File modulesFile
    private List<BuildModule> moduleCache
    private Class<? extends BuildModuleRepository> repository

    BuildModuleManagerExtension(Project project, File modulesDir, File activeModulesDir) {
        this.modulesFile = new File(project.projectDir, project.properties.getOrDefault(PROPERTY_MODULES_FILE, DEFAULT_MODULES_FILE) as String)
        this.modulesDir = modulesDir
        this.activeModulesDir = activeModulesDir
        this.delegatedTaskContainer = new DelegatedTaskReferenceContainer()
        this.repository = GitBuildModuleRepository
    }

    void setModulesFile(File file) {
        modulesFile = file
        moduleCache = null
    }

    void setModulesFile(String file) {
        setModulesFile(new File(file))
    }

    File getModulesFile() {
        return modulesFile
    }

    void setRepository(Class<? extends BuildModuleRepository> impl) {
        repository = impl
    }

    Class<? extends BuildModuleRepository> getRepository() {
        return repository
    }

    File getModulesDir() {
        return modulesDir
    }

    File getActiveModulesDir() {
        return activeModulesDir
    }

    List<BuildModule> getActiveModules() {
        if (!activeModulesDir.exists()) {
            return []
        }

        return getAllModules().findAll { it.active }
    }

    List<BuildModule> getAllModules() {
        if (moduleCache == null) {
            moduleCache = readModulesFromPropertiesFile()
        }

        return moduleCache.sort()
    }

    DelegatedTaskReferenceContainer tasks(Closure closure) {
        ConfigureUtil.configure(closure, delegatedTaskContainer)
    }

    Set<DelegatedTaskReference> getDelegatedTasks() {
        return delegatedTaskContainer
    }

    private List<BuildModule> readModulesFromPropertiesFile() {
        if (modulesFile == null || !modulesFile.exists()) {
            return []
        }

        Properties modules = new Properties()
        modulesFile.withInputStream {
            modules.load(it)
        }
        
        return moduleCache = modules.stringPropertyNames().collect {
            new BuildModule(modulesDir, activeModulesDir, it, instantiate(repository, modules.getProperty(it)))
        }
    }
}
