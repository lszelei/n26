package com.n26.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n26.json.JsonStatistic;
import com.n26.model.Statistic;
import com.n26.service.StatisticService;

@RestController
public class StatisticsController {

	@Autowired
	private StatisticService statisticService;

	@GetMapping("/statistics")
	public ResponseEntity<JsonStatistic> sendStatistics() {
		Instant now = Instant.now();
		Statistic statistic = statisticService.calculateStatistics(now);

		return new ResponseEntity<>(new JsonStatistic(statistic), HttpStatus.OK);
	}

}
