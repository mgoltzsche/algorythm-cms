package de.algorythm.cms.common.model.loader.impl;

public interface IProxyResolver<C,T> {

	T resolveProxy(C context);
}
