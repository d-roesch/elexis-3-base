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

package ch.itmed.radcentre.importer;

import java.util.List;
import java.util.stream.Collectors;

import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.core.ui.views.Messages;
import ch.itmed.radcentre.data.dao.CaseDao;
import ch.itmed.radcentre.ui.MessageBoxUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CaseImporter {
	private CaseDao caseDao;
	private Patient patient;
	private Fall fall;
	private static Logger logger = LoggerFactory.getLogger(CaseImporter.class);

	public CaseImporter(CaseDao caseDao, Patient patient) {
		this.caseDao = caseDao;
		this.patient = patient;
	}

	public void addCase() {
		if (!checkExistingCase()) {
			addNewCase();
		}
	}

	public Fall getCase() {
		return fall;
	}

	private void addNewCase() {
		try {
			fall = patient.neuerFall("Allgemein", "", caseDao.getBillingMethod());
			// fall.setFallNummer(caseDao.getNumber());
			fall.setCostBearer(KontaktImporter.getCostBearerFromGln(caseDao.getCostBearer()));
			fall.setGarant(getInvoiceRecipient());

			if (fall.getAbrechnungsSystem().equals("KVG") || fall.getAbrechnungsSystem().equals("VVG")) {
				fall.setRequiredString(Messages.FallDetailBlatt2_InsuranceNumber, caseDao.getInsuranceNumber());
			}

			if (caseDao.getBillingMethod().equals("UVG")) {
				fall.setInfoString("Unfallnummer", caseDao.getAccidentNumber());
				fall.setInfoElement("Unfalldatum", caseDao.getAccidentDate());
			}

			if (caseDao.getBillingMethod().equals("IVG")) {
				fall.setInfoString("Fallnummer", caseDao.getIvNumber());
			}
			fall.setInfoString("Zuweiser", KontaktImporter.getReferrerFromGln(caseDao.getReferrer()).getId());
		} catch (Exception e) {
			fall.delete();
			MessageBoxUtil.setErrorMsg("Fehler beim Erstellen des Falls");
			logger.error("Failed to create case", e);
			throw new RuntimeException("Failed to create case.");
		}
	}

	private boolean checkExistingCase() {
		Query<Fall> query = new Query<>(Fall.class);
		query.add(Fall.FLD_PATIENT_ID, Query.EQUALS, patient.getId());
		List<Fall> result = query.execute();
		if (result.size() == 0) {
			// No open Fall exists
			return false;
		} else {
			List<Fall> matchedCase = result.stream().filter(f -> f.isOpen()).filter(f -> checkFallMetadata(f))
					.collect(Collectors.toList());
			if (matchedCase.size() == 0) {
				return false;
			}
			fall = matchedCase.get(0);

			return true;
		}
	}

	private boolean checkFallMetadata(Fall fall) {
		if (!fall.getAbrechnungsSystem().equals(caseDao.getBillingMethod())) {
			return false;
		}

		if (fall.getCostBearer() != null) {
			if (!fall.getCostBearer().equals(KontaktImporter.getCostBearerFromGln(caseDao.getCostBearer()))) {
				return false;
			}
		}

		if (fall.getGarant() != null) {
			if (!fall.getGarant().equals(getInvoiceRecipient())) {
				return false;
			}
		}

		if (fall.getRequiredContact("Zuweiser") != null) {
			if (!fall.getRequiredContact("Zuweiser")
					.equals(KontaktImporter.getReferrerFromGln(caseDao.getReferrer()))) {
				return false;
			}
		}

		if (fall.getAbrechnungsSystem().equals("KVG") || fall.getAbrechnungsSystem().equals("VVG")) {
			if (fall.getRequiredString(Messages.FallDetailBlatt2_InsuranceNumber) != null) {
				if (!fall.getRequiredString(Messages.FallDetailBlatt2_InsuranceNumber)
						.equals(caseDao.getInsuranceNumber())) {
					return false;
				}
			}
		}

		if (fall.getAbrechnungsSystem().equals("UVG")) {
			if (fall.getInfoElement("Unfallnummer") != null) {
				if (!fall.getInfoElement("Unfallnummer").equals(caseDao.getAccidentNumber())) {
					return false;
				}
			}

			if (fall.getInfoElement("Unfalldatum") != null) {
				if (!fall.getInfoElement("Unfalldatum").equals(caseDao.getAccidentDate())) {
					return false;
				}
			}
		}

		if (fall.getAbrechnungsSystem().equals("IVG")) {
			if (fall.getInfoElement("Fallnummer") != null) {
				if (!fall.getInfoElement("Fallnummer").equals(caseDao.getIvNumber())) {
					return false;
				}
			}
		}

		return true;
	}

	private Kontakt getInvoiceRecipient() {
		if (caseDao.getInvoiceRecipient().equalsIgnoreCase("TG")) {
			return patient;
		} else if (caseDao.getInvoiceRecipient().equalsIgnoreCase("TP")) {
			return KontaktImporter.getCostBearerFromGln(caseDao.getCostBearer());
		}
		return null;
	}
}
