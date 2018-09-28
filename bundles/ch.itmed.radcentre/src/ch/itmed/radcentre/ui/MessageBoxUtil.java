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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

public final class MessageBoxUtil {
	private static String errorMsg;

	public static void setErrorMsg(String error) {
		errorMsg = error;
	}

	public static void showErrorDialog(final String title) {

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				MessageBox messgeBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						SWT.ICON_ERROR);
				messgeBox.setText(title);
				messgeBox.setMessage(errorMsg);
				messgeBox.open();
			}
		});
	}

}
