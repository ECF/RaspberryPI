/*******************************************************************************
 * Copyright (c) 2014 Remain B.V. All rights reserved. 
 * This program and the accompanying materials are made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Wim Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio;

/**
 * LM35 is a temperature measurement device.
 * 
 * @author Wim Jongman
 *
 */
public interface ILM35 {

	/**
	 * Temperature changed on the specified host.
	 * 
	 * @param pHost
	 * @param pTemperature
	 */
	public void setTemperature(String pHost, double pTemperature);

}
