package com.n26.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.ThrowableAssertAlternative;
import org.junit.Test;

import com.n26.exception.InvalidTransactionException;
import com.n26.exception.TransactionInTheFutureException;
import com.n26.exception.TransactionInThePastException;
import com.n26.json.JsonTransaction;
import com.n26.model.Transaction;

public class TransactionValidatorTest {

	@Test()
	public void testNullTransaction() {
		TransactionValidator validator = new TransactionValidator();
		assertThatExceptionOfType(InvalidTransactionException.class)
				.isThrownBy(() -> validator.validateAndConvert(null, Instant.now()));
	}

	@Test()
	public void testBlankTransactionAmount() {
		TransactionValidator validator = new TransactionValidator();
		assertThatExceptionOfType(InvalidTransactionException.class).isThrownBy(() -> validator
				.validateAndConvert(new JsonTransaction(StringUtils.EMPTY, "2018-07-17T09:59:51.312Z"), Instant.now()));
	}

	@Test()
	public void testBlankTransactionTimestamp() {
		TransactionValidator validator = new TransactionValidator();
		assertThatExceptionOfType(InvalidTransactionException.class).isThrownBy(
				() -> validator.validateAndConvert(new JsonTransaction("12.3343", StringUtils.EMPTY), Instant.now()));
	}

	@Test()
	public void testUnparsableTransactionAmount() {
		TransactionValidator validator = new TransactionValidator();
		ThrowableAssertAlternative<InvalidTransactionException> alternative = assertThatExceptionOfType(
				InvalidTransactionException.class).isThrownBy(
						() -> validator.validateAndConvert(new JsonTransaction("12.3s43", "2018-07-17T09:59:51.312Z"),
								Instant.now()));
		alternative.withCauseInstanceOf(NumberFormatException.class);
	}

	@Test()
	public void testUnparsableTransactionTimestamp() {
		TransactionValidator validator = new TransactionValidator();
		ThrowableAssertAlternative<InvalidTransactionException> alternative = assertThatExceptionOfType(
				InvalidTransactionException.class)
						.isThrownBy(() -> validator.validateAndConvert(
								new JsonTransaction("12.3343", "'2011-12-03T10:15:30+01:00[Europe/Paris]'"),
								Instant.now()));
		alternative.withCauseInstanceOf(DateTimeParseException.class);
	}

	@Test()
	public void testTransactionInThePast() {
		TransactionValidator validator = new TransactionValidator();
		assertThatExceptionOfType(TransactionInThePastException.class).isThrownBy(() -> validator
				.validateAndConvert(new JsonTransaction("12.3343", "2018-07-17T09:59:51.312Z"), Instant.now()));
	}

	@Test()
	public void testTransactionInTheFuture() {
		TransactionValidator validator = new TransactionValidator();
		assertThatExceptionOfType(TransactionInTheFutureException.class).isThrownBy(() -> validator
				.validateAndConvert(new JsonTransaction("12.3343", "2118-07-17T09:59:51.312Z"), Instant.now()));
	}

	@Test()
	public void testValidTransaction() {
		TransactionValidator validator = new TransactionValidator();
		Instant instant = Instant.now().minusMillis(10000);
		String time = TransactionValidator.ISO_MILLIS.format(instant);
		BigDecimal bigDecimal = new BigDecimal("12.3343");

		assertThatCode(() -> {
			Transaction transaction = validator.validateAndConvert(new JsonTransaction(bigDecimal.toString(), time),
					Instant.now());
			assertEquals(bigDecimal, transaction.getAmount());
			assertEquals(instant.toEpochMilli(), transaction.getTimestamp().toInstant().toEpochMilli());
		}).doesNotThrowAnyException();
	}
}
