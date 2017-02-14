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

public interface ILegacyPrint {
	public void printAppointment();
	public void printAddressLabel();
	public void printContactLabel();
	public void printLabel();
	public void printVersionedLabel();
}
