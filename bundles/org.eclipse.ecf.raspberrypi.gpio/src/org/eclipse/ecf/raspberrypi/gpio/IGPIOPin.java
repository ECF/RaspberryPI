/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio;

public interface IGPIOPin {
	public static final int PIN_00 = 0;
	public static final int PIN_01 = 1;
	public static final int PIN_02 = 2;
	public static final int PIN_03 = 3;
	public static final int PIN_04 = 4;
	public static final int PIN_05 = 5;
	public static final int PIN_06 = 6;
	public static final int PIN_07 = 7;
	public static final int PIN_08 = 8;
	public static final int PIN_09 = 9;
	public static final int PIN_10 = 10;
	public static final int PIN_11 = 11;
	public static final int PIN_12 = 12;
	public static final int PIN_13 = 13;
	public static final int PIN_14 = 14;
	public static final int PIN_15 = 15;
	public static final int PIN_16 = 16;
	public static final int PIN_17 = 17;
	public static final int PIN_18 = 18;
	public static final int PIN_19 = 19;
	public static final int PIN_20 = 20;
	
	public static final String PIN_ID_PROP = "org.eclipse.ecf.raspberrypi.gpio.pin.id";
	public static final String PIN_NAME_PROP = "org.eclipse.ecf.raspberrypi.gpio.pin.name";
	public static final String PIN_DEFAULTSTATE_PROP = "org.eclipse.ecf.raspberrypi.gpio.pin.defaultstate";
	
}
