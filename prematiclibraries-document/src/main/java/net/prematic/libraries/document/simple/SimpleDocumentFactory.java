/*
 * (C) Copyright 2019 The PrematicLibraries Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 09.06.19 22:58
 *
 * The PrematicLibraries Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.prematic.libraries.document.simple;

import net.prematic.libraries.document.*;

public class SimpleDocumentFactory implements DocumentFactory {

    @Override
    public DocumentContext newContext() {
        return new SimpleDocumentContext();
    }

    @Override
    public Document newDocument() {
        return newDocument(null);
    }

    @Override
    public Document newDocument(String key) {
        return new SimpleDocument(key);
    }

    @Override
    public PrimitiveEntry newPrimitiveEntry(String key, Object object) {
        return new SimplePrimitiveEntry(key,object);
    }

    @Override
    public ArrayEntry newArrayEntry(String key) {
        return new SimpleArrayEntry(key);
    }
}
