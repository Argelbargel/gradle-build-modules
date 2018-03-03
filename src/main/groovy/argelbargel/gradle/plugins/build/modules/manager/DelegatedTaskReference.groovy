package argelbargel.gradle.plugins.build.modules.manager

import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.tasks.TaskReference

class DelegatedTaskReference implements TaskReference, Action<Task> {
    private static final String DEFAULT_GROUP = "other"

    private String name
    private String group


    DelegatedTaskReference() {
        this("")
    }

    DelegatedTaskReference(String name) {
        this(name, DEFAULT_GROUP)
    }

    DelegatedTaskReference(String name, String group) {
        this.name = name
        this.group = group
    }

    void setName(String name) {
        this.name = name
    }

    @Override
    String getName() {
        return name
    }

    void setGroup(String group) {
        this.group = group
    }

    String getGroup() {
        return group
    }

    @Override
    void execute(Task task) {
        task.group = group
        task.dependsOn task.project.gradle.includedBuilds*.task(task.path)
    }
}
