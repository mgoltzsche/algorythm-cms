package de.algorythm.cms.common.concurrent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import de.algorythm.cms.common.scheduling.IExecutor;

public class PipelineExecutor implements IPipelineExecutor {

	private final IExecutor executor;
	private final ClassLoader loader;

	public PipelineExecutor(IExecutor executor) {
		this.executor = executor;
		this.loader = getClass().getClassLoader();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <I, T extends I> I createExecutor(T obj, Class<I> type) {
		InvocationHandler handler = new AsynchronousInvocationHandler(obj, executor);
		
		return (T) Proxy.newProxyInstance(loader, new Class[] {type}, handler);
	}
}
