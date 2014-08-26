/*
 * Copyright 2002-2014 the original author or authors.
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

import java.util.List;
import java.util.Scanner;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.integration.samples.payments.util.SpringIntegrationUtils;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.messaging.Message;
import org.springframework.util.StopWatch;

/**
 * Starts the Spring Context and will initialize the Spring Integration routes.
 *
 * @author Gunnar Hillert
 *
 */
public final class Main {

	private Main() { }

	/**
	 * Load the Spring Integration Application Context
	 *
	 * @param args - command line arguments
	 * @throws InterruptedException
	 */
	public static void main(final String... args) throws InterruptedException {

		final Scanner scanner = new Scanner(System.in);

		System.out.println("\n========================================================="
						+ "\n    Welcome to the Spring Batch Integration              "
						+ "\n          Payments Import Sample                         "
						+ "\n                                                         "
						+ "\n    For more information please visit:                   "
						+ "\n    http://www.springsource.org/spring-integration       "
						+ "\n                                                         "
						+ "\n=========================================================" );

		System.out.println("Please enter a choice and press <enter>: ");
		System.out.println("\t1. Use AsyncItemProcessor");
		System.out.println("\t2. Use AsyncItemProcessor with Spring Integration");
		System.out.print("Enter you choice: ");

		final GenericXmlApplicationContext context = new GenericXmlApplicationContext();

		while (true) {
			final String input = scanner.nextLine();

			if("1".equals(input.trim())) {
				context.getEnvironment().setActiveProfiles("without-spring-integration");
				break;
			} else if("2".equals(input.trim())) {
				context.getEnvironment().setActiveProfiles("with-spring-integration");
				break;
			} else if("q".equals(input.trim())) {
				System.out.println("Exiting application...bye.");
				System.exit(0);
			} else {
				System.out.println("Invalid choice\n\n");
				System.out.print("Enter you choice: ");
			}
		}

		context.load("classpath:META-INF/spring/batch-context.xml",
					"classpath:META-INF/spring/integration-context.xml");
		context.registerShutdownHook();
		context.refresh();

		final StopWatch stopWatch = new StopWatch("Total Execution");
		stopWatch.start("Import Payments");

		final JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);

		SpringIntegrationUtils.displayDirectories(context);

		System.out.println("\n========================================================="
						+ "\n                                                         "
						+ "\n    Waiting for Job execution to finish.                 "
						+ "\n                                                         "
						+ "\n=========================================================" );

		final QueueChannel completeApplicationChannel =
				context.getBean("completeApplication", QueueChannel.class);

		@SuppressWarnings("unchecked")
		final Message<JobExecution> jobExecutionMessage = (Message<JobExecution>) completeApplicationChannel.receive();
		final JobExecution jobExecution = jobExecutionMessage.getPayload();
		final ExitStatus exitStatus = jobExecution.getExitStatus();
		final int count = jdbcTemplate.queryForObject("select count(*) from payments", Integer.class);

		System.out.println(String.format("\nDONE!!\nexitStatus: %s; # of payments imported: %s",
				exitStatus.getExitCode(), count));

		final StubJavaMailSender mailSender = context.getBean(StubJavaMailSender.class);
		final List<SimpleMailMessage> emails = mailSender.getSentSimpleMailMessages();
		final int numberOfSentNotifications = emails.size();

		System.out.println(String.format("Sent '%s' notifications:", numberOfSentNotifications));

		int counter = 1;
		for (SimpleMailMessage mailMessage : emails) {
			System.out.println(String.format("#%s Subject: '%s', Message: '%s'.",
					counter, mailMessage.getText(), mailMessage.getText()));
			counter++;
		}

		stopWatch.stop();
		System.out.println(stopWatch.prettyPrint());

		System.exit(0);

	}
}
