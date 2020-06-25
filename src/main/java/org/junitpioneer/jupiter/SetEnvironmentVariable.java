/*
 * Copyright 2015-2020 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * {@code @SetEnvironmentVariable} is a JUnit Jupiter extension to set the value of a
 * environment variable for a test execution.
 *
 * <p>The key and value of the environment variable to be set must be specified via
 * {@link #key()} and {@link #value()}. After the annotated method has been
 * executed, the initial default value is restored.</p>
 *
 * <p>{@code SetEnvironmentVariable} is repeatable and can be used on the method and on
 * the class level. If a class is annotated, the configured variable will be set
 * for all tests inside that class. Any method level configurations will
 * override the class level configurations.</p>
 *
 * <p>WARNING: Java considers environment variables to be immutable, so this extension
 * uses reflection to change them. This requires that the {@link SecurityManager}
 * allows modifications and can potentially break on different operating systems and
 * Java versions. Be aware that this is a fragile solution and consider finding a
 * better one for your specific situation. If you're running on Java 9 or later, you
 * may have to add {@code --add-opens=java.base/java.util=ALL-UNNAMED} to your test
 * execution to prevent warnings or even errors.</p>
 *
 * <p>For more details and examples, see
 * <a href="https://junit-pioneer.org/docs/environment-variables/" target="_top">the documentation on <code>@ClearEnvironmentVariable and @SetEnvironmentVariable</code></a>.
 * </p>
 *
 * @since 0.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Repeatable(SetEnvironmentVariable.SetEnvironmentVariables.class)
@ExtendWith(EnvironmentVariableExtension.class)
public @interface SetEnvironmentVariable {

	/**
	 * The key of the system property to be set.
	 */
	String key();

	/**
	 * The value of the system property to be set.
	 */
	String value();

	/**
	 * Containing annotation of repeatable {@code @SetEnvironmentVariable}.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	@ExtendWith(EnvironmentVariableExtension.class)
	@interface SetEnvironmentVariables {

		SetEnvironmentVariable[] value();

	}

}
