package ch.itmed.radcentre.xml;

import java.util.ArrayList;
import java.util.Collections;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ch.itmed.radcentre.converter.DataConverter;
import ch.itmed.radcentre.data.dao.PatientDao;

public final class SAXHandler extends DefaultHandler {
	private PatientDao patient;
	private String content;
	private ArrayList<String> tarmed = new ArrayList<>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {
		case "Visit":
			patient = new PatientDao();
			break;

		case "PersonV40":
			System.out.println(attributes.getValue("PersonTyp"));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (qName) {
		case "PatientID":
			patient.setId(content);
			break;
		case "PatientName":
			patient.setLastName(content);
			break;
		case "PatientGivenName":
			patient.setFirstName(content);
			break;
		case "PatientBirthDate":
			patient.setBirthDate(DataConverter.patientBirthday(content));
			break;
		case "PatientGender":
			patient.setGender(DataConverter.patientGender(content));
			break;

		case "ServiceItem":
			tarmed.add(content);
			System.out.println("## Sorting tarmed");
			Collections.sort(tarmed);
			tarmed.stream().forEach(System.out::println);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		content = String.copyValueOf(ch, start, length).trim();
	}

	public PatientDao getPatient() {
		return patient;
	}
}

// https://sanaulla.info/2013/05/23/parsing-xml-using-dom-sax-and-stax-parser-in-java/#sax