package argelbargel.gradle.plugins.build.modules.manager

import org.gradle.util.ConfigureUtil

import static org.gradle.language.base.plugins.LifecycleBasePlugin.*

class DelegatedTaskReferenceContainer extends AbstractSet<DelegatedTaskReference> {
    static final Collection<DelegatedTaskReference> BASE_TASKS = [
            new DelegatedTaskReference(ASSEMBLE_TASK_NAME, BUILD_GROUP),
            new DelegatedTaskReference(BUILD_TASK_NAME, BUILD_GROUP),
            new DelegatedTaskReference(CHECK_TASK_NAME, VERIFICATION_GROUP),
            new DelegatedTaskReference(CLEAN_TASK_NAME, BUILD_GROUP)
    ]


    private final Set<DelegatedTaskReference> delegate

    DelegatedTaskReferenceContainer() {
        delegate = new HashSet<>()
    }

    void baseTasks() {
        delegate.addAll(BASE_TASKS)
    }

    @Override
    Iterator<DelegatedTaskReference> iterator() {
        return delegate.iterator()
    }

    @Override
    int size() {
        return delegate.size()
    }

    @Override
    boolean add(DelegatedTaskReference reference) {
        delegate.add(reference)
    }

    void task(String name) {
        add(new DelegatedTaskReference(name))
    }

    void task(Closure closure) {
        add(ConfigureUtil.configure(closure, new DelegatedTaskReference()))
    }

    void task(Map<String, String> map) {
        add(ConfigureUtil.configureByMap(map, new DelegatedTaskReference()))
    }
}
