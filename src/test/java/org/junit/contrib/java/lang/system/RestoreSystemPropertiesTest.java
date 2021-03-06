package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;
import static java.lang.System.getProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.Executor.executeTestWithRule;

import org.junit.After;
import org.junit.Test;
import org.junit.rules.TestRule;

public class RestoreSystemPropertiesTest {
	//ensure that every test uses the same property, because this one is restored after the test
	private static final String PROPERTY_KEY = "dummy property";

	private final String propertiesOriginalValue = getProperty(PROPERTY_KEY);
	private final TestRule rule = new RestoreSystemProperties();

	@After
	public void restoreProperty() {
		if (propertiesOriginalValue == null)
			clearProperty(PROPERTY_KEY);
		else
			System.setProperty(PROPERTY_KEY, propertiesOriginalValue);
	}

	@Test
	public void after_test_properties_have_the_same_values_as_before() {
		System.setProperty(PROPERTY_KEY, "dummy value");
		executeTestWithRule(
			Statements.setProperty(PROPERTY_KEY, "another value"),
			rule);
		assertThat(getProperty(PROPERTY_KEY)).isEqualTo("dummy value");
	}

	@Test
	public void property_that_does_not_exist_before_the_test_does_not_exist_after_the_test() {
		clearProperty(PROPERTY_KEY);
		executeTestWithRule(
			Statements.setProperty(PROPERTY_KEY, "another value"),
			rule);
		assertThat(getProperty(PROPERTY_KEY)).isNull();
	}

	@Test
	public void property_value_is_unchanged_at_start_of_test() {
		System.setProperty(PROPERTY_KEY, "dummy value");
		TestThatCapturesProperties test = new TestThatCapturesProperties();
		executeTestWithRule(test, rule);
		assertThat(test.propertiesAtStart)
			.containsEntry(PROPERTY_KEY, "dummy value");
	}
}
