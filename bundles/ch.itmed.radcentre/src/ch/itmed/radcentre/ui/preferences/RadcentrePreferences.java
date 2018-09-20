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

package ch.itmed.radcentre.ui.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.itmed.radcentre.preferences.PreferenceConstants;

public class RadcentrePreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor(PreferenceConstants.RADCENTRE_EXPORT_PATH, "RadCentre Export Pfad",
				getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench arg0) {
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.globalCfg));
		setDescription("Die Einstellungen gelten global.");
	}

}
