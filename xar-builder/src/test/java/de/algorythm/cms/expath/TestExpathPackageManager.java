package de.algorythm.cms.expath;

import de.algorythm.cms.expath.model.ExpathPackage;
import org.junit.Test;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by max on 01.06.15.
 */
public class TestExpathPackageManager {

    @Test
    public void deriveComponents_should_derive_components() throws Exception {
        ExpathPackageManager testee = new ExpathPackageManager();
        ExpathPackage pkg = new ExpathPackage(URI.create("http://example.org"), "exmpl", "Example package", "1.0");
        /*Set<String> derivedComponentUris = new HashSet<>();
        Set<String> expectedComponentUris = new HashSet<>(Arrays.asList(new String[] {
                "http://example.org/hello",
                "http://example.org/hello.xsl",
                "http://example.org/hello.txt"
        }));*/

        final Path sourceDirectory = Paths.get(getClass().getResource('/' + pkg.getAbbrev()).toURI());
        final Path xarFile = Paths.get(getClass().getResource("/").toURI()).resolve("test.xar");

        testee.deriveComponents(pkg, sourceDirectory);
        testee.createXarArchive(xarFile, pkg, sourceDirectory);

        /*for (AbstractComponent c : pkg.getComponents())
            derivedComponentUris.add(c.get)*/

        System.out.println(pkg.getComponents());
    }
}
