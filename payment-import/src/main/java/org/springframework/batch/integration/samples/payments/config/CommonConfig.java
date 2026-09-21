/*
 * Copyright 2002-2026 the original author or authors.
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
package org.springframework.batch.integration.samples.payments.config;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Gunnar Hillert
 *
 */
@Configuration(proxyBeanMethods = false)
@EnableBatchProcessing(taskExecutorRef = "jobTaskExecutor")
@EnableJdbcJobRepository(
		dataSourceRef = "dataSource",
		transactionManagerRef = "transactionManager")
@Import(BatchConfig.class)
@ImportResource("classpath:META-INF/spring/integration-context.xml")
public class CommonConfig {

	@Bean(destroyMethod = "shutdown")
	public DataSource dataSource() {
		final EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		builder.setType(EmbeddedDatabaseType.HSQL)
			.generateUniqueName(true)
			.addScript("classpath:/org/springframework/batch/core/schema-hsqldb.sql")
			.addScript("classpath:database/dbinit.sql");
		return builder.build();
	}

	@Bean
	public PlatformTransactionManager transactionManager(
			DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

}
