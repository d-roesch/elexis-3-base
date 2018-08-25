package ch.itmed.radcentre.xml;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ch.itmed.radcentre.importer.DataImporter;

public final class XmlParser {

	public static void run(Path file) {
		// System.out.println("run xml parser");
		try {
			SAXParserFactory parserFactor = SAXParserFactory.newInstance();
			SAXParser parser = parserFactor.newSAXParser();
			SAXHandler handler = new SAXHandler();
			parser.parse(Files.newInputStream(file), handler);

			DataImporter importer = new DataImporter();
			importer.startImport(handler);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
