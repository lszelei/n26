package com.n26.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.n26.model.Statistic;
import com.n26.model.Transaction;
import com.n26.validator.TransactionValidator;

public class StatisticServiceTest {

	@Test
	public void testInitializedStatistic() {
		StatisticService service = new StatisticService();
		Statistic statistic = service.calculateStatistics(Instant.now());

		assertEquals(Statistic.ZERO, statistic.getSum());
		assertEquals(Statistic.ZERO, statistic.getMax());
		assertEquals(Statistic.ZERO, statistic.getMin());
		assertEquals(Statistic.ZERO, statistic.getAvg());
		assertEquals(0l, statistic.getCount());
	}

	@Test
	public void testAddTransactionOnlyOneFromNow() {
		Instant now = Instant.now();
		StatisticService service = new StatisticService();
		Statistic statistic = service.calculateStatistics(now);

		assertEquals(Statistic.ZERO, statistic.getSum());
		assertEquals(Statistic.ZERO, statistic.getMax());
		assertEquals(Statistic.ZERO, statistic.getMin());
		assertEquals(Statistic.ZERO, statistic.getAvg());
		assertEquals(0l, statistic.getCount());

		ZonedDateTime dateTime = now.atZone(ZoneId.of(TransactionValidator.ZONE_ID));

		BigDecimal bigDecimal = new BigDecimal("1.01");
		Transaction transaction = new Transaction(bigDecimal, dateTime);
		service.addTransaction(transaction);

		for (long i = 0; i < 60; i++) {
			statistic = service.calculateStatistics(now.plusSeconds(i));
			assertEquals(bigDecimal, statistic.getSum());
			assertEquals(bigDecimal, statistic.getMax());
			assertEquals(bigDecimal, statistic.getMin());
			assertEquals(bigDecimal, statistic.getAvg());
			assertEquals(1l, statistic.getCount());
		}
	}

	@Test
	public void testAddTransactionOnlyOneFromLast30() {
		Instant now = Instant.now();
		StatisticService service = new StatisticService();
		Statistic statistic = service.calculateStatistics(now);

		assertEquals(Statistic.ZERO, statistic.getSum());
		assertEquals(Statistic.ZERO, statistic.getMax());
		assertEquals(Statistic.ZERO, statistic.getMin());
		assertEquals(Statistic.ZERO, statistic.getAvg());
		assertEquals(0l, statistic.getCount());

		ZonedDateTime dateTime = now.minusSeconds(30).atZone(ZoneId.of(TransactionValidator.ZONE_ID));

		BigDecimal bigDecimal = new BigDecimal("1.01");
		Transaction transaction = new Transaction(bigDecimal, dateTime);
		service.addTransaction(transaction);

		for (long i = 0; i < 30; i++) {
			statistic = service.calculateStatistics(now.plusSeconds(i));
			assertEquals(bigDecimal, statistic.getSum());
			assertEquals(bigDecimal, statistic.getMax());
			assertEquals(bigDecimal, statistic.getMin());
			assertEquals(bigDecimal, statistic.getAvg());
			assertEquals(1l, statistic.getCount());
		}

		for (long i = 30; i < 60; i++) {
			statistic = service.calculateStatistics(now.plusSeconds(i));
			assertEquals(Statistic.ZERO, statistic.getSum());
			assertEquals(Statistic.ZERO, statistic.getMax());
			assertEquals(Statistic.ZERO, statistic.getMin());
			assertEquals(Statistic.ZERO, statistic.getAvg());
			assertEquals(0l, statistic.getCount());
		}
	}

