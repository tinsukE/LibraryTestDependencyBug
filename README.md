# LibraryTestDependencyBug
Test Project that reproduces an Android Studio bug.

## Setup

There are two test classes in the _app_ module, `BrokenUnitTest.kt` and `WorkingUnitTest.kt`. Both of them make use of classes defined in the test source folder of the _library_ module.

This is not possible by default, but adding this to the _library_'s `build.gradle`:
```
task jarTest (type: Jar, dependsOn: 'compileDebugUnitTestSources') {
    from 'build/intermediates/classes/test/debug', 'build/tmp/kotlin-classes/debugUnitTest'
    classifier = 'test'
}

configurations {
    testOutput
}

artifacts {
    testOutput jarTest
}
```

And this to the _app_'s `build.gradle`:
```
testImplementation project(path: ':library', configuration: 'testOutput')
```

Makes it possible.

## Problem

Running the tests via command like works fine:
> ./gradlew clean testDebugUnitTest

Running WorkingUnitTest, which uses a Java dependency, from Android Studio, works. But running BrokenUnitTest, which uses a Kotlin dependency, from Android Studio, fails with:
> java.lang.NoClassDefFoundError: com/tinsuke/library/KotlinTestDependency

By exploring the `java` command AS issues to run the tests I was able to see the following classpath entries related to the _library_ module:
- .../LibraryTestDependencyBug/library/build/intermediates/classes/test/debug
- .../LibraryTestDependencyBug/library/build/intermediates/classes/debug
- .../LibraryTestDependencyBug/library/build/intermediates/sourceFolderJavaResources/debug
- .../LibraryTestDependencyBug/library/build/tmp/kotlin-classes/debug

As one can see, AS adds entries for BOTH the library main Java classes (`build/intermediates/classes/debug`) and test Java classes (`build/intermediates/classes/debug`). But for Kotlin, only the main classes entry is there (`build/tmp/kotlin-classes/debug`), while I believe the test classes entry (`build/tmp/kotlin-classes/debugUnitTest`) should be there too.

The reasoning for my belief and reporting this as a bug is that the command line execution works fine and that AS execution works for Java.
