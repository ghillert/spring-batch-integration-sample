package org.springframework.batch.integration.samples.payments;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.batch.integration.samples.payments.model.Payment;
import org.springframework.batch.item.ItemProcessor;

public class PaymentProcessor implements ItemProcessor<Payment, Payment> {

	private static final Logger LOGGER = Logger.getLogger(PaymentProcessor.class);

	@Override
	public Payment process(Payment payment) throws Exception {

		LOGGER.info("Processing " + payment.getDate());
		payment.setAmount(BigDecimal.valueOf(9999));
		Thread.sleep(2000);

		return payment;
	}

}

