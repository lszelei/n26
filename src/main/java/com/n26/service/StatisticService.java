package com.n26.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.n26.model.Statistic;
import com.n26.model.Transaction;

@Service
public class StatisticService {

	private static final int SECONDS = 60;

	private static final Logger LOGGER = LoggerFactory.getLogger(StatisticService.class);

	private final Object lock = new Object();

	private ConcurrentHashMap<Long, Statistic> statisticsMap;

	private SortedSet<Long> timestamps;

	public StatisticService() {
		statisticsMap = new ConcurrentHashMap<>(SECONDS);
		timestamps = new TreeSet<>();
	}

	public void addTransaction(Transaction transaction) {
		long timestamp = transaction.getTimestamp().toEpochSecond();

		synchronized (lock) {
			Statistic statistic = getStatistic(timestamp);
			addTransactionToStatistic(statistic, transaction);

			statisticsMap.put(timestamp, statistic);
		}
	}

	public Statistic calculateStatistics(Instant instant) {
		long start = instant.getEpochSecond();

		Statistic statistic = new Statistic();
		for (long key = start; key > start - SECONDS; key--) {
			Statistic value = getStatistic(key);
			combineStatistics(statistic, value);
		}

		return statistic;
	}

	public void clearTransactions() {
		statisticsMap.clear();
	}

	private Statistic getStatistic(Long timestamp) {
		timestamps.add(timestamp);

		return statisticsMap.computeIfAbsent(timestamp, statistic -> new Statistic());
	}

	private void addTransactionToStatistic(Statistic statistic, Transaction transaction) {
		statistic.increaseCount();

		statistic.setSum(statistic.getSum().add(transaction.getAmount()).setScale(2, RoundingMode.HALF_UP));
		setStatisticMin(statistic, transaction.getAmount());
		setStatisticMax(statistic, transaction.getAmount());
	}

	private void combineStatistics(Statistic statistic, Statistic value) {
		statistic.increaseCount(value.getCount());

		statistic.setSum(statistic.getSum().add(value.getSum()).setScale(2, RoundingMode.HALF_UP));
		setStatisticMin(statistic, value.getMin());
		setStatisticMax(statistic, value.getMax());
	}

	private void setStatisticMin(Statistic statistic, BigDecimal value) {
		if (statistic.getMin().equals(Statistic.ZERO)
				|| (!value.equals(Statistic.ZERO) && value.compareTo(statistic.getMin()) < 0)) {
			statistic.setMin(value.setScale(2, RoundingMode.HALF_UP));
		}
	}

	private void setStatisticMax(Statistic statistic, BigDecimal value) {
		if (statistic.getMax().equals(Statistic.ZERO)
				|| (!value.equals(Statistic.ZERO) && value.compareTo(statistic.getMax()) > 0)) {
			statistic.setMax(value.setScale(2, RoundingMode.HALF_UP));
		}
	}

	@Scheduled(fixedRate = 1000)
	public void cleanup() {
		Long first = Instant.now().minusSeconds(SECONDS).getEpochSecond();
		Set<Long> removeable = new HashSet<>(timestamps.headSet(first));

		for (Long key : removeable) {
			statisticsMap.remove(key);
			timestamps.remove(key);
		}
	}

}
