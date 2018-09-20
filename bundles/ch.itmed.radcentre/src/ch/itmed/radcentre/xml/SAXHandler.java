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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ch.itmed.radcentre.converter.DataConverter;
import ch.itmed.radcentre.data.dao.CaseDao;
import ch.itmed.radcentre.data.dao.ConsultationDao;
import ch.itmed.radcentre.data.dao.PatientDao;

public final class SAXHandler extends DefaultHandler {
	private PatientDao patient;
	private CaseDao caseDao;
	private ConsultationDao consultationDao;
	private String content;
	private String tarmedCode;
	private boolean tarmedService;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {
		case "Visit":
			patient = new PatientDao();
			caseDao = new CaseDao();
			consultationDao = new ConsultationDao();
			break;
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
			patient.setBirthDate(DataConverter.elexisDate(content));
			break;
		case "PatientGender":
			patient.setGender(DataConverter.patientGender(content));
			break;
		case "PatientStreet":
			patient.setStreet(content);
			break;
		case "PatientPostcode":
			patient.setZip(content);
			break;
		case "PatientCity":
			patient.setCity(content);
			break;
		case "PatientCountry":
			patient.setCountry(content);
			break;
		case "PatientAHVNo":
			patient.setAhv(content);
			break;

		case "VisitNumber":
			caseDao.setNumber(content);
			break;
		case "PatientStatus":
			caseDao.setBillingMethod(content);
			break;
		case "CostUnit":
			caseDao.setCostBearer(content);
			break;
		case "InsuranceNo":
			caseDao.setInsuranceNumber(content);
			break;
		case "DateOfAccident":
			caseDao.setAccidentDate(content);
			break;
		case "AccidentNo":
			caseDao.setAccidentNumber(content);
			break;
		case "InvoiceRecipient":
			caseDao.setInvoiceRecipient(content);
			break;
		case "ReferrerGLN":
			caseDao.setReferrer(content);
			break;

		case "ServiceType":
			if (content.contentEquals("MATERIAL")) {
				tarmedService = false;
			} else if (content.contentEquals("TARMED")) {
				tarmedService = true;
			}
			break;
		case "ServiceDate":
			consultationDao.setDate(DataConverter.instantToElexisDate(content));
			break;
		case "ServiceProviderGLN":
			consultationDao.setServiceProviderGln(content);
			break;
		case "ServiceItem":
			if (tarmedService) {
				consultationDao.addTarmedCode(content);
				tarmedCode = content;
			} else {
				consultationDao.addArticleGtin(content);
			}
			break;
		case "Quantity":
			if (tarmedService) {
				consultationDao.addTarmedQuantity(Integer.parseInt(content));
			} else {
				consultationDao.addArticleQuantity(Integer.parseInt(content));
			}
			break;

		case "ParamValue":
			if (tarmedService) {
				consultationDao.addTarmedSide(tarmedCode, content);
			}
			break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		content = String.copyValueOf(ch, start, length).trim();
	}

	public PatientDao getPatient() {
		return patient;
	}

	public CaseDao getCase() {
		return caseDao;
	}

	public ConsultationDao getConsultation() {
		return consultationDao;
	}
}