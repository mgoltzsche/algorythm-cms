package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.resources.IOutputTarget;
import de.algorythm.cms.common.resources.IOutputTargetFactory;

@Singleton
public class JavascriptCompressor {

	static private final Logger log = LoggerFactory.getLogger(JavascriptCompressor.class);
	static private final URI MAIN_JS = URI.create("main.js");
	
	static private class JsErrorReporter implements ErrorReporter {

		private final List<String> errors = new LinkedList<String>();
		private final List<String> warnings = new LinkedList<String>();
		
		@Override
		public void warning(String message, String sourceName, int line,
				String lineSource, int lineOffset) {
			warnings.add(message(message, sourceName, line, lineSource, lineOffset));
		}

		@Override
		public void error(String message, String sourceName, int line,
				String lineSource, int lineOffset) {
			errors.add(message(message, sourceName, line, lineSource, lineOffset));
		}

		@Override
		public EvaluatorException runtimeError(String message,
				String sourceName, int line, String lineSource, int lineOffset) {
			final StringBuilder msg = new StringBuilder(message(message, sourceName, line, lineSource, lineOffset));
			
			for (String error : errors)
				msg.append("\n\t").append(error);
			
			throw new EvaluatorException(msg.toString());
		}
		
		private String message(String message,
				String sourceName, int line, String lineSource, int column) {
			final StringBuilder msg = new StringBuilder(message).append(' ');
			
			if (sourceName != null)
				msg.append(sourceName).append(':');
			
			msg.append(line).append(':').append(column);
			
			if (lineSource != null)
				msg.append(" - ").append(lineSource);
			
			return msg.toString();
		}
	}

	private boolean compress = false;
	private boolean munge = true;
	private boolean verbose = false;
	private boolean preserveAllSemiColons = false;
	private boolean disableOptimizations = false;

	public void compressJs(final IRenderingContext ctx, final Collection<URI> sources, final IOutputTargetFactory targetFactory) throws Exception {
		final TimeMeter meter = TimeMeter.meter(toString());
		final String jsPath = ctx.getResourcePrefix().resolve(MAIN_JS).getPath();
		final StringBuilder scriptBuilder = new StringBuilder();
		
		for (URI source : sources) {
			InputStream stream = ctx.createInputStream(source);
			byte[] bytes = IOUtils.toByteArray(stream);
			String script = new String(bytes, StandardCharsets.UTF_8);
			
			if (compress) {
				final Reader reader = new StringReader(script);
				final JsErrorReporter reporter = new JsErrorReporter();
				final JavaScriptCompressor compressor = new JavaScriptCompressor(reader, reporter);
				final Writer writer = new StringWriter();
				compressor.compress(writer, 0, munge, verbose, preserveAllSemiColons, disableOptimizations);
				
				for (String warning : reporter.warnings)
					log.warn(warning);
				
				script = writer.toString().replace("\n", "");
			}
			
			scriptBuilder.append(script).append("\n");
		}
		
		final String scripts = scriptBuilder.toString();
		final IOutputTarget target = targetFactory.createOutputTarget(jsPath);
		
		try (OutputStream outStream = target.createOutputStream()) {
			outStream.write(scripts.getBytes(StandardCharsets.UTF_8));
		} catch(Exception e) {
			throw e;
		}
		
		meter.finish();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}