package de.algorythm.cms.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.scheduling.impl.Future;

public class TestProxy {

	static private interface IDummy {
		
		Future<String> doSth(String arg);
	}
	
	static private class DummyImpl implements IDummy {

		@Override
		public Future<String> doSth(String arg) {
			System.out.println("doSth(" + arg + ")");
			
			return new Future<>(arg);
		}
	}
	
	static private class SynchronousInvocationHandler implements InvocationHandler {

		private final Object delegator;
		
		public SynchronousInvocationHandler(final Object delegator) {
			this.delegator = delegator;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if (method.getReturnType() == Future.class) {
				Future<?> result = (Future<?>) method.invoke(delegator, args);
				return new Future<Object>(result.getResult());
			} else {
				return null;
			}
		}
	}
	
	@Test
	public void testProxy() {
		DummyImpl dummyImpl = new DummyImpl();
		InvocationHandler handler = new SynchronousInvocationHandler(dummyImpl);
		ClassLoader loader = getClass().getClassLoader();
		Class<?>[] interfaces = new Class<?>[] {IDummy.class};
		
		TimeMeter meter = TimeMeter.meter("proxy creation 1");
		IDummy dummy = (IDummy) Proxy.newProxyInstance(loader, interfaces, handler);
		meter.finish();
		
		meter = TimeMeter.meter("proxy creation 2");
		dummy = (IDummy) Proxy.newProxyInstance(loader, interfaces, handler);
		meter.finish();
		
		meter = TimeMeter.meter("proxy invocation 1");
		System.out.println(dummy.doSth("asdf").getResult());
		meter.finish();
		
		meter = TimeMeter.meter("proxy invocation 2");
		System.out.println(dummy.doSth("asdf").getResult());
		meter.finish();
	}
}
