package de.algorythm.cms;

import java.net.URI;

import org.junit.Test;

public class ExampleTest {
	
	@Test
	public void test() {
		System.out.println("### YEEHA ###");
		System.out.println(URI.create("http://üärtz.de/asdfä/..").normalize());
	}
}
