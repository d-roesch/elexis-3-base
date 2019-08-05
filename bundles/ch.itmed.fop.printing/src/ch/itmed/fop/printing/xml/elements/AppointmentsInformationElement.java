/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.xml.elements;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.data.AppointmentData;
import ch.itmed.fop.printing.data.AppointmentsData;

public class AppointmentsInformationElement {

	public static Element create(Document doc, final boolean singleAppoinment) throws Exception {
		ArrayList<AppointmentData> al; // AppointmentsList

		if (singleAppoinment) {
			AppointmentData ad = new AppointmentData();
			ad.load();
			al = new ArrayList<>();
			al.add(ad);
		} else {
			AppointmentsData ad = new AppointmentsData();
			al = ad.load();
		}

		Element p = doc.createElement("AppointmentsInformation");

		Element c = doc.createElement("AgendaArea");
		c.appendChild(doc.createTextNode(al.get(0).getAgendaArea()));
		p.appendChild(c);

		c = doc.createElement("Appointments");
		for (AppointmentData ad : al) {
			Element appointment = doc.createElement("Appointment");
			appointment.appendChild(doc.createTextNode(ad.getAppointmentDetailed()));
			c.appendChild(appointment);
		}
		p.appendChild(c);

		return p;
	}
}