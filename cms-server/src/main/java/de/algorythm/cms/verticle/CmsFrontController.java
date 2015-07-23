package de.algorythm.cms.verticle;

import org.vertx.java.core.http.HttpServerRequest;

import de.algorythm.cms.url.IMatchState;
import de.algorythm.cms.url.IPathMatchHandler;
import de.algorythm.cms.url.IUrlManager;
import de.algorythm.cms.url.PathRule;
import de.algorythm.cms.url.PathRuleException;
import de.algorythm.cms.url.UrlManagerBuilder;
import de.algorythm.cms.url.config.UrlConfiguration;
import de.algorythm.cms.url.config.UrlRule;

public class CmsFrontController {

	private final IUrlManager<String, String> commandResolver;

	public CmsFrontController(UrlConfiguration urlConfig) throws PathRuleException {
		UrlManagerBuilder<String, String> builder = new UrlManagerBuilder<>("");
		
		for (UrlRule rule : urlConfig.getRules()) {
			builder.addRule(new PathRule<String, String>(rule.getKey(), rule.getPattern(), rule.getCommand()));
		}
		
		this.commandResolver = builder.build();
	}
	
	public void handleRequest(final HttpServerRequest req) {
		commandResolver.match(req.path(), new IPathMatchHandler<String, String>() {
			@Override
			public void matchedPrefix(IMatchState<String, String> state) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void matchedPositive(IMatchState<String, String> state) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void matchedNegative(String path) {
				
			}
		});
	}
}
