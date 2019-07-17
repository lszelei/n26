package com.n26.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.n26.controller.StatisticsController;
import com.n26.model.Statistic;
import com.n26.service.StatisticService;

@RunWith(SpringRunner.class)
@WebMvcTest(StatisticsController.class)
public class StatisticsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StatisticService service;

	@Test
	public void testGetStatistics() throws Exception {
		Statistic statistic = new Statistic();
		statistic.setMax(new BigDecimal("100.02"));
		statistic.setMin(new BigDecimal("1.01"));
		statistic.setSum(new BigDecimal("101.03"));
		statistic.increaseCount(2);

		when(service.calculateStatistics(Mockito.any())).thenReturn(statistic);
		mockMvc.perform(get("/statistics").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("sum", is(statistic.getSum().toString())))
				.andExpect(jsonPath("max", is(statistic.getMax().toString())))
				.andExpect(jsonPath("min", is(statistic.getMin().toString())))
				.andExpect(jsonPath("avg", is(statistic.getAvg().toString()))).andExpect(jsonPath("count", is(2)));
	}

}
