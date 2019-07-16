package com.n26.json;

import com.n26.model.Statistic;

public class JsonStatistic {

	private String sum;

	private String avg;

	private String min;

	private String max;

	private long count;

	public JsonStatistic(Statistic statistic) {
		sum = statistic.getSum().toString();
		avg = statistic.getAvg().toString();
		min = statistic.getMin().toString();
		max = statistic.getMax().toString();
		count = statistic.getCount();
	}

	public String getSum() {
		return sum;
	}

	public String getAvg() {
		return avg;
	}

	public String getMin() {
		return min;
	}

	public String getMax() {
		return max;
	}

	public long getCount() {
		return count;
	}

}
