package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.inject.Singleton;

import org.apache.commons.io.FileUtils;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.tools.conversion.woff.WoffWriter;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;

@Singleton
public class FontConverter {

	private File source;

	public void convertFont(final IRenderingContext context) throws Exception {
		final FontFactory fontFactory = FontFactory.getInstance();
		//final Font.Builder fontBuilder = fontFactory.newFontBuilder();
		//final Table.Builder<? extends Table> tableBuilder = fontBuilder.newTableBuilder(0);
		//tableBuilder.data().
		final byte[] fontBytes = FileUtils.readFileToByteArray(source);
		final Font font = fontFactory.loadFonts(readBytes(source))[0];
		final WoffWriter woffWriter = new WoffWriter();
		woffWriter.convert(font);
	}
	
	private byte[] readBytes(final File fontFile) throws IOException {
		byte[] fontBytes = new byte[0];
		FileInputStream fis = new FileInputStream(fontFile);
		
		try {
			fontBytes = new byte[(int) fontFile.length()];
			fis.read(fontBytes);
		} finally {
			fis.close();
		}
		
		return fontBytes;
	}
}
