/*******************************************************************************
 * Copyright (c) 2018 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.radcentre.xml;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ch.itmed.radcentre.importer.DataImporter;

public final class XmlParser {

	public static void run(Path file) throws Exception {
		SAXParserFactory parserFactor = SAXParserFactory.newInstance();
		SAXParser parser = parserFactor.newSAXParser();
		SAXHandler handler = new SAXHandler();
		parser.parse(Files.newInputStream(file), handler);

		DataImporter importer = new DataImporter();
		importer.startImport(handler);
	}

}
