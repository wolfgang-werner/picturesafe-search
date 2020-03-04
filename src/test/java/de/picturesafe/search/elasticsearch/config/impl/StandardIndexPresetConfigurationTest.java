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

package de.picturesafe.search.elasticsearch.config.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class StandardIndexPresetConfigurationTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testConstructor() {

        String indexAlias = "test-alias";
        String indexNamePrefix = "test-index";
        final String indexNameDateFormat = "yyyyMMdd-HHmmss-SSS";
        final int numberOfShards = 1;
        final int numberOfReplicas = 2;
        final int maxResultWindow = 100;

        StandardIndexPresetConfiguration standardIndexPresetConfiguration
                = new StandardIndexPresetConfiguration(indexAlias, indexNamePrefix, indexNameDateFormat, numberOfShards, numberOfReplicas, maxResultWindow);

        assertEquals("test-alias", standardIndexPresetConfiguration.getIndexAlias());
        assertTrue(standardIndexPresetConfiguration.createNewIndexName().startsWith("test-index-"));
        assertEquals(1, standardIndexPresetConfiguration.getNumberOfShards());
        assertEquals(2, standardIndexPresetConfiguration.getNumberOfReplicas());
        assertEquals(100, standardIndexPresetConfiguration.getMaxResultWindow());

        indexAlias = "test-alias";
        indexNamePrefix = "";
        standardIndexPresetConfiguration
                = new StandardIndexPresetConfiguration(indexAlias, indexNamePrefix, indexNameDateFormat, numberOfShards, numberOfReplicas, maxResultWindow);

        assertEquals("test-alias", standardIndexPresetConfiguration.getIndexAlias());
        assertTrue(standardIndexPresetConfiguration.createNewIndexName().startsWith("test-alias-"));

        indexAlias = "test-alias";
        indexNamePrefix = null;
        standardIndexPresetConfiguration
                = new StandardIndexPresetConfiguration(indexAlias, indexNamePrefix, indexNameDateFormat, numberOfShards, numberOfReplicas, maxResultWindow);
        assertEquals("test-alias", standardIndexPresetConfiguration.getIndexAlias());
        assertTrue(standardIndexPresetConfiguration.createNewIndexName().startsWith("test-alias-"));

        indexAlias = null;
        indexNamePrefix = null;
        exception.expect(NullPointerException.class);
        exception.expectMessage("Argument 'indexAlias' must not be empty!");
        new StandardIndexPresetConfiguration(indexAlias, indexNamePrefix, indexNameDateFormat, numberOfShards, numberOfReplicas, maxResultWindow);

        indexAlias = "";
        indexNamePrefix = null;
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Argument 'indexAlias' must not be empty!");
        new StandardIndexPresetConfiguration(indexAlias, indexNamePrefix, indexNameDateFormat, numberOfShards, numberOfReplicas, maxResultWindow);
    }
}
