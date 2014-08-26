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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.integration.samples.payments.model.Notification;
import org.springframework.batch.integration.samples.payments.model.Payment;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

/**
 * @author Marius Bogoevici
 * @author Gunnar Hillert
 */
public class PaymentChunkListener extends ItemListenerSupport<Payment, Payment> {

	private static final Log logger = LogFactory.getLog(PaymentChunkListener.class);

	@Autowired
	@Qualifier("chunkExecutions")
	MessageChannel chunkNotificationsChannel;

	@Override
	public void onReadError(Exception ex) {
		if (ex instanceof FlatFileParseException) {
			FlatFileParseException ffpe = (FlatFileParseException) ex;
			logger.error(String.format("Error reading data on line '%s' - data: '%s'", ffpe.getLineNumber(), ffpe.getInput()));
		}
		chunkNotificationsChannel.send(MessageBuilder.withPayload(new Notification(ex.getMessage(),true)).build());
	}

	@Override
	public void onWriteError(Exception ex, List<? extends Payment> item) {
	}
}

