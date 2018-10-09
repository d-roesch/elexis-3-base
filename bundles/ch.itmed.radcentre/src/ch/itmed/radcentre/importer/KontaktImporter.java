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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Query;
import ch.elexis.data.Xid;
import ch.itmed.radcentre.ui.MessageBoxUtil;

public final class KontaktImporter {
	private static Logger logger = LoggerFactory.getLogger(KontaktImporter.class);

	public static Kontakt getCostBearer(String name, String billingMethod) {

		String insurance;
		if (name.indexOf(" ") != -1) {
			insurance = name.substring(0, name.indexOf(" ") - 1);
		} else {
			insurance = name;
		}

		String bm;
		if (billingMethod.equalsIgnoreCase("UVG")) {
			bm = "UVG";
		} else {
			bm = "KVG";
		}

		Query<Kontakt> query = new Query<>(Kontakt.class);
		query.add(Kontakt.FLD_IS_ORGANIZATION, Query.EQUALS, "1");
		query.add("Bezeichnung1", Query.LIKE, insurance);
		query.add("TitelSuffix", Query.EQUALS, bm);
		List<Kontakt> result = query.execute();

		if (result.size() == 0) {
			logger.debug("No insurance Kontakt found with name: " + name);
			return null;
		} else {
			Kontakt costBearer = result.get(0);
			return costBearer;
		}
	}

	public static Kontakt getReferrerFromGln(String gln) {
		if (gln.isEmpty()) {
			logger.error("No Kontakt found because GLN is empty");
			MessageBoxUtil.setErrorMsg("Kein Zuweiser gefunden, weil GLN leer ist.");
			throw new NullPointerException("No Kontakt found because GLN is empty");
		}

		Query<Xid> query = new Query<>(Xid.class);
		query.add(Xid.FLD_DOMAIN, Query.EQUALS, "www.xid.ch/id/ean");
		query.add(Xid.FLD_ID_IN_DOMAIN, Query.EQUALS, gln);
		List<Xid> result = query.execute();

		if (result.size() == 0) {
			logger.error("No Kontakt found with GLN: " + gln);
			MessageBoxUtil.setErrorMsg("Kein Zuweiser mit GLN \"" + gln + "\" gefunden.");
			throw new NullPointerException("No Kontakt found with GLN: " + gln);
		} else {
			Xid xid = result.get(0);
			return (Kontakt) xid.getObject();
		}
	}

	public static Mandant getServiceProviderFromGln(String gln) {
		if (gln.isEmpty()) {
			logger.error("No Mandant found because GLN is empty");
			MessageBoxUtil.setErrorMsg("Kein Leistungserbringer gefunden, weil GLN leer ist.");
			throw new NullPointerException("No Mandant found because GLN is empty");
		}

		Query<Xid> query = new Query<>(Xid.class);
		query.add(Xid.FLD_DOMAIN, Query.EQUALS, "www.xid.ch/id/ean");
		query.add(Xid.FLD_ID_IN_DOMAIN, Query.EQUALS, gln);
		List<Xid> result = query.execute();

		if (result.size() == 0) {
			logger.error("No Mandant found with GLN: " + gln);
			MessageBoxUtil.setErrorMsg("Kein Leistungserbringer mit GLN \"" + gln + "\" gefunden.");
			throw new NullPointerException("No Mandant found with GLN: " + gln);
		} else {
			Xid xid = result.get(0);
			return Mandant.load(xid.get(Xid.FLD_OBJECT));
		}
	}
}
