package de.algorythm.cms.common.scheduling;

public interface IProgressObserver<R> {

	void finished();
	void finished(R result);
	void finishedWithError(Throwable error);
}
