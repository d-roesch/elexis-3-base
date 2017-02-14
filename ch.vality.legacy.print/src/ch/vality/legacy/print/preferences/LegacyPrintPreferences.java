/*******************************************************************************
 * Copyright (c) 2017 vality GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Daniel Rösch, vality GmbH - initial implementation
 *     
 ******************************************************************************/
package ch.vality.legacy.print.preferences;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

/**
 * A preference page for the administration of the 
 * legacy print plugin settings.
 * 
 * @author Daniel Rösch, vality GmbH
 *
 */
public class LegacyPrintPreferences extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	
	public LegacyPrintPreferences(){
		super(GRID);
		// Make sure we persist the configuration 
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
		setDescription("Legacy Print Settings Description"); //$NON-NLS-1$
	}
	
	@Override
	protected void createFieldEditors(){
		
		List<IConfigurationElement> list = Extensions.getExtensions("ch.vality.legacy.print"); //$NON-NLS-1$
		String[][] rows = new String[list.size()][];
		int i = 0;
		for (IConfigurationElement ice : list) {
			rows[i] = new String[2];
			rows[i][1] = ice.getAttribute("name"); //$NON-NLS-1$
			rows[i][0] = Integer.toString(i) + " : " + rows[i][1]; //$NON-NLS-1$
			i += 1;
		}
		addField(new RadioGroupFieldEditor("vality/legacy/print", "Legacy Print Plugins", 2,
			rows, getFieldEditorParent())); //$NON-NLS-1$
	}
	
	public void init(IWorkbench workbench){}
}
