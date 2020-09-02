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

package de.picturesafe.search.spring.configuration;

import de.picturesafe.search.elasticsearch.config.QueryConfiguration;
import de.picturesafe.search.elasticsearch.connect.filter.DefaultExpressionFilterFactory;
import de.picturesafe.search.elasticsearch.connect.filter.FilterFactory;
import de.picturesafe.search.elasticsearch.connect.query.FulltextQueryFactory;
import de.picturesafe.search.elasticsearch.connect.query.NestedQueryFactory;
import de.picturesafe.search.elasticsearch.connect.query.OperationExpressionQueryFactory;
import de.picturesafe.search.elasticsearch.connect.query.QueryFactory;
import de.picturesafe.search.elasticsearch.connect.query.RelevanceSortQueryFactory;
import de.picturesafe.search.elasticsearch.connect.query.preprocessor.StandardQuerystringPreprocessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;

import static de.picturesafe.search.elasticsearch.timezone.TimeZoneAware.DEFAULT_TIME_ZONE;

@Configuration
@PropertySource(value = "classpath:${ES_PROPERTIES_CLASSPATH:elasticsearch.properties}", ignoreResourceNotFound = true)
@Import({FulltextQueryFactory.class, NestedQueryFactory.class, OperationExpressionQueryFactory.class, StandardQuerystringPreprocessor.class})
public class DefaultQueryConfiguration {

    @Value("${elasticsearch.service.time_zone:" + DEFAULT_TIME_ZONE + "}")
    private String elasticsearchTimeZone;

    @Bean
    public String elasticsearchTimeZone() {
        return elasticsearchTimeZone;
    }

    @Bean
    public QueryConfiguration queryConfiguration() {
        return new QueryConfiguration();
    }

    @Bean
    public List<QueryFactory> queryFactories(FulltextQueryFactory fulltextQueryFactory, OperationExpressionQueryFactory operationExpressionQueryFactory,
                                             NestedQueryFactory nestedQueryFactory, RelevanceSortQueryFactory relevanceSortQueryFactory) {
        final List<QueryFactory> queryFactories = new ArrayList<>();
        queryFactories.add(fulltextQueryFactory);
        queryFactories.add(operationExpressionQueryFactory);
        queryFactories.add(nestedQueryFactory);
        queryFactories.add(relevanceSortQueryFactory);
        return queryFactories;
    }

    @Bean
    public List<FilterFactory> filterFactories(QueryConfiguration queryConfiguration, String elasticsearchTimeZone) {
        final List<FilterFactory> filterFactories = new ArrayList<>();
        filterFactories.add(new DefaultExpressionFilterFactory(queryConfiguration, elasticsearchTimeZone));
        return filterFactories;
    }
}
