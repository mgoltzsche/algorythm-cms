package de.algorythm.cms.common.scheduling;

import org.junit.Test;

import de.algorythm.cms.common.scheduling.impl.BlockingRingBuffer;
import de.algorythm.cms.common.scheduling.impl.BlockingRingBuffer.INode;
import static org.junit.Assert.*;

public class TestRingBuffer {

	private INode<String> nextNode;
	private Throwable error;
	private boolean finished;
	
	@Test
	public void testRingBuffer() throws Throwable {
		final BlockingRingBuffer<String> testee = new BlockingRingBuffer<String>();
		
		assertEquals("initial size", 0, testee.size());
		
		Thread testThread = new Thread() {
			@Override
			public void run() {
				try {
					nextNode = testee.next();
					finished = true;
				} catch(Throwable e) {
					error = e;
				}
			}
		};
		
		testThread.start();
		Thread.sleep(50);
		assertFinished(false);
		testee.add("1st value");
		Thread.sleep(50);
		assertFinished(true);
		assertNotNull("next() should not return null", nextNode);
		assertEquals("next().getValue()", "1st value", nextNode.getValue());
		assertEquals("next().getValue()", "1st value", testee.next().getValue());
		testee.add("2nd value");
		assertEquals("next().getValue()", "2nd value", testee.next().getValue());
		testee.add("3rd value");
		testee.add("4th value");
		assertEquals("next().getValue()", "4th value", testee.next().getValue());
		assertEquals("next().getValue()", "3rd value", testee.next().getValue());
		assertEquals("next().getValue()", "1st value", testee.next().getValue());
		assertEquals("next().getValue()", "2nd value", testee.next().getValue());
		assertEquals("next().getValue()", "4th value", testee.next().getValue());
		assertEquals("next().getValue()", "3rd value", testee.next().getValue());
		assertEquals("next().getValue()", "1st value", testee.next().getValue());
		assertEquals("size after elements added", 4, testee.size());
		
		int size = testee.size();
		
		for (int i = 0; i < size; i++)
			testee.next().remove();
		
		assertEquals("final size after all elements removed", 0, testee.size());
	}
	
	private void assertFinished(boolean shouldBeFinished) throws Throwable {
		if (error != null)
			throw error;
		
		assertEquals("finished", shouldBeFinished, finished);
	}
}
