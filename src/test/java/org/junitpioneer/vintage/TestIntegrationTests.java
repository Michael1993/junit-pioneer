/*
 * Copyright 2015-2020 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.vintage;

import static java.lang.String.format;
import static org.junitpioneer.testkit.assertion.PioneerAssert.EntryPoint.assertThat;
import static org.junitpioneer.vintage.ExpectedExceptionExtension.EXPECTED_EXCEPTION_WAS_NOT_THROWN;

import java.nio.file.InvalidPathException;

import org.junitpioneer.testkit.ExecutionResults;
import org.junitpioneer.testkit.PioneerTestKit;

/**
 * Tests the vintage {@link Test @Test} annotation by running the entire test engine.
 */
class TestIntegrationTests {

	@org.junit.jupiter.api.Test
	void test_successfulTest_passes() throws Exception {
		ExecutionResults results = PioneerTestKit.executeTestMethod(TestTestCase.class, "test_successfulTest");

		assertThat(results).hasSingleTest().thatStarted().andAllOfThemSucceeded();
	}

	@org.junit.jupiter.api.Test
	void test_exceptionThrown_fails() throws Exception {
		ExecutionResults results = PioneerTestKit.executeTestMethod(TestTestCase.class, "test_exceptionThrown");

		assertThat(results).hasSingleTest().thatStarted().andAllOfThemFailed();
	}

	// expected exception

	@org.junit.jupiter.api.Test
	void testWithExpectedException_successfulTest_fails() {
		ExecutionResults results = PioneerTestKit
				.executeTestMethod(TestTestCase.class, "testWithExpectedException_successfulTest");

		assertThat(results)
				.hasSingleTest()
				.thatStarted()
				.andAllOfThemFailed()
				.withException()
				.hasMessageContainingAll(format(EXPECTED_EXCEPTION_WAS_NOT_THROWN, IllegalArgumentException.class));
	}

	@org.junit.jupiter.api.Test
	void testWithExpectedException_exceptionThrownOfRightType_passes() {
		ExecutionResults results = PioneerTestKit
				.executeTestMethod(TestTestCase.class, "testWithExpectedException_exceptionThrownOfRightType");

		assertThat(results).hasSingleTest().thatStarted().andAllOfThemSucceeded();
	}

	@org.junit.jupiter.api.Test
	void testWithExpectedException_exceptionThrownOfSubtype_passes() {
		ExecutionResults results = PioneerTestKit
				.executeTestMethod(TestTestCase.class, "testWithExpectedException_exceptionThrownOfSubtype");

		assertThat(results).hasSingleTest().thatStarted().andAllOfThemSucceeded();
	}

	@org.junit.jupiter.api.Test
	void testWithExpectedException_exceptionThrownOfSupertype_fails() {
		ExecutionResults results = PioneerTestKit
				.executeTestMethod(TestTestCase.class, "testWithExpectedException_exceptionThrownOfSupertype");

		assertThat(results).hasSingleTest().thatStarted().andAllOfThemFailed().withException(RuntimeException.class);
	}

	// timeout

	@org.junit.jupiter.api.Test
	void testWithTimeout_belowTimeout_passes() {
		ExecutionResults results = PioneerTestKit.executeTestMethod(TestTestCase.class, "testWithTimeout_belowTimeout");

		assertThat(results).hasSingleTest().thatStarted().andAllOfThemSucceeded();
	}

	@org.junit.jupiter.api.Test
	void testWithTimeout_exceedsTimeout_fails() throws Exception {
		ExecutionResults results = PioneerTestKit
				.executeTestMethod(TestTestCase.class, "testWithTimeout_exceedsTimeout");
		String expectedMessage = format(TimeoutExtension.TEST_RAN_TOO_LONG, "testWithTimeout_exceedsTimeout()", 1, 10);
		// the message contains the actual run time, which is unpredictable, so it has to be cut off for the assertion
		String expectedKnownPrefix = expectedMessage.substring(0, expectedMessage.length() - 6);

		assertThat(results)
				.hasSingleTest()
				.thatStarted()
				.andAllOfThemFailed()
				.withException()
				.hasMessageContainingAll(expectedKnownPrefix);
	}

	// TEST CASES -------------------------------------------------------------------

	static class TestTestCase {

		@Test
		void test_successfulTest() {
			assertThat(true).isTrue();
		}

		@Test
		void test_exceptionThrown() {
			throw new IllegalArgumentException();
		}

		// expected exception

		@Test(expected = IllegalArgumentException.class)
		void testWithExpectedException_successfulTest() {
			assertThat(true).isTrue();
		}

		@Test(expected = IllegalArgumentException.class)
		void testWithExpectedException_exceptionThrownOfRightType() {
			throw new IllegalArgumentException();
		}

		@Test(expected = IllegalArgumentException.class)
		void testWithExpectedException_exceptionThrownOfSubtype() {
			throw new InvalidPathException("", "");
		}

		@Test(expected = IllegalArgumentException.class)
		void testWithExpectedException_exceptionThrownOfSupertype() {
			throw new RuntimeException();
		}

		// timeout

		@Test(timeout = 10_000)
		void testWithTimeout_belowTimeout() {
			assertThat(true).isTrue();
		}

		@Test(timeout = 1)
		void testWithTimeout_exceedsTimeout() throws Exception {
			Thread.sleep(10); //NOSONAR wanted behaviour for testing
		}

	}

}
