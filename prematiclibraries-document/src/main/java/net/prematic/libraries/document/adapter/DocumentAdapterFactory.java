/*
 * (C) Copyright 2019 The PrematicLibraries Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 20.12.19, 23:01
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

package net.prematic.libraries.document.adapter;

import net.prematic.libraries.utility.reflect.TypeReference;

/**
 * {@link DocumentAdapter} are restricted to a type. With factories you are able to dynamically
 * create adapters for different object types.
 *
 * <p>If no adapter was found for a type, all factory will be asked for a possible adapter.
 * Example: {@link net.prematic.libraries.document.adapter.defaults.HierarchyAdapterFactory}</p>
 */
public interface DocumentAdapterFactory {

    /**
     * Find and create an adapter for a type
     *
     * @param type The object type
     * @param <T> The new adapter, which corresponds to the reference type
     * @return The new adapter, if an adapter is available or null
     */
    <T> DocumentAdapter<T> create(TypeReference<T> type);

}
