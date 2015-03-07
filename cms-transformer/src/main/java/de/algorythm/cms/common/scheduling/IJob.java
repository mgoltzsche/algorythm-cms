package de.algorythm.cms.common.scheduling;

public interface IJob<C> {

	void run(C context) throws Exception;
}
