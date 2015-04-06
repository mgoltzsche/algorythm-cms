package de.algorythm.cms.common.concurrent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import de.algorythm.cms.common.scheduling.IExecutor;

public class AsynchronousInvocationHandler implements InvocationHandler {

	private final IExecutor executor;
	private final Object delegator;
	
	public AsynchronousInvocationHandler(Object delegator, IExecutor executor) {
		this.delegator = delegator;
		this.executor = executor;
	}
	
	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args)
			throws Throwable {
		final Object result;
		final Runnable methodInvocation;
		final List<Condition> preconditions = preconditions(args);
		
		if (method.getReturnType() == Future.class) {
			final Future<?> future = new Future<Object>();
			
			methodInvocation = new Runnable() {
				@Override
				public void run() {
					try {
						Future<?> result = (Future<?>) method.invoke(delegator, args);
						
						setResult(future, result.getResult());
					} catch(InvocationTargetException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			};
			
			result = future;
		} else if (method.getReturnType() == Condition.class) {
			final Condition condition = new Condition();
			
			methodInvocation = new Runnable() {
				@Override
				public void run() {
					try {
						method.invoke(delegator, args);
						condition.fulfill();
					} catch(InvocationTargetException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			};
			
			result = condition;
		} else {
			methodInvocation = new Runnable() {
				@Override
				public void run() {
					try {
						method.invoke(delegator, args);
					} catch(InvocationTargetException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			};
			
			result = null;
		}
		
		if (preconditions.isEmpty()) {
			executor.execute(methodInvocation);
		} else {
			new Condition(preconditions).addListener(new IConditionListener() {
				@Override
				public void fulfilled(Condition condition) {
					executor.execute(methodInvocation);
				}
			});
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private <R> void setResult(Future<R> future, Object result) {
		future.setResult((R) result);
	}
	
	private List<Condition> preconditions(Object[] args) {
		final List<Condition> preconditions = new LinkedList<>();
		
		for (Object arg : args) {
			if (arg != null && arg instanceof Condition) {
				preconditions.add((Condition) arg);
			}
		}
		
		return preconditions;
	}
}
