package com.n26.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import com.n26.validator.TransactionValidator;

public class TransactionTest {

	@Test
	public void testConstructor() {
		Instant now = Instant.now();
		ZonedDateTime zdt = now.atZone(ZoneId.of(TransactionValidator.ZONE_ID));

		Transaction transaction = new Transaction(BigDecimal.ZERO, zdt);
		assertEquals(BigDecimal.ZERO, transaction.getAmount());
		assertEquals(zdt, transaction.getTimestamp());
	}
}
