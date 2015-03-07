package de.algorythm.cms.common.scheduling;

public interface IExecutor {

	<C> void execute(IJob<C> job, C context);
}
