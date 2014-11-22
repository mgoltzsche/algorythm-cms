package de.algorythm.cms.common.resources;

public interface IProxyResolver<C,T> {

	T resolveProxy(C context);
}
