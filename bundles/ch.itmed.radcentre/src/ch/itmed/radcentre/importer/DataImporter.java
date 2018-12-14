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

import ch.itmed.radcentre.xml.SAXHandler;

public final class DataImporter {

	public void startImport(SAXHandler saxHandler) {
		PatientImporter patientImp = new PatientImporter(saxHandler.getPatient());
		patientImp.addPatient();

		CaseImporter caseImp = new CaseImporter(saxHandler.getCase(), patientImp.getPatient());
		caseImp.addCase();

		ConsultationImporter consultationImp = new ConsultationImporter(caseImp.getCase(), saxHandler.getConsultation());
		consultationImp.addConsultation();
	}

}