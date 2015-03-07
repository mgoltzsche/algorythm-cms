package de.algorythm.cms.common.scheduling;

public interface IFuture<R> {

	R sync() throws Throwable;
}
