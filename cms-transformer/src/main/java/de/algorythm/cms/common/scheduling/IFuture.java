package de.algorythm.cms.common.scheduling;

public interface IFuture<R> {

	R getResult();
	R sync() throws Throwable;
}
