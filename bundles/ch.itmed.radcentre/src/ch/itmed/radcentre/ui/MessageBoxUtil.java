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

package ch.itmed.radcentre.ui;

import ch.elexis.core.ui.util.SWTHelper;

public final class MessageBoxUtil {
	private static String errorMsg;

	public static void setErrorMsg(String error) {
		errorMsg = error;
	}

	public static void showErrorDialog(final String title) {
		SWTHelper.showError(title, errorMsg);
	}

}