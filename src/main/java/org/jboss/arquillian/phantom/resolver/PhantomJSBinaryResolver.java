/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.jboss.arquillian.phantom.resolver;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:jpapouse@redhat.com">Jan Papousek</a>
 */
public interface PhantomJSBinaryResolver {

    /**
     * Resolves a phantomjs binary file to the given destination. If
     * the destination file exists, it tries to check whether the file is up to date
     * and if it isn't, it is overridden.
     *
     * @throws IOException if there is a problem with resolving
     */
    PhantomJSBinary resolve(File destination) throws IOException;

    /**
     * Resolves a phantomjs binary file with the given version to the given destination. If
     * the destination file exists, it tries to check whether the file is up to date
     * and if it isn't, it is overridden.
     *
     * @throws IOException if there is a problem with resolving
     */
    PhantomJSBinary resolve(File destination, String version) throws IOException;

}
