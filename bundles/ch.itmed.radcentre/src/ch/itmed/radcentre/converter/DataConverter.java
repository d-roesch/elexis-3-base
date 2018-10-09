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

package ch.itmed.radcentre.converter;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class DataConverter {

	public static String patientGender(String gender) {
		if (gender.equalsIgnoreCase("M")) {
			return "m";
		} else if (gender.equalsIgnoreCase("F")) {
			return "w";
		}
		return "";
	}

	public static String elexisDate(String date) {
		return date.replaceAll("\\-", "");
	}

	public static String instantToElexisDate(String s) {
		Instant instant = Instant.parse(s);
		Date date = Date.from(instant);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.format(date);
	}

}
