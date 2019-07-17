package com.n26.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.n26.exception.InvalidTransactionException;
import com.n26.exception.TransactionInTheFutureException;
import com.n26.exception.TransactionInThePastException;
import com.n26.model.Transaction;
import com.n26.service.StatisticService;
import com.n26.validator.TransactionValidator;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionsController.class)
public class TransactionsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TransactionValidator validator;

	@MockBean
	private StatisticService service;

	@Test
	public void testClearTransactions() throws Exception {
		mockMvc.perform(delete("/transactions")).andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}

	@Test
	public void testInvalidTransactionExceptionWhenNewTransaction() throws Exception {
		final String json = "{\"amount\": \"12.3343\",\"timestamp\": \"2018-07-17T09:59:51.312Z\"}";
		when(validator.validateAndConvert(Mockito.any(), Mockito.any())).thenThrow(InvalidTransactionException.class);
		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
	}

	@Test
	public void testTransactionInTheFutureExceptionWhenNewTransaction() throws Exception {
		final String json = "{\"amount\": \"12.3343\",\"timestamp\": \"2018-07-17T09:59:51.312Z\"}";
		when(validator.validateAndConvert(Mockito.any(), Mockito.any()))
				.thenThrow(TransactionInTheFutureException.class);
		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
	}

	@Test
	public void testTransactionInThePastExceptionWhenNewTransaction() throws Exception {
		final String json = "{\"amount\": \"12.3343\",\"timestamp\": \"2018-07-17T09:59:51.312Z\"}";
		when(validator.validateAndConvert(Mockito.any(), Mockito.any())).thenThrow(TransactionInThePastException.class);
		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}

	@Test
	public void testValidTransactionWhenNewTransaction() throws Exception {
		final String json = "{\"amount\": \"12.3343\",\"timestamp\": \"2018-07-17T09:59:51.312Z\"}";
		when(validator.validateAndConvert(Mockito.any(), Mockito.any())).thenReturn(new Transaction(
				new BigDecimal("1000.00"), Instant.now().atZone(ZoneId.of(TransactionValidator.ZONE_ID))));
		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().is(HttpStatus.CREATED.value()));
	}
}
