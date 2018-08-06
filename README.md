# Gradle Build-Modules-Plugin [![Build Status](https://travis-ci.org/Argelbargel/gradle-build-modules.svg?branch=master)](https://travis-ci.org/Argelbargel/gradle-build-modules)

This is a Gradle-Plugin which allows you to easily create a gradle-build which ties together multiple otherwise separate 
gradle-projects. It's use-case is quite similar to that of gradle's own [Composite builds](https://docs.gradle.org/current/userguide/composite_builds.html).


## Apply plugins to your project(s)

See `src/example` for a minimal example-project.

In `settings.gradle` of your master-build:
```
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven { url 'https://raw.githubusercontent.com/Argelbargel/gradle-build-modules/mvn-repo/' }
    }
    dependencies {
        classpath 'argelbargel.gradle.plugins:build-modules:0.5'
    }
}

apply plugin: 'argelbargel.build-modules.manager'
``` 


Additionally you must add a file `build-modules.properties` listing all modules and their source-code-repositories (currently only git is supported), for example:
```
module1=http\://git.example.com/module1.git
module2=http\://git.example.com/module2.git
```


In `build.gradle` of any project which should be a module of your master-build:
``` 
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven { url 'https://raw.githubusercontent.com/Argelbargel/gradle-build-modules/mvn-repo/' }
    }
    dependencies {
        classpath 'argelbargel.gradle.plugins:build-modules:0.5'
    }
}

apply plugin: 'argelbargel.build-modules.module'
```

Additonally you must add the following properties to `gradle.properties` of each module:

```
buildModule.name=<module-name>
buildModule.group=<module-group>
```