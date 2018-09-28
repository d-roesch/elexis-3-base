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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Organisation;
import ch.elexis.data.Query;
import ch.itmed.radcentre.ui.MessageBoxUtil;

public final class KontaktImporter {
	private static Logger logger = LoggerFactory.getLogger(KontaktImporter.class);

	public static Kontakt getCostBearer(String name) {
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

	public static Kontakt getReferrerFromGln(String gln) {
		if (gln.isEmpty()) {
			logger.error("No contact found because GLN is empty");
			MessageBoxUtil.setErrorMsg("Kein Zuweiser gefunden, weil GLN leer ist.");
			return null;
		}

		Query<Kontakt> query = new Query<>(Kontakt.class);
		List<Kontakt> contacts = query.execute();

		List<Kontakt> result = (List<Kontakt>) contacts.stream()
				.filter(contact -> contact.getInfoString("EAN").equals(gln)).collect(Collectors.toList());

		if (result.size() == 0) {
			logger.error("No contact found with GLN: " + gln);
			MessageBoxUtil.setErrorMsg("Kein Zuweiser mit GLN \"" + gln + "\" gefunden.");
		}

		return result.get(0);
	}

	public static Mandant getServiceProviderFromGln(String gln) throws IndexOutOfBoundsException {
		Query<Mandant> query = new Query<>(Mandant.class);
		List<Mandant> mandators = query.execute();

		List<Mandant> result = (List<Mandant>) mandators.stream()
				.filter(contact -> contact.getInfoString("EAN").equals(gln)).collect(Collectors.toList());

		if (result.size() == 0) {
			logger.error("No mandator found with GLN: " + gln);
			MessageBoxUtil.setErrorMsg("Kein Leistungserbringer mit GLN \"" + gln + "\" gefunden.");
		}

		return result.get(0);
	}
}
