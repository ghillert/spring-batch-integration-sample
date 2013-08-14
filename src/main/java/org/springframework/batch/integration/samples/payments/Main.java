/*
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.batch.integration.samples.payments;

import java.util.Scanner;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.samples.payments.util.SpringIntegrationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Starts the Spring Context and will initialize the Spring Integration routes.
 *
 * @author Gunnar Hillert
 *
 */
public final class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class);

	private Main() { }

	/**
	 * Load the Spring Integration Application Context
	 *
	 * @param args - command line arguments
	 */
	public static void main(final String... args) {

		System.out.println("\n========================================================="
						+ "\n    Welcome to the Spring Batch Integration              "
						+ "\n          Payments Import Sample                         "
						+ "\n                                                         "
						+ "\n    For more information please visit:                   "
						+ "\n    http://www.springsource.org/spring-integration       "
						+ "\n                                                         "
						+ "\n=========================================================" );

		final AbstractApplicationContext context =
				new ClassPathXmlApplicationContext("classpath:META-INF/spring/batch-context.xml",
						"classpath:META-INF/spring/integration-context.xml");

		context.registerShutdownHook();

		final JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
		final QueueChannel statusesChannel = context.getBean("statuses", QueueChannel.class);
		final JobRepository jobRepository = context.getBean(JobRepository.class);

		SpringIntegrationUtils.displayDirectories(context);


		final Scanner scanner = new Scanner(System.in);

		System.out.println("\n========================================================="
						+ "\n                                                         "
						+ "\n    Waiting for Job execution to finish.                 "
						+ "\n                                                         "
						+ "\n=========================================================" );

		JobExecution jobExecution = ((Message<JobExecution>) statusesChannel.receive(120000)).getPayload();
		ExitStatus exitStatus = jobExecution.getExitStatus();
		Assert.assertEquals(ExitStatus.COMPLETED, exitStatus);
		int count = jdbcTemplate.queryForInt("select count(*) from payments");

		System.out.println(String.format("\nDONE!!\nexitStatus: %s; imported # of payments: %s",
				exitStatus.getExitCode(), count));

		System.exit(0);

	}
}
