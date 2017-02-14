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
package ch.vality.legacy.print;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import ch.elexis.core.data.activator.CoreHub;
import ch.rgw.tools.ExHandler;

/**
 * Provides access to the registered legacy print plugin.
 * 
 * This class must not be instantiated. Instead, use the {@link #getLegacyPrintPlugin()
 * getLegacyPrintPlugin()} and {@link #isPluginAvailable() isPluginAvailable() } method.
 * 
 * @author Daniel Rösch, vality GmbH
 *
 */
public final class LegacyPrintProvider {
	public static ILegacyPrint legacyPrintPlugin;
	
	/**
	 * Provides an instance of current active legacy print plugin. Returns {@code null} if no legacy
	 * print plugin is specified.
	 * 
	 * @return The current active legacy print plugin
	 */
	public static ILegacyPrint getLegacyPrintPlugin(){
		String ExtensionToUse = CoreHub.localCfg.get("vality/legacy/print", null); //$NON-NLS-1$
		IExtensionRegistry exr = Platform.getExtensionRegistry();
		IExtensionPoint exp = exr.getExtensionPoint("ch.vality.legacy.print"); //$NON-NLS-1$
		if (exp != null) {
			IExtension[] extensions = exp.getExtensions();
			for (IExtension ex : extensions) {
				IConfigurationElement[] elems = ex.getConfigurationElements();
				for (IConfigurationElement el : elems) {
					if ((ExtensionToUse == null) || el.getAttribute("name").equals( //$NON-NLS-1$
						ExtensionToUse)) {
						try {
							return (ILegacyPrint) el.createExecutableExtension("class"); //$NON-NLS-1$
						} catch (Exception e) {
							ExHandler.handle(e);
						}
					}
				}
			}
		}
		// Return null if no plugin was found
		return null;
	}
	
	/**
	 * Checks if a legacy print plugin is specified.
	 * 
	 * @return Returns {@code true} if a legacy print plugin is available
	 */
	public static boolean isPluginAvailable() {
		if(legacyPrintPlugin == null) {
			ILegacyPrint plugin = getLegacyPrintPlugin();
			if (plugin == null) {
				return false;
			} else {
				legacyPrintPlugin = plugin;
				return true;
			}
		} else {
			return true;
		}
	}
	
}