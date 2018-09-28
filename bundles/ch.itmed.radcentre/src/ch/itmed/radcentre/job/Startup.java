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

package ch.itmed.radcentre.job;

import org.eclipse.ui.IStartup;

import ch.elexis.core.data.activator.CoreHub;
import ch.itmed.radcentre.preferences.PreferenceConstants;
import ch.itmed.radcentre.ui.MessageBoxUtil;

public class Startup implements IStartup {

	public void earlyStartup() {

		String path = CoreHub.globalCfg.get(PreferenceConstants.RADCENTRE_EXPORT_PATH, "");
		if (path.isEmpty()) {
			MessageBoxUtil.setErrorMsg("Spezifizieren Sie einen Export Pfad und starten Sie Elexis danach erneut.");
			MessageBoxUtil.showErrorDialog("Kein RadCentre Export Pfad spezifiziert");
		} else {
			(new Thread(new DirectoryWatcherRunnable())).start();
		}
	}
}
