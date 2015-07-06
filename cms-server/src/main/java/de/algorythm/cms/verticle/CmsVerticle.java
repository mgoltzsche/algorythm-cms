package de.algorythm.cms.verticle;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import de.algorythm.cms.CmsFacade;
import de.algorythm.cms.IHandler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.platform.Verticle;

public class CmsVerticle extends Verticle {

	static private final Logger log = LoggerFactory.getLogger(CmsVerticle.class);
	static private final URI ROOT = URI.create("/");

	//@Inject private ICmsCommonFacade facade;
	//private IRenderer renderer;
	private CmsFacade facade;
	private FrontController frontController;
	private Path docRoot;

	@Override
	public void start() {
		facade = new CmsFacade();
		frontController = new FrontController();

		try {
			frontController.registerController("^/(.*?)(/(index\\.html)?)?$", new XQueryController(facade, IOUtils.toString(getClass().getResourceAsStream("/xquery/load-document.xq")), "path"));
			frontController.registerController(".*", new XQueryController(facade, "'Not Found'"));
		} catch(IOException e) {
			throw new IllegalStateException(e);
		}

		try {
			docRoot = Files.createTempDirectory("cms-static-resources");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		/*try {
			renderer = initRenderer();
			renderer.expand();
			renderer.renderStaticResources(Format.HTML, new FileOutputTargetFactory(docRoot));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}*/
		
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
					returnPage(req, req.absoluteURI(), resp);
				}
			}
		}).listen(8080);
	}
	
	private void returnPage(final HttpServerRequest req, final URI absoluteUri, final HttpServerResponse resp) {
		String path = absoluteUri.getPath();
		path = path.charAt(path.length() - 1) == '/'
				? path + "index.html" : path;

		try {
			frontController.run(req);
		} catch (Exception e) { // Error
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
