package de.algorythm.cms.common.scheduling;

public interface IFuture {

	void sync() throws InterruptedException;
}
