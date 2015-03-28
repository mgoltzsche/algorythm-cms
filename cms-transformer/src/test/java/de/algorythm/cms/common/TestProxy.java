package de.algorythm.cms.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;

public class TestProxy {

	static private interface IDummy {
		
		void doSth(String arg);
	}
	
	static private class DummyImpl implements IDummy {

		@Override
		public void doSth(String arg) {
			System.out.println(arg);
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
			return method.invoke(delegator, args);
		}
	}
	
	@Test
	public void testProxy() {
		final DummyImpl dummyImpl = new DummyImpl();
		final InvocationHandler handler = new SynchronousInvocationHandler(dummyImpl);
		final ClassLoader loader = getClass().getClassLoader();
		final Class<?>[] interfaces = new Class<?>[] {IDummy.class};
		
		final IDummy dummy = (IDummy) Proxy.newProxyInstance(loader, interfaces, handler);
		
		dummy.doSth("asdf");
	}
}
