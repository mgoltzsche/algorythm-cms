package de.algorythm.cms.expath.handler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import de.algorythm.cms.expath.IResourceHandler;
import de.algorythm.cms.expath.model.ExpathPackage;
import de.algorythm.cms.expath.model.XQueryComponent;

/**
 * Derives an expath xquery component description.
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 */
public class XQueryModuleHandler implements IResourceHandler {

    static private final Pattern XQUERY_NS_PATTERN = Pattern.compile("^module\\s+namespace\\s+[\\w]+\\s*=\\s*\"([^\"]+)\"");

    @Override
    public void registerResource(ExpathPackage pkg, URI localFile, URI packageRelativeFilePath, Charset encoding) throws IOException {
        String xquery = IOUtils.toString(Files.newInputStream(Paths.get(localFile)), encoding.name());

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
