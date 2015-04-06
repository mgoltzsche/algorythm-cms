package de.algorythm.cms.common.concurrent;

public interface IPipelineExecutor {

	<I, T extends I> I createExecutor(T obj, Class<I> interfaceType);
}
