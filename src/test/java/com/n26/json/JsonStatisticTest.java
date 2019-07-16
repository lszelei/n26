package com.n26.json;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.n26.model.Statistic;

public class JsonStatisticTest {

	@Test
	public void testConstructor() {
		BigDecimal min = new BigDecimal("1.01");
		BigDecimal max = new BigDecimal("100.01");
		BigDecimal sum = new BigDecimal("101.01");

		Statistic statistic = new Statistic();
		statistic.setMax(max);
		statistic.setMin(min);
		statistic.setSum(sum);
		statistic.increaseCount(2l);

		JsonStatistic jsonStatistic = new JsonStatistic(statistic);
		assertEquals("101.01", jsonStatistic.getSum());
		assertEquals("100.01", jsonStatistic.getMax());
		assertEquals("1.01", jsonStatistic.getMin());
		assertEquals("50.51", jsonStatistic.getAvg());
		assertEquals(2l, jsonStatistic.getCount());
	}

}
