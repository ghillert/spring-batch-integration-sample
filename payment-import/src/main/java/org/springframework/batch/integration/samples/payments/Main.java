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

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.integration.samples.payments.config.CommonConfig;
import org.springframework.batch.integration.samples.payments.util.SpringIntegrationUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.messaging.Message;

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

		System.out.println("\n========================================================="
						+ "\n    Welcome to the Spring Batch Integration              "
						+ "\n          Payments Import Sample                         "
						+ "\n                                                         "
						+ "\n    For more information please visit:                   "
						+ "\n    http://www.spring.io/spring-batch                    "
						+ "\n                                                         "
						+ "\n=========================================================" );

		final ConfigurableApplicationContext context = SpringApplication.run(CommonConfig.class);

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

		System.exit(0);

	}
}
