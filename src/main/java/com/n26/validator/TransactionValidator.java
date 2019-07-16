package com.n26.validator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.n26.exception.InvalidTransactionException;
import com.n26.exception.TransactionInTheFutureException;
import com.n26.exception.TransactionInThePastException;
import com.n26.json.JsonTransaction;
import com.n26.model.Transaction;

@Service
public class TransactionValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionValidator.class);

	public static final long TRANSACTION_TIME_THRESHOLD = 60000l;

	public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String ZONE_ID = "UTC";

	public static final DateTimeFormatter ISO_MILLIS;
	static {
		ISO_MILLIS = DateTimeFormatter.ofPattern(DATETIME_FORMAT).withZone(ZoneId.of(ZONE_ID));
	}

	public Transaction validateAndConvert(JsonTransaction jsonTransaction, Instant now)
			throws InvalidTransactionException, TransactionInThePastException, TransactionInTheFutureException {
		if (jsonTransaction == null) {
			LOGGER.error("Transaction is null!");
			throw new InvalidTransactionException();
		}

		if (StringUtils.isAnyBlank(jsonTransaction.getAmount(), jsonTransaction.getTimestamp())) {
			LOGGER.error("Transaction is incomplete: {}", jsonTransaction);
			throw new InvalidTransactionException();
		}

		BigDecimal amount;
		try {
			amount = new BigDecimal(jsonTransaction.getAmount());
		} catch (NumberFormatException e) {
			LOGGER.error("Cannot parse amount: {}", jsonTransaction.getAmount());
			throw new InvalidTransactionException(e);
		}

		ZonedDateTime timestamp;
		try {
			timestamp = ZonedDateTime.parse(jsonTransaction.getTimestamp(), ISO_MILLIS);
		} catch (DateTimeParseException e) {
			LOGGER.error("Cannot parse timestamp: {}", jsonTransaction.getTimestamp());
			throw new InvalidTransactionException(e);
		}

		Instant transactionTime = timestamp.toInstant();
		if (transactionTime.isBefore(now.minusMillis(TRANSACTION_TIME_THRESHOLD))) {
			LOGGER.warn("Transaction is in the past: {}", timestamp);
			throw new TransactionInThePastException();
		}
		if (transactionTime.isAfter(now)) {
			LOGGER.warn("Transaction is in the future: {}", timestamp);
			throw new TransactionInTheFutureException();
		}

		return new Transaction(amount, timestamp);
	}

}
