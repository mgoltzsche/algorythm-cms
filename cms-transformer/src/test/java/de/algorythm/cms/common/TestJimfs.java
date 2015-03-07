package de.algorythm.cms.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class TestJimfs {

	@Test
	public void test() throws IOException {
		FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
		
		try {
			Path dir = fs.getPath("/foo/x/../y");
			Files.createDirectories(dir);
			Path file = dir.resolve("hello.txt");
			Files.write(file, ImmutableList.of("hello world"), StandardCharsets.UTF_8);
			System.out.println(file.toUri());
			System.out.println(file.normalize());
			System.out.println(Files.readAllLines(fs.getPath("/foo/y/hello.txt"), StandardCharsets.UTF_8));
		} finally {
			fs.close();
		}
		
		System.out.println();
		
		fs = FileSystems.getDefault();
		Path a = fs.getPath(getClass().getResource("/test-content/article.xml").getPath());
		System.out.println(Files.readAllLines(a, StandardCharsets.UTF_8));
		Path dir = fs.getPath(getClass().getResource("/test-content/").getPath());
		
	}
}
