package com.n26.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class StatisticTest {

	@Test
	public void testInitialized() {
		Statistic statistic = new Statistic();
		assertEquals(Statistic.ZERO, statistic.getSum());
		assertEquals(Statistic.ZERO, statistic.getMax());
		assertEquals(Statistic.ZERO, statistic.getMin());
		assertEquals(Statistic.ZERO, statistic.getAvg());
		assertEquals(0l, statistic.getCount());
	}

	@Test
	public void testMethods() {
		BigDecimal min = new BigDecimal("1.01");
		BigDecimal max = new BigDecimal("100.01");
		BigDecimal sum = new BigDecimal("101.01");
		long count = 2l;
		BigDecimal avg = new BigDecimal("50.51");

		Statistic statistic = new Statistic();
		statistic.setMax(max);
		statistic.setMin(min);
		statistic.setSum(sum);
		statistic.increaseCount(count);

		assertEquals(sum, statistic.getSum());
		assertEquals(max, statistic.getMax());
		assertEquals(min, statistic.getMin());
		assertEquals(avg, statistic.getAvg());
		assertEquals(count, statistic.getCount());
	}

}
