/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio;

import java.io.Serializable;

public class GPIOAnalogPinInputEvent extends AbstractGPIOPinEvent implements
		Serializable {

	private static final long serialVersionUID = 421080303440768006L;
	private final long state;

	public GPIOAnalogPinInputEvent(int pinId, long state) {
		super(pinId);
		this.state = state;
	}

	public long getState() {
		return state;
	}

	@Override
	public String toString() {
		return "GPIOAnalogPinInputEvent [getPinId()=" + getPinId() + ", state="
				+ state + "]";
	}

}
