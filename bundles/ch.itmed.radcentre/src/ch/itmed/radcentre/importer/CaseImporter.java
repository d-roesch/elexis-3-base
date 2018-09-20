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

import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Organisation;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.elexis.core.ui.views.Messages;
import ch.itmed.radcentre.data.dao.CaseDao;

public final class CaseImporter {
	private CaseDao caseDao;
	private Patient patient;
	private Fall fall;

	public CaseImporter(CaseDao caseDao, Patient patient) {
		this.caseDao = caseDao;
		this.patient = patient;
	}

	public void addCase() {
		if (!checkExistingCase()) {
			fall = patient.neuerFall("Allgemein", "", caseDao.getBillingMethod());
			fall.setFallNummer(caseDao.getNumber());
			fall.setCostBearer(getCostBearer(caseDao.getCostBearer()));
			fall.setGarant(getInvoiceRecipient());
			fall.setRequiredString(Messages.FallDetailBlatt2_InsuranceNumber, caseDao.getInsuranceNumber());
			if (caseDao.getBillingMethod().equals("UVG")) {
				fall.setInfoString("Unfallnummer", caseDao.getAccidentNumber());
				fall.setInfoElement("Unfalldatum", caseDao.getAccidentDate());
			}
			fall.setInfoString("Zuweiser", KontaktImporter.getKontaktFromGln(caseDao.getReferrer()).getId());
		}
	}

	public Fall getCase() {
		return fall;
	}

	private boolean checkExistingCase() {
		Query<Fall> query = new Query<>(Fall.class);
		query.add(Fall.FLD_FALL_NUMMER, Query.EQUALS, caseDao.getNumber());
		List<Fall> result = query.execute();
		if (result.size() == 0) {
			// Fall does not exist
			return false;
		} else {
			// Fall does exist
			fall = result.get(0);

			Query<Rechnung> rQuery = new Query<Rechnung>(Rechnung.class);
			rQuery.add(Rechnung.CASE_ID, Query.EQUALS, fall.getId());
			List<Rechnung> billMatch = rQuery.execute();

			if (!billMatch.isEmpty()) {
				// Case exists, but is immutable because there are already existing invoices
				// A new case should be created
				return false;
			}

			checkForMutations();
			return true;
		}
	}

	private void checkForMutations() {
		if (!fall.getAbrechnungsSystem().equals(caseDao.getBillingMethod())) {
			fall.setAbrechnungsSystem(caseDao.getBillingMethod());
		}

		if (fall.getCostBearer() != null) {
			if (!fall.getCostBearer().equals(getCostBearer(caseDao.getCostBearer()))) {
				fall.setCostBearer(getCostBearer(caseDao.getCostBearer()));
			}
		} else {
			fall.setCostBearer(getCostBearer(caseDao.getCostBearer()));
		}

		if (fall.getGarant() != null) {
			if (!fall.getGarant().equals(getInvoiceRecipient())) {
				fall.setGarant(getInvoiceRecipient());
			}
		} else {
			fall.setGarant(getInvoiceRecipient());
		}

		if (!fall.getInfoString(Messages.FallDetailBlatt2_InsuranceNumber).equals(caseDao.getInsuranceNumber())) {
			fall.setRequiredString(Messages.FallDetailBlatt2_InsuranceNumber, caseDao.getInsuranceNumber());
		}

		if (!fall.getRequiredContact("Zuweiser").equals(KontaktImporter.getKontaktFromGln(caseDao.getReferrer()))) {
			fall.setInfoString("Zuweiser", KontaktImporter.getKontaktFromGln(caseDao.getReferrer()).getId());
		}

		if (fall.getAbrechnungsSystem().equals("UVG")) {
			if (fall.getInfoElement("Unfallnummer") != null) {
				if (!fall.getInfoElement("Unfallnummer").equals(caseDao.getAccidentNumber())) {
					fall.setInfoString("Unfallnummer", caseDao.getAccidentNumber());
				}
			} else {
				fall.setInfoString("Unfallnummer", caseDao.getAccidentNumber());
			}

			if (fall.getInfoElement("Unfalldatum") != null) {
				if (!fall.getInfoElement("Unfalldatum").equals(caseDao.getAccidentDate())) {
					fall.setInfoString("Unfalldatum", caseDao.getAccidentDate());
				}
			} else {
				fall.setInfoString("Unfalldatum", caseDao.getAccidentDate());
			}
		}

	}

	private Kontakt getCostBearer(String name) {
		Query<Organisation> query = new Query<>(Organisation.class);
		query.add("Name", Query.EQUALS, name);
		List<Organisation> result = query.execute();

		// Check if costBearer already exists, otherwise create a new one.
		if (result.size() == 0) {
			return new Organisation(name, "");
		} else {
			Kontakt costBearer = result.get(0);
			return costBearer;
		}
	}

	private Kontakt getInvoiceRecipient() {
		if (caseDao.getInvoiceRecipient().equals("S")) {
			return patient;
		} else {
			return getCostBearer(caseDao.getCostBearer());
		}
	}
}
