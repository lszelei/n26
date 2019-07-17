package com.n26.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Statistic {

	public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);

	private BigDecimal sum = ZERO;

	private BigDecimal max = ZERO;

	private BigDecimal min = ZERO;

	private long count = 0l;

	public BigDecimal getSum() {
		return sum;
	}

	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	public BigDecimal getMax() {
		return max;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}

	public BigDecimal getMin() {
		return min;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

	public long getCount() {
		return count;
	}

	public void increaseCount() {
		count++;
	}

	public void increaseCount(long value) {
		count += value;
	}

	public BigDecimal getAvg() {
		if (getSum().equals(ZERO)) {
			return ZERO;
		} else {
			return getSum().divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