	@Test
	public void testAddTransactionAndClear() {
		Instant now = Instant.now();
		StatisticService service = new StatisticService();
		Statistic statistic = service.calculateStatistics(now);

		assertEquals(Statistic.ZERO, statistic.getSum());
		assertEquals(Statistic.ZERO, statistic.getMax());
		assertEquals(Statistic.ZERO, statistic.getMin());
		assertEquals(Statistic.ZERO, statistic.getAvg());
		assertEquals(0l, statistic.getCount());

		ZonedDateTime dateTime = now.atZone(ZoneId.of(TransactionValidator.ZONE_ID));

		BigDecimal bigDecimal = new BigDecimal("1.01");
		Transaction transaction = new Transaction(bigDecimal, dateTime);
		service.addTransaction(transaction);

		for (long i = 0; i < 60; i++) {
			statistic = service.calculateStatistics(now.plusSeconds(i));
			assertEquals(bigDecimal, statistic.getSum());
			assertEquals(bigDecimal, statistic.getMax());
			assertEquals(bigDecimal, statistic.getMin());
			assertEquals(bigDecimal, statistic.getAvg());
			assertEquals(1l, statistic.getCount());
		}

		service.clearTransactions();

		for (long i = 0; i < 60; i++) {
			statistic = service.calculateStatistics(now.plusSeconds(i));
			assertEquals(Statistic.ZERO, statistic.getSum());
			assertEquals(Statistic.ZERO, statistic.getMax());
			assertEquals(Statistic.ZERO, statistic.getMin());
			assertEquals(Statistic.ZERO, statistic.getAvg());
			assertEquals(0l, statistic.getCount());
		}
	}

	@Test
	public void testAdd60Transactions() {
		Random random = new Random();
		Instant now = Instant.now();
		StatisticService service = new StatisticService();

		BigDecimal min = null;
		BigDecimal max = null;
		BigDecimal sum = BigDecimal.ZERO;

		for (int i = 0; i < 60; i++) {
			ZonedDateTime dateTime = now.minusSeconds(i).atZone(ZoneId.of(TransactionValidator.ZONE_ID));
			BigDecimal value = getRandomBigDecimal(random, 1.0d, 1000.0d);
			if (min == null || min.compareTo(value) > 0) {
				min = value;
			}
			if (max == null || max.compareTo(value) < 0) {
				max = value;
			}
			sum = sum.add(value);

			Transaction transaction = new Transaction(value, dateTime);
			service.addTransaction(transaction);
		}

		Statistic statistic = service.calculateStatistics(now);
		assertEquals(sum, statistic.getSum());
		assertEquals(max, statistic.getMax());
		assertEquals(min, statistic.getMin());
		assertEquals(sum.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP), statistic.getAvg());
		assertEquals(60l, statistic.getCount());
	}

	@Test
	public void testAddTransactionsParallel() throws InterruptedException {
		Random random = new Random();

		Instant now = Instant.now();
		StatisticService service = new StatisticService();

		int count = 100000;
		ConcurrentHashMap<Integer, BigDecimal> testMap = new ConcurrentHashMap<>();

		ExecutorService executorService = Executors.newFixedThreadPool(10);
		for (int i = 0; i < count; i++) {
			final Integer key = i;
			executorService.execute(() -> {
				ZonedDateTime dateTime = getRandomZonedDateTime(random, now, 60);
				BigDecimal value = getRandomBigDecimal(random, 1.0d, 1000.0d);
				testMap.putIfAbsent(key, value);

				Transaction transaction = new Transaction(value, dateTime);
				service.addTransaction(transaction);
			});
		}

		executorService.shutdown();
		executorService.awaitTermination(5, TimeUnit.SECONDS);

		BigDecimal min = null;
		BigDecimal max = null;
		BigDecimal sum = BigDecimal.ZERO;
		for (BigDecimal value : testMap.values()) {
			if (min == null || min.compareTo(value) > 0) {
				min = value;
			}
			if (max == null || max.compareTo(value) < 0) {
				max = value;
			}
			sum = sum.add(value);
		}

		Statistic statistic = service.calculateStatistics(now);
		assertEquals(sum, statistic.getSum());
		assertEquals(max, statistic.getMax());
		assertEquals(min, statistic.getMin());
		assertEquals(sum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP), statistic.getAvg());
		assertEquals(count, statistic.getCount());
	}

	private ZonedDateTime getRandomZonedDateTime(Random random, Instant instant, int limit) {
		int generatedInt = random.nextInt(limit);
		return instant.minusSeconds(generatedInt).atZone(ZoneId.of(TransactionValidator.ZONE_ID));
	}

	private BigDecimal getRandomBigDecimal(Random random, double leftLimit, double rightLimit) {
		double generatedDouble = leftLimit + random.nextDouble() * (rightLimit - leftLimit);
		return new BigDecimal(generatedDouble).setScale(2, RoundingMode.HALF_UP);
	}

}
