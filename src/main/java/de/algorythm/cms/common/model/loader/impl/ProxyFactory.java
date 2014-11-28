package de.algorythm.cms.common.model.loader.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProxyFactory {

	static private class ProxyHandler<C, T> implements InvocationHandler {

		private final Lock lock = new ReentrantLock();
		private final IProxyResolver<C, T> resolver;
		private final C context;
		private T resolvedObject;
		
		public ProxyHandler(final IProxyResolver<C, T> resolver, final C context) {
			this.resolver = resolver;
			this.context = context;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] arguments)
				throws Throwable {
			lock.lock();
			
			try {
				if (resolvedObject == null)
					resolvedObject = resolver.resolveProxy(context);
			} finally {
				lock.unlock();
			}
			
			return method.invoke(resolvedObject, arguments);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <C, T> T createProxy(final IProxyResolver<C, T> resolver, final C context, final Class<T> typeInterface) {
		final InvocationHandler h = new ProxyHandler<C, T>(resolver, context);
		final ClassLoader classLoader = getClass().getClassLoader();
		
		return (T) Proxy.newProxyInstance(classLoader, new Class<?>[] {typeInterface}, h);
	}
}