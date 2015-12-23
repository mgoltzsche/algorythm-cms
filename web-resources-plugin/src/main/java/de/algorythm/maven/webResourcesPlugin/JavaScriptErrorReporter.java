package de.algorythm.maven.webResourcesPlugin;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

public class JavaScriptErrorReporter implements ErrorReporter {

	public final List<String> errors = new LinkedList<String>();
	public final List<String> warnings = new LinkedList<String>();

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