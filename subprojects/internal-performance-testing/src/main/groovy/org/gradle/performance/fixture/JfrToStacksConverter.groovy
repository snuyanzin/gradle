/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.performance.fixture

import com.github.chrishantha.jfr.flamegraph.output.Application
import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * Converts JFR recordings to the collapsed stacks format used by the FlameGraph tool.
 */
@CompileStatic
@PackageScope
class JfrToStacksConverter {

    void convertToStacks(File jfrRecording, File stacks, String... options) {
        stacks.parentFile.mkdirs()
        String[] args = ["-f", jfrRecording.absolutePath, "-o", stacks.absolutePath]
        args += options
        Application.main(args)
    }
}
