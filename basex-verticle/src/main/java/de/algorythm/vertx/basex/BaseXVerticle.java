package de.algorythm.vertx.basex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.basex.api.client.LocalSession;
import org.basex.api.client.Session;
import org.basex.core.Context;
import org.basex.util.options.BooleanOption;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;

public class BaseXVerticle extends Verticle {

	private final ThreadLocal<Session> sessions = new ThreadLocal<Session>();
	private Context context;

	@Override
	public void start() {
		container.logger().info("BaseX verticle started with config " + container.config());
		
		//if (!vertx.isWorker())
		//	throw new IllegalStateException("Must be started as worker verticle");

		System.setProperty("org.basex.CHOP", "false"); // preserves white spaces
		context = new Context();
		
		vertx.eventBus().registerHandler("de.algorythm.basex", new Handler<Message<String>>() {
			@Override
			public void handle(Message<String> event) {
				container.logger().info("Received command: " + event.body());
				
				final Session session = getSession();
				final String command = event.body();
				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				
				session.setOutputStream(out);
				
				try {
					session.execute(command);
				} catch (IOException e) {
					container.logger().error("Command failed: " + command, e);
					event.fail(1, "Command error: " + e);
					return;
				}
				
				container.logger().info("Command executed: " + command);
				
				try {
					event.reply(out.toString(StandardCharsets.UTF_8.name()));
				} catch (UnsupportedEncodingException e) {
					container.logger().error("Cannot encode response due to unsupported encoding", e);
				}
			}
		});
	}
	
	@Override
	public void stop() {
		if (context != null)
			context.close();
	}
	
	private Session getSession() {
		Session session = sessions.get();
		
		if (session == null) {
			session = new LocalSession(context);
			sessions.set(session);
		}
		
		return session;
	}
}
