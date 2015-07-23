package de.algorythm.cms.verticle;

import java.io.InputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.platform.Verticle;

import de.algorythm.cms.url.IMatchState;
import de.algorythm.cms.url.IPathMatchHandler;
import de.algorythm.cms.url.IUrlManager;
import de.algorythm.cms.url.PathRule;
import de.algorythm.cms.url.PathRuleException;
import de.algorythm.cms.url.UrlManagerBuilder;
import de.algorythm.cms.url.config.UrlConfiguration;
import de.algorythm.cms.url.config.UrlRule;

public class CmsVerticle extends Verticle {

	static private final URI ROOT = URI.create("/");

	private int timeout;
	private String hostname = "algorythm.de";
	private IUrlManager<String, String> commandResolver;
	
	@Override
	public void start() {
		// TODO: config
		timeout = 10000;
		
		try {
			initCommandResolver();
		} catch (PathRuleException e) {
			throw new IllegalStateException("URL command configuration error", e);
		}
		
		//vertx.eventBus().send("de.algorythm.basex", "ADD mydatabase/vertx-example.xml <root>vertx example</root>");
		
		//container.deployModule("de.algorythm.cms~basex-verticle~1.0.0-SNAPSHOT");
		vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				final HttpServerResponse resp = req.response();
				String path = req.path();
				container.logger().info("REQUEST: " + path);
				//resp.end("sent");
				
				commandResolver.match(path, new IPathMatchHandler<String, String>() {
					@Override
					public void matchedPrefix(IMatchState<String, String> state) {}
					
					@Override
					public void matchedPositive(IMatchState<String, String> state) {
						execCommand(state.getResource(), req);
					}
					
					@Override
					public void matchedNegative(String path) {
						notFound(req.response());
					}
				});
			}
		}).listen(8080);
		
		container.logger().info("CMS verticle started");
	}

	private void initCommandResolver() throws PathRuleException {
		final UrlConfiguration urlConfig;
		
		try (InputStream in = getClass().getResourceAsStream("/url-config.xml")) {
			urlConfig = UrlConfiguration.fromStream(in);
		} catch(Exception e) {
			throw new RuntimeException("Cannot load URL configuration", e);
		}
		
		UrlManagerBuilder<String, String> builder = new UrlManagerBuilder<>("");
		
		for (UrlRule rule : urlConfig.getRules()) {
			builder.addRule(new PathRule<String, String>(rule.getKey(), rule.getPattern(), rule.getCommand()));
		}
		
		commandResolver = builder.build();
	}
	
	private void execCommand(String command, HttpServerRequest req) {
		final HttpServerResponse resp = req.response();
		
		vertx.eventBus().sendWithTimeout("de.algorythm.basex", command, timeout, new Handler<AsyncResult<Message<String>>>() {
			@Override
			public void handle(AsyncResult<Message<String>> event) {
				container.logger().info("message received (within timeout)");
				
				resp.headers().set("Content-Type", "text/html; charset=UTF-8");
				
				if (event.succeeded()) {
					resp.setStatusCode(200);
					resp.setStatusMessage("OK");
					resp.end(event.result().body());
				} else {
					resp.setStatusCode(500);
					resp.setStatusMessage("Internal Server Error");
					resp.end(errorHtml("Internal Server Error", "Service failure: " + event.failed()));
				}
			}
		});
	}
	
	private void notFound(HttpServerResponse resp) {
		resp.headers().set("Content-Type", "text/html; charset=UTF-8");
		resp.setStatusCode(404);
		resp.setStatusMessage("Not Found");
		resp.end(errorHtml("Not Found", "The requested resource does not exist."));
	}
	
	private String errorHtml(String title, String msg) {
		return new StringBuilder("<html><head><title>")
			.append(title).append(" - ").append(hostname).append("</title></head><body><h1>")
			.append(title).append("</h1><p>")
			.append(msg).append("</p></body></html>").toString();
	}
}
