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
package org.springframework.batch.integration.samples.payments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * @author Marius Bogoevici
 * @author Gunnar Hillert
 */
public class JobRestart {

	private static final Logger LOGGER =
			LoggerFactory.getLogger(JobRestart.class);

	@Autowired
	private JobOperator jobOperator;

	@ServiceActivator
	public void restartIfPossible(JobExecution execution)
			throws JobRestartException {

		LOGGER.info("Restarting job execution {}", execution.getId());
		jobOperator.restart(execution);
	}
}
