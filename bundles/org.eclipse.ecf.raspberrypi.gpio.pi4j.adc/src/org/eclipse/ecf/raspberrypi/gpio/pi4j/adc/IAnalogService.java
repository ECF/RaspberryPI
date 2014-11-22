/*******************************************************************************
 * Copyright (c) 2014 Remain B.V. All rights reserved. 
 * This program and the accompanying materials are made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Wim Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio.pi4j.adc;

/**
 * This class represents an analog service. It could be a temperature
 * measurement device like a LM35 or just a regular potentiometer. The idea is
 * that this service will be captured by an analog device and then the device
 * can report the value.
 * 
 * @author Wim Jongman
 *
 */
public interface IAnalogService {

	/**
	 * A value changed on the specified host on the specified device.
	 * 
	 * @param pHost
	 * @param pDevice
	 *            the device identification
	 * @param pTemperature
	 */
	public void setValue(String pHost, String pDevice, double pValue);

}
