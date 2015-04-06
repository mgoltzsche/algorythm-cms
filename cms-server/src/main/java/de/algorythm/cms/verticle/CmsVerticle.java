package de.algorythm.cms.verticle;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.google.inject.Guice;

import de.algorythm.cms.common.CmsCommonModule;
import de.algorythm.cms.common.ICmsCommonFacade;
import de.algorythm.cms.common.model.entity.bundle.Format;
import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.impl.FileOutputTargetFactory;

public class CmsVerticle extends Verticle {

	static private final Logger log = LoggerFactory.getLogger(CmsVerticle.class);
	static private final URI ROOT = URI.create("/");
	
	@Inject
	private ICmsCommonFacade facade;
	private Path docRoot;
	private IRenderer renderer;

	@Override
	public void start() {
		try {
			docRoot = Files.createTempDirectory("cms-static-resources");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		try {
			renderer = initRenderer();
			renderer.expand();
			renderer.renderStaticResources(Format.HTML, new FileOutputTargetFactory(docRoot));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest req) {
				final HttpServerResponse resp = req.response();
				final String path = ROOT.resolve(req.absoluteURI()).normalize().getPath();
				
				if (path.length() > 4 && path.startsWith("/../") || path.equals("/..")) {
					// Forbidden - Outside doc root
					resp.headers().set("Content-Type", "text/html; charset=UTF-8");
					resp.setStatusCode(403);
					resp.setStatusMessage("Forbidden");
					resp.end(errorHtml("Forbidden", "The requested URL " + path + " is outside the document root"));
				} else if (path.startsWith("/r/")) {
					returnStaticResource(path, resp);
				} else {
					returnPage(req.absoluteURI(), resp);
				}
			}
		}).listen(8080);
	}
	
	private void returnPage(final URI absoluteUri, final HttpServerResponse resp) {
		String path = absoluteUri.getPath();
		URI indexUri = path.charAt(path.length() - 1) == '/'
				? URI.create(path + "index.html")
				: absoluteUri;
		
		try {
			final byte[] output = renderer.renderArtifact(indexUri);
			final String str = new String(output, StandardCharsets.UTF_8);
			
			resp.headers().set("Content-Type", "text/html; charset=UTF-8");
			resp.setStatusCode(200);
			resp.setStatusMessage("OK");
			resp.end(str);
		} catch (ResourceNotFoundException e) {
			notFound(resp);
		} catch (Exception e) {
			// Error
			log.error("Request failed", e);
			
			final StringWriter stWriter = new StringWriter();
			
			e.printStackTrace(new PrintWriter(stWriter));
			
			final String stackTrace = stWriter.toString();
			
			resp.headers().set("Content-Type", "text/html; charset=UTF-8");
			resp.setStatusCode(500);
			resp.setStatusMessage("Internal Server Error");
			resp.end(errorHtml("Internal Server Error", "<pre>" + stackTrace + "</pre>"));
		}
	}
	
	private void returnStaticResource(final String path, final HttpServerResponse resp) {
		Path requestedFile = docRoot.resolve(path.substring(1));
		
		if (Files.exists(requestedFile)) {
			resp.sendFile(requestedFile.toUri().getPath());
		} else {
			notFound(resp);
		}
	}
	
	private IRenderer initRenderer() throws Exception {
		final JsonObject cfg = getContainer().config().getObject("algorythm-cms");
		
		if (cfg == null)
			throw new IllegalStateException("Missing algorythm-cms object in config");
		
		final String repoCfg = cfg.getString("repo");
		final String bundleCfg = cfg.getString("bundle");
		
		if (repoCfg == null)
			throw new IllegalStateException("Missing repo entry in config");
		
		if (bundleCfg == null)
			throw new IllegalStateException("Missing bundle entry in config");
		
		final URI repoUri = URI.create(repoCfg);
		final URI bundleUri = URI.create(bundleCfg);
		final Path repoDirectory = Paths.get(repoUri);
		
		if (!Files.exists(repoDirectory))
			throw new IllegalStateException("Cannot find given repository directory " + repoDirectory);
		
		if (!Files.isDirectory(repoDirectory))
			throw new IllegalStateException("Given cms.repo value " + repoDirectory + " is not a directory");
		
		Guice.createInjector(new CmsCommonModule()).injectMembers(this);
		
		final IInputResolver resolver = facade.createInputResolver(repoDirectory);
		final IBundle bundle = facade.loadBundle(bundleUri, resolver);
		
		return facade.createRenderer(bundle, resolver);
	}

	private void notFound(final HttpServerResponse resp) {
		resp.headers().set("Content-Type", "text/html; charset=UTF-8");
		resp.setStatusCode(404);
		resp.setStatusMessage("Not Found");
		resp.end(errorHtml("Not Found", "The requested resource does not exist."));
	}
	
	private String errorHtml(String title, String msg) {
		return new StringBuilder("<html><head><title>")
			.append(title).append(" - algorythm CMS</title></head><body><h1>")
			.append(title).append("</h1><p>")
			.append(msg).append("</p></body></html>").toString();
	}
}
