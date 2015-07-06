package de.algorythm.cms.expath.handler;

import de.algorythm.cms.expath.IResourceHandler;
import de.algorythm.cms.expath.model.ExpathPackage;
import de.algorythm.cms.expath.model.XQueryComponent;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by max on 31.05.15.
 */
public class XQueryModuleHandler implements IResourceHandler {

    static private final Pattern XQUERY_NS_PATTERN = Pattern.compile("^module\\s+namespace\\s+[\\w]+\\s*=\\s*\"([^\"]+)\"");

    @Override
    public void registerResource(ExpathPackage pkg, URI localFile, URI packageRelativeFilePath) throws IOException {
        String xquery = IOUtils.toString(Files.newInputStream(Paths.get(localFile)), StandardCharsets.UTF_8.displayName());

        if (xquery.isEmpty())
            throw new IllegalStateException("XQuery module " + localFile + " is empty");

        xquery = xquery.replaceAll("(:.*?:)", "").trim();
        Matcher matcher = XQUERY_NS_PATTERN.matcher(xquery);
        // Matches e.g. 'module namespace functx = "http://www.functx.com"';

        if (matcher.find()) {
            URI ns = URI.create(matcher.group(1));

            pkg.getComponents().add(new XQueryComponent(packageRelativeFilePath, ns));
        } else {
            throw new IllegalStateException("XQuery module " + localFile + " does not declare module namespace");
        }
    }
}
