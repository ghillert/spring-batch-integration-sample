/*
 * Copyright 2026 the original author or authors.
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

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.integration.samples.payments.PaymentChunkListener;
import org.springframework.batch.integration.samples.payments.PaymentFieldSetMapper;
import org.springframework.batch.integration.samples.payments.PaymentProcessor;
import org.springframework.batch.integration.samples.payments.PaymentWriter;
import org.springframework.batch.integration.samples.payments.model.Payment;
import org.springframework.batch.integration.samples.payments.service.BusinessService;
import org.springframework.batch.integration.samples.payments.service.SuperSlowBusinessService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.concurrent.Future;

/**
 * @author Gunnar Hillert
 */
@Configuration(proxyBeanMethods = false)
public class BatchConfig {

	@Bean
	public Job importPayments(
			JobRepository jobRepository,
			@Qualifier("loadPayments") Step loadPayments,
			@Qualifier("notificationExecutionsListener")
			JobExecutionListener notificationListener) {

		return new JobBuilder("importPayments", jobRepository)
				.start(loadPayments)
				.listener(notificationListener)
				.build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Payment> itemReader(
			@Value("#{jobParameters['input.file.name']}") String filename) {

		return new FlatFileItemReaderBuilder<Payment>()
				.name("itemReader")
				.resource(new FileSystemResource(filename))
				.linesToSkip(1)
				.delimited()
				.names("source", "destination", "amount", "date")
				.fieldSetMapper(new PaymentFieldSetMapper())
				.build();
	}

	@Bean
	public PaymentWriter itemWriter(DataSource dataSource) {
		return new PaymentWriter(dataSource);
	}

	@Bean
	public PaymentChunkListener paymentChunkListener() {
		return new PaymentChunkListener();
	}

	// Add this module's methods here.

	@Bean
	public BusinessService businessService() {
		return new SuperSlowBusinessService();
	}

	@Bean
	@Profile("without-spring-integration")
	public ItemProcessor<Payment, Payment> itemProcessor(
			BusinessService businessService) {
		return new PaymentProcessor(businessService);
	}

	@Bean
	public ThreadPoolTaskExecutor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(100);
		executor.setMaxPoolSize(100);
		executor.setThreadNamePrefix("payment-async-");
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(120);
		return executor;
	}

	@Bean
	public AsyncItemProcessor<Payment, Payment> itemProcessorAsync(
			@Qualifier("itemProcessor")
			ItemProcessor<Payment, Payment> delegate,
			ThreadPoolTaskExecutor asyncExecutor) {

		AsyncItemProcessor<Payment, Payment> processor =
				new AsyncItemProcessor<>(delegate);
		processor.setTaskExecutor(asyncExecutor);
		return processor;
	}

	@Bean
	public AsyncItemWriter<Payment> asyncWriter(PaymentWriter itemWriter) {
		return new AsyncItemWriter<>(itemWriter);
	}

	@Bean
	public Step loadPayments(
			JobRepository jobRepository,
			PlatformTransactionManager transactionManager,
			FlatFileItemReader<Payment> itemReader,
			AsyncItemProcessor<Payment, Payment> itemProcessorAsync,
			AsyncItemWriter<Payment> asyncWriter,
			PaymentChunkListener paymentChunkListener) {

		return new StepBuilder("loadPayments", jobRepository)
				.<Payment, Future<Payment>>chunk(10)
				.transactionManager(transactionManager)
				.reader(itemReader)
				.processor(itemProcessorAsync)
				.writer(asyncWriter)
				.listener(paymentChunkListener)
				.build();
	}
}