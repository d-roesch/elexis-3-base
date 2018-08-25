package ch.itmed.radcentre.importer;

import java.util.List;

import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.itmed.radcentre.data.dao.PatientDao;

public final class PatientImp {
	private PatientDao pat;
	
	public PatientImp(PatientDao patDao) {
		this.pat = patDao;
	}

	public void addPatient() {
		if (!checkExistingPatient()) {
			Patient result = new Patient(pat.getLastName(), pat.getFirstName(), pat.getBirthDate(), pat.getGender());
			result.set("PatientNr", pat.getId());
		}
	}

	private boolean checkExistingPatient() {
		Query<Patient> query = new Query<>(Patient.class);
		query.add(Patient.FLD_PATID, Query.EQUALS, pat.getId());
		List<Patient> patient = query.execute();
		if (patient.size() == 0) {
			System.out.println("patient does not exist");
			return false;
		} else {
			System.out.println("patient does exist");
			
			checkForMutations(patient.get(0), pat);
			
			return true;
		}
	}
	
	private void checkForMutations(Patient pat, PatientDao patDao) {
		if (!pat.get("Bezeichnung1").equals(patDao.getLastName())) {
			pat.set("Bezeichnung1", patDao.getLastName());
		}
		if (!pat.get("Bezeichnung2").equals(patDao.getLastName())) {
			pat.set("Bezeichnung2", patDao.getFirstName());
		}
	}
}
