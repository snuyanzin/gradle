/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.initialization;

import org.gradle.api.internal.ClassPathRegistry;
import org.gradle.internal.classloader.FilteringClassLoader;
import org.gradle.internal.reflect.Instantiator;

public class DefaultClassLoaderRegistry implements ClassLoaderRegistry {
    private final ClassLoader apiOnlyClassLoader;
    private final ClassLoader apiAndPluginsClassLoader;
    private final ClassLoader pluginsClassLoader;
    private final ClassLoader workerPluginsClassLoader;
    private final Instantiator instantiator;

    public DefaultClassLoaderRegistry(ClassPathRegistry classPathRegistry, LegacyTypesSupport legacyTypesSupport, Instantiator instantiator) {
        this.instantiator = instantiator;
        ClassLoader runtimeClassLoader = getClass().getClassLoader();
        this.apiOnlyClassLoader = restrictToGradleApi(runtimeClassLoader);
        this.pluginsClassLoader = new MixInLegacyTypesClassLoader(runtimeClassLoader, classPathRegistry.getClassPath("GRADLE_EXTENSIONS"), legacyTypesSupport);
        this.workerPluginsClassLoader = new MixInLegacyTypesClassLoader(runtimeClassLoader, classPathRegistry.getClassPath("GRADLE_WORKER_EXTENSIONS"), legacyTypesSupport);
        this.apiAndPluginsClassLoader = restrictToGradleApi(pluginsClassLoader);
    }

    private ClassLoader restrictToGradleApi(ClassLoader classLoader) {
        return restrictTo(apiSpecFor(classLoader), classLoader);
    }

    private static ClassLoader restrictTo(FilteringClassLoader.Spec spec, ClassLoader parent) {
        return new FilteringClassLoader(parent, spec);
    }

    private FilteringClassLoader.Spec apiSpecFor(ClassLoader classLoader) {
        return GradleApiUtil.apiSpecFor(classLoader, instantiator);
    }

    @Override
    public ClassLoader getRuntimeClassLoader() {
        return getClass().getClassLoader();
    }

    @Override
    public ClassLoader getGradleApiClassLoader() {
        return apiAndPluginsClassLoader;
    }

    @Override
    public ClassLoader getPluginsClassLoader() {
        return pluginsClassLoader;
    }

    @Override
    public ClassLoader getGradleCoreApiClassLoader() {
        return apiOnlyClassLoader;
    }

    @Override
    public ClassLoader getWorkerPluginsClassLoader() {
        return workerPluginsClassLoader;
    }
}
