/*
 * Copyright 2020 picturesafe media/data/bank GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.picturesafe.search.elasticsearch.connect.util;

public class PhraseMatchHelper {

    public static boolean matchPhrase(String value) {
        return value.startsWith("\"") && value.endsWith("\"") || value.startsWith("{") && value.endsWith("}");
    }

    public static String escapePhraseMatchChars(String value) {
        return matchPhrase(value) ? value.substring(1, value.length() - 1) : value;
    }

    public static String replacePhraseMatchChars(String value) {
        if (value.startsWith("{") && value.endsWith("}")) {
            return "\"" + value.substring(1, value.length() - 1) + "\"";
        }

        return value;
    }
}
