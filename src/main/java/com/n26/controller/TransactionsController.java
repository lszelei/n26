package com.n26.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.n26.exception.InvalidTransactionException;
import com.n26.exception.TransactionInTheFutureException;
import com.n26.exception.TransactionInThePastException;
import com.n26.json.JsonTransaction;
import com.n26.model.Transaction;
import com.n26.service.StatisticService;
import com.n26.validator.TransactionValidator;

@RestController
public class TransactionsController {

	@Autowired
	private TransactionValidator validator;

	@Autowired
	private StatisticService statisticService;

	@PostMapping("/transactions")
	public ResponseEntity<String> newTransaction(@RequestBody JsonTransaction jsonTransaction) {
		Instant now = Instant.now();
		try {
			Transaction transaction = validator.validateAndConvert(jsonTransaction, now);
			statisticService.addTransaction(transaction);

			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (InvalidTransactionException | TransactionInTheFutureException e) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (TransactionInThePastException e) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT);
		}
	}

	@DeleteMapping("/transactions")
	public ResponseEntity<String> clearTransactions() {
		statisticService.clearTransactions();

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
