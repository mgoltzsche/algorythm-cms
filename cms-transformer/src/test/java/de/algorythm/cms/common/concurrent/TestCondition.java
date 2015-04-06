package de.algorythm.cms.common.concurrent;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestCondition {

	private int fulfilledCalls;

	@Test
	public void fulfill_should_call_listeners_once() {
		Condition testee = new Condition();
		
		addConditionListeners(testee, 3);
		assertEquals("Condition listeners should not be called if not fulfilled", 0, fulfilledCalls);
		testee.fulfill();
		assertEquals("3 condition listeners should be called", 3, fulfilledCalls);
		
		try {
			testee.fulfill();
			throw new AssertionError("2nd fulfill() call should throw IllegalStateException");
		} catch(IllegalStateException e) {
		}
	}

	@Test
	public void addPrecondition_should_fulfill_if_all_preconditions_fulfilled() {
		final Condition pre1 = new Condition();
		final Condition pre2 = new Condition();
		final Condition testee = new Condition(pre1, pre2);
		
		addConditionListeners(testee, 3);
		
		assertEquals("Condition listeners should not be called if no precondition fulfilled", 0, fulfilledCalls);
		pre1.fulfill();
		assertEquals("Condition listeners should not be called if not all preconditions fulfilled", 0, fulfilledCalls);
		pre2.fulfill();
		assertEquals("3 condition listeners should be called", 3, fulfilledCalls);
	}
	
	private void addConditionListeners(final Condition condition, int n) {
		for (int i = 0; i < n; i++) {
			condition.addListener(new IConditionListener() {
				@Override
				public void fulfilled(Condition c) {
					assertEquals("Fulfilled condition", condition, c);
					fulfilledCalls++;
				}
			});
		}
	}
}
