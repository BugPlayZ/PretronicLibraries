/*
 * (C) Copyright 2019 The PrematicLibraries Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.12.19, 21:10
 *
 * The PrematicDatabaseQuery Project is under the Apache License, version 2.0 (the "License");
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

package net.prematic.libraries.utility.map;

import net.prematic.libraries.utility.map.caseintensive.CaseIntensiveLinkedHashMap;
import net.prematic.libraries.utility.map.index.IndexMap;

import java.util.Map;

public class IndexCaseIntensiveLinkedHashMap<V> extends CaseIntensiveLinkedHashMap<V> implements IndexMap<String, V> {

    @Override
    public V getIndex(int index) {
        int i = 0;
        for (Map.Entry<String, V> entry : this.entrySet()) {
            if(i == index) return entry.getValue();
            i++;
        }
        return null;
    }
}
