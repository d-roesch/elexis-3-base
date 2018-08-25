package ch.itmed.radcentre.importer;

import ch.itmed.radcentre.xml.SAXHandler;

public final class DataImporter {
	
	public void startImport(SAXHandler saxHandler) {
		PatientImp.addPatient(saxHandler.getPatient());
		
	}

}
