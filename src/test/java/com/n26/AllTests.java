package com.n26;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.n26.controller.StatisticsControllerTest;
import com.n26.controller.TransactionsControllerTest;
import com.n26.json.JsonStatisticTest;
import com.n26.model.StatisticTest;
import com.n26.model.TransactionTest;
import com.n26.service.StatisticServiceTest;
import com.n26.validator.TransactionValidatorTest;

@RunWith(Suite.class)
@SuiteClasses({ JsonStatisticTest.class, StatisticTest.class, StatisticServiceTest.class,
		TransactionValidatorTest.class, TransactionTest.class, StatisticsControllerTest.class,
		TransactionsControllerTest.class })
public class AllTests {

}
