package com.n26.json;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class JsonTransaction {

	/**
	 * transaction amount; a string of arbitrary length that is parsable as a
	 * BigDecimal
	 */
	private String amount;

	/**
	 * transaction time in the ISO 8601 format YYYY-MM-DDThh:mm:ss.sssZ in the UTC
	 * timezone (this is not the current timestamp)
	 */
	private String timestamp;

	public JsonTransaction(String amount, String timestamp) {
		this.amount = amount;
		this.timestamp = timestamp;
	}

	public String getAmount() {
		return amount;
	}

	public String getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
