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

import ch.elexis.core.constants.XidConstants;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.itmed.radcentre.data.dao.PatientDao;

public final class PatientImporter {
	private PatientDao patDao;
	private Patient patient;

	public PatientImporter(PatientDao patDao) {
		this.patDao = patDao;
	}

	public void addPatient() {
		if (!checkExistingPatient()) {
			this.patient = new Patient(patDao.getLastName(), patDao.getFirstName(), patDao.getBirthDate(),
					patDao.getGender());
			patient.set("PatientNr", patDao.getId());
			patient.set("Strasse", patDao.getStreet());
			patient.set("Ort", patDao.getCity());
			patient.set("Plz", patDao.getZip());
			patient.set("Land", patDao.getCountry());
			patient.addXid(XidConstants.DOMAIN_AHV, patDao.getAhv(), true);
		}
	}

	public Patient getPatient() {
		return this.patient;
	}

	private boolean checkExistingPatient() {
		if (checkExistingLegacyPatient()) {
			return true;
		}

		Query<Patient> query = new Query<>(Patient.class);
		query.add(Patient.FLD_PATID, Query.EQUALS, patDao.getId());
		List<Patient> patient = query.execute();
		if (patient.size() == 0) {
			// patient does not exist
			return false;
		} else {
			// patient does exist

			this.patient = patient.get(0);
			checkForMutations();

			return true;
		}
	}

	private boolean checkExistingLegacyPatient() {
		Query<Patient> query = new Query<>(Patient.class);
		query.add(Patient.FLD_PATID, Query.EQUALS, patDao.getLegacyId());
		List<Patient> patient = query.execute();
		if (patient.size() == 0) {
			// patient does not exist
			return false;
		} else {
			// patient does exist

			this.patient = patient.get(0);
			this.patient.set("PatientNr", patDao.getId());
			checkForMutations();

			return true;
		}
	}

	private void checkForMutations() {
		if (!patient.get("Bezeichnung1").equals(patDao.getLastName())) {
			patient.set("Bezeichnung1", patDao.getLastName());
		}
		if (!patient.get("Bezeichnung2").equals(patDao.getLastName())) {
			patient.set("Bezeichnung2", patDao.getFirstName());
		}
		if (!patient.get("Geburtsdatum").equals(patDao.getBirthDate())) {
			patient.set("Geburtsdatum", patDao.getBirthDate());
		}
		if (!patient.get("Geschlecht").equals(patDao.getGender())) {
			patient.set("Geschlecht", patDao.getGender());
		}
		if (!patient.get("Strasse").equals(patDao.getStreet())) {
			patient.set("Strasse", patDao.getStreet());
		}
		if (!patient.get("Ort").equals(patDao.getCity())) {
			patient.set("Ort", patDao.getCity());
		}
		if (!patient.get("Plz").equals(patDao.getZip())) {
			patient.set("Plz", patDao.getZip());
		}
		if (!patient.get("Land").equals(patDao.getCountry())) {
			patient.set("Land", patDao.getCountry());
		}
		if (!patient.getXid(XidConstants.DOMAIN_AHV).equals(patDao.getAhv())) {
			patient.addXid(XidConstants.DOMAIN_AHV, patDao.getAhv(), true);
		}

	}
}
