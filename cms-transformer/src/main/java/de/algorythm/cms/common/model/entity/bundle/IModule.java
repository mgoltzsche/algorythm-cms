package de.algorythm.cms.common.model.entity.bundle;

import java.net.URI;
import java.util.Set;

public interface IModule {

	Set<URI> getTemplates();
	Set<URI> getStyles();
	Set<URI> getScripts();
}
