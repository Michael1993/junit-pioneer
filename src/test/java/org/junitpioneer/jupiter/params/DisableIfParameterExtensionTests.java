/*
 * Copyright 2016-2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.jupiter.params;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junitpioneer.testkit.assertion.PioneerAssert.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.testkit.ExecutionResults;
import org.junitpioneer.testkit.PioneerTestKit;

/**
 * Oscar Wilde: Requiescat is in the public domain
 */
@DisplayName("DisableIfParameterExtension")
class DisableIfParameterExtensionTests {

	@Nested
	@DisplayName("when configured correctly")
	class CorrectConfigurationTests {

		@Test
		@DisplayName("disables tests when parameter targeted by explicit index contains any value from the 'contains' array")
		void interceptContainsExplicitIndex() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(CorrectConfigTestCases.class, "interceptContainsExplicitIndex",
						String.class, String.class);

			assertThat(results).hasNumberOfAbortedTests(1).hasNumberOfSucceededTests(7).hasNumberOfFailedTests(2);
		}

		@Test
		@DisplayName("disables tests when parameter targeted by implicit index contains any value from the 'contains' array")
		void interceptContainsImplicitIndex() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(CorrectConfigTestCases.class, "interceptContainsImplicitIndex",
						String.class, String.class);

			assertThat(results).hasNumberOfAbortedTests(2).hasNumberOfSucceededTests(6).hasNumberOfFailedTests(2);
		}

		@Test
		@DisplayName("disables tests when any parameter contains any value from the 'contains' array")
		void interceptContainsAny() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(CorrectConfigTestCases.class, "interceptContainsAny",
						String.class, String.class);

			assertThat(results).hasNumberOfAbortedTests(4).hasNumberOfSucceededTests(6);
		}

		@Test
		@DisplayName("disables tests when all parameters contains any value from the 'contains' array")
		void interceptContainsAll() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(CorrectConfigTestCases.class, "interceptContainsAll",
						String.class, String.class);

			assertThat(results).hasNumberOfAbortedTests(1).hasNumberOfSucceededTests(9);
		}

		@Test
		@DisplayName("disables tests when parameter matches any regex from the 'matches' array")
		void interceptMatches() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(CorrectConfigTestCases.class, "interceptMatches", String.class,
						String.class);

			assertThat(results).hasNumberOfAbortedTests(5).hasNumberOfSucceededTests(2).hasNumberOfFailedTests(3);
		}

		@Test
		@DisplayName("disables tests when any parameter matches any regex from the 'matches' array")
		void interceptMatchesAny() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(CorrectConfigTestCases.class, "interceptMatchesAny",
						String.class, String.class);

			assertThat(results).hasNumberOfAbortedTests(3).hasNumberOfSucceededTests(7);
		}

		@Test
		@DisplayName("disables tests when all parameters match any regex from the 'matches' array")
		void interceptMatchesAll() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(CorrectConfigTestCases.class, "interceptMatchesAll",
						String.class, String.class);

			assertThat(results).hasNumberOfAbortedTests(1).hasNumberOfSucceededTests(2).hasNumberOfFailedTests(7);
		}

	}

	@Nested
	@DisplayName("when not configured correctly")
	class MisconfigurationTests {

		@Test
		@DisplayName("does not intercept non-parameterized tests")
		void simpleTest() {
			ExecutionResults results = PioneerTestKit.executeTestMethod(BadConfigTestCases.class, "simpleTest");

			assertThat(results).hasSingleSucceededTest();
		}

		@Test
		@DisplayName("throws an exception if DisableIfParameter index is too large")
		void overindex() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(BadConfigTestCases.class, "overindexed", String.class);

			assertThat(results)
					.hasNumberOfFailedTests(2)
					.withExceptionInstancesOf(ExtensionConfigurationException.class)
					.allMatch(s -> s.startsWith("Annotation has invalid index"));
		}

		@Test
		@DisplayName("throws an exception if both index and name is set on DisableIfParameter")
		void multipleTargets() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(BadConfigTestCases.class, "multipleTargets", String.class);

			assertThat(results)
					.hasSingleFailedTest()
					.withExceptionInstanceOf(ExtensionConfigurationException.class)
					.hasMessageContaining(
						"Using both name and index parameter targeting in a single @DisableIfParameter is not permitted.");
		}

		@Test
		@DisplayName("throws an exception if method has no parameters")
		void noParameters() {
			ExecutionResults results = PioneerTestKit.executeTestMethod(BadConfigTestCases.class, "noParameters");

			assertThat(results)
					.hasSingleFailedTest()
					.withExceptionInstanceOf(ExtensionConfigurationException.class)
					.hasMessageContainingAll("Can't disable based on arguments", "had no parameters");
		}

		@Test
		@DisplayName("throws an exception if both 'matches' and 'contains' is missing for DisableIfParameter")
		void missingValues() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(BadConfigTestCases.class, "missingInputs", String.class);

			assertThat(results)
					.hasNumberOfFailedTests(3)
					.withExceptionInstancesOf(ExtensionConfigurationException.class)
					.allMatch(("DisableIfParameter requires that either `contains` or `matches` is set.")::equals);
		}

		@Test
		@DisplayName("throws an exception if both 'matches' and 'contains' is missing for DisableIfAnyParameter")
		void missingValuesAny() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(BadConfigTestCases.class, "missingInputsAny", String.class);

			assertThat(results)
					.hasNumberOfFailedTests(3)
					.withExceptionInstancesOf(ExtensionConfigurationException.class)
					.allMatch(("DisableIfAnyParameter requires that either `contains` or `matches` is set.")::equals);
		}

		@Test
		@DisplayName("throws an exception if both 'matches' and 'contains' is missing for DisableIfAllParameters")
		void missingValuesAll() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(BadConfigTestCases.class, "missingInputsAll", String.class);

			assertThat(results)
					.hasNumberOfFailedTests(3)
					.withExceptionInstancesOf(ExtensionConfigurationException.class)
					.allMatch(("DisableIfAllParameters requires that either `contains` or `matches` is set.")::equals);
		}

		@Test
		@DisplayName("throws an exception if both 'matches' and 'contains' is set for DisableIfParameter")
		void bothValuesSet() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(BadConfigTestCases.class, "setBothParams", String.class);

			assertThat(results)
					.hasNumberOfFailedTests(3)
					.withExceptionInstancesOf(ExtensionConfigurationException.class)
					.allMatch(("DisableIfParameter requires that either `contains` or `matches` is set.")::equals);
		}

		@Test
		@DisplayName("throws an exception if both 'matches' and 'contains' is set for DisableIfAnyParameter")
		void bothValuesSetAny() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(BadConfigTestCases.class, "setBothParamsAny", String.class);

			assertThat(results)
					.hasNumberOfFailedTests(3)
					.withExceptionInstancesOf(ExtensionConfigurationException.class)
					.allMatch(("DisableIfAnyParameter requires that either `contains` or `matches` is set.")::equals);
		}

		@Test
		@DisplayName("throws an exception if both 'matches' and 'contains' is set for DisableIfAllParameters")
		void bothValuesSetAll() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(BadConfigTestCases.class, "setBothParamsAll", String.class);

			assertThat(results)
					.hasNumberOfFailedTests(3)
					.withExceptionInstancesOf(ExtensionConfigurationException.class)
					.allMatch(("DisableIfAllParameters requires that either `contains` or `matches` is set.")::equals);
		}

		@Test
		@DisplayName("throws an exception if it can not find the parameter based on the given name for DisableIfParameter")
		void missingParameter() {
			ExecutionResults results = PioneerTestKit
					.executeTestMethodWithParameterTypes(BadConfigTestCases.class, "badlyNamedParam", String.class);

			assertThat(results)
					.hasNumberOfFailedTests(3)
					.withExceptions()
					.allMatch(s -> s.contains("Could not resolve parameter"));
		}

	}

	static class CorrectConfigTestCases {

		@ParameterizedTest
		@DisableIfParameter(contains = "She")
		@MethodSource("requiescat")
		void interceptContainsImplicitIndex(String line, String line2) {
			// implicit index - disable if first parameter contains "She"
			assertThat(line2).doesNotContain("She");
		}

		@ParameterizedTest
		@DisableIfParameter(index = 1, contains = "her")
		@MethodSource("requiescat")
		void interceptContainsExplicitIndex(String line, String line2) {
			// explicit index - disable if second parameter contains "her"
			assertThat(line).doesNotContain("her");
		}

		@ParameterizedTest
		@DisableIfAnyParameter(contains = { "She" })
		@MethodSource("requiescat")
		void interceptContainsAny(String line, String line2) {
			assertThat(line).doesNotContain("She");
			assertThat(line2).doesNotContain("She");
		}

		@ParameterizedTest
		@DisableIfAllParameters(contains = { "She", "she" })
		@MethodSource("requiescat")
		void interceptContainsAll(String line, String line2) {
		}

		@ParameterizedTest
		@DisableIfParameter(matches = { ".*[tr].?" })
		@MethodSource("requiescat")
		void interceptMatches(String line, String line2) {
			assertThat(line).doesNotMatch(".*[tr].?");
			assertThat(line2).doesNotMatch(".*[tr].?");
		}

		@ParameterizedTest
		@DisableIfAnyParameter(matches = ".*here?.*")
		@MethodSource("requiescat")
		void interceptMatchesAny(String line, String line2) {
			assertThat(line).doesNotContainPattern("here?");
			assertThat(line2).doesNotContainPattern("here?");
		}

		@ParameterizedTest
		@DisableIfAllParameters(matches = ".*[Ss]he.*")
		@MethodSource("requiescat")
		void interceptMatchesAll(String line, String line2) {
			assertThat(line).doesNotContainPattern(".*[Ss]he.*");
			assertThat(line2).doesNotContainPattern(".*[Ss]he.*");
		}

		@ParameterizedTest
		@DisableIfParameter(index = 1, contains = "she")
		@DisableIfParameter(index = 0, contains = "her")
		@MethodSource("requiescat")
		void indexedIntercept(String line, String line2) {
			assertThat(line).doesNotContain("her");
			assertThat(line2).doesNotContain("she");
		}

		static Stream<Arguments> requiescat() {
			return Stream
					.of(Arguments.of("Tread lightly, she is near", "Under the snow,"),
						Arguments.of("Speak gently, she can hear", "The daisies grow."),
						Arguments.of("All her bright golden hair", "Tarnished with rust,"),
						Arguments.of("She that was young and fair", "Fallen to dust."),
						Arguments.of("Lily-like, white as snow,", "She hardly knew"),
						Arguments.of("She was a woman, so", "Sweetly she grew."),
						Arguments.of("Coffin-board, heavy stone,", "Lie on her breast,"),
						Arguments.of("I vex my heart alone,", "She is at rest."),
						Arguments.of("Peace, peace, she cannot hear", "Lyre or sonnet,"),
						Arguments.of("All my life's buried here,", "Heap earth upon it."));
		}

	}

	static class BadConfigTestCases {

		@Test
		@DisableIfAnyParameter(contains = "A")
		void simpleTest() {
		}

		@ParameterizedTest
		@DisableIfParameter(index = 3, contains = "overindexed")
		@ValueSource(strings = { "a", "b" })
		void overindexed(String s) {
		}

		@ParameterizedTest
		@DisableIfParameter(index = 0, name = "s", contains = "value")
		@ValueSource(strings = { "value" })
		void multipleTargets(String s) {
		}

		@ParameterizedTest
		@DisableIfParameter(index = 0, contains = "aaa")
		@ValueSource(strings = { "aaa" })
		void noParameters() {
		}

		@ParameterizedTest
		@DisableIfAnyParameter(contains = "a")
		@ValueSource(strings = { "aaa" })
		void noParametersAny() {
		}

		@ParameterizedTest
		@DisableIfAllParameters(contains = "a")
		@ValueSource(strings = { "aaa" })
		void noParametersAll() {
		}

		@ParameterizedTest
		@DisableIfParameter
		@ValueSource(strings = { "A", "B", "C" })
		void missingInputs(String value) {
		}

		@ParameterizedTest
		@DisableIfAnyParameter
		@ValueSource(strings = { "A", "B", "C" })
		void missingInputsAny(String value) {
		}

		@ParameterizedTest
		@DisableIfAllParameters
		@ValueSource(strings = { "A", "B", "C" })
		void missingInputsAll(String value) {
		}

		@ParameterizedTest
		@DisableIfParameter(name = "param", contains = { "A" })
		@ValueSource(strings = { "A", "B", "C" })
		void badlyNamedParam(String value) {
		}

		@ParameterizedTest
		@DisableIfParameter(contains = { "sonnet", "life" }, matches = "^.*(Peace, )\\1.*$")
		@ValueSource(strings = { "A", "B", "C" })
		void setBothParams(String value) {
		}

		@ParameterizedTest
		@DisableIfAnyParameter(contains = { "sonnet", "life" }, matches = "^.*(Peace, )\\1.*$")
		@ValueSource(strings = { "A", "B", "C" })
		void setBothParamsAny(String value) {
		}

		@ParameterizedTest
		@DisableIfAllParameters(contains = { "sonnet", "life" }, matches = "^.*(Peace, )\\1.*$")
		@ValueSource(strings = { "A", "B", "C" })
		void setBothParamsAll(String value) {
		}

	}

}
