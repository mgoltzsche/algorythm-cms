package de.algorythm.cms.common.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author	Max Goltzsche <max.goltzsche@algorythm.de>
 * @date	28.06.2014
 */
public class DeadlockDetection {

	static private final Logger log = LoggerFactory.getLogger(DeadlockDetection.class);
	
	
	private final Thread deadlockDetector;
	private volatile boolean deadlockDetected = false;
	
	public DeadlockDetection() {
		deadlockDetector = new Thread("deadlock-detector") {
			@Override
			public void run() {
				ThreadMXBean bean = ManagementFactory.getThreadMXBean();
				
				while(!deadlockDetected) {
					long[] threadIds = bean.findDeadlockedThreads();
					
					if (threadIds != null && threadIds.length > 0) {
						deadlockDetected = true;
						ThreadInfo[] threadInfos = bean.getThreadInfo(threadIds);
						Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
						
						for (ThreadInfo info : threadInfos) {
							StringBuilder sb = new StringBuilder()
								.append("Deadlock detected for ")
								.append(info.getThreadName())
								.append(". Stack:");
							
							for (Entry<Thread, StackTraceElement[]> trace : traces.entrySet())
								if (trace.getKey().getName().equals(info.getThreadName()))
									for (StackTraceElement stack : trace.getValue())
										sb.append("\n    ").append(stack);
							
							log.error(sb.toString());
						}
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		
		deadlockDetector.start();
	}
	
	public boolean stop() {
		deadlockDetector.interrupt();
		
		return deadlockDetected;
	}
}
