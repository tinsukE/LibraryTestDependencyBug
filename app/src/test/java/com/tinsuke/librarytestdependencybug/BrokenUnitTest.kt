package com.tinsuke.librarytestdependencybug

import com.tinsuke.library.KotlinTestDependency
import org.junit.Test

class BrokenUnitTest {
    @Test
    fun kotlinTestDependency() {
        KotlinTestDependency()
    }
}
