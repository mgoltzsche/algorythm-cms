package de.algorythm.cms;

import de.algorythm.cms.CmsFacade;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by max on 16.05.15.
 */
public class TestCmsDatabase {

    @Test
    public void importDatabaseFromDirectory_should_import_content_and_xquery_modules() throws Exception {
        CmsFacade testee = new CmsFacade();
        Path sourceDirectory = Paths.get(getClass().getResource("/example-import-directory").toURI());

        testee.openOrCreateDatabase("myimporteddatabase", sourceDirectory);

        // Assert
        final ByteArrayOutputStream out = new ByteArrayOutputStream(512);

        testee.session.setOutputStream(out);
        testee.session.execute("XQUERY import module namespace m = 'http://algorythm.de/cms/example/Hello';\n" +
                "m:hello(\"Universe\")");
        Assert.assertEquals("Hello Universe", out.toString(StandardCharsets.UTF_8.name()));
    }
}
