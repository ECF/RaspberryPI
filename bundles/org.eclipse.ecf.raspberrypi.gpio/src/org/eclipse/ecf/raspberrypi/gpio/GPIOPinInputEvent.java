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

/**
 * A simple class to represent an input event.
 *
 */
public class GPIOPinInputEvent extends AbstractGPIOPinEvent implements
		Serializable {

	private static final long serialVersionUID = -6103636181685626657L;

	private final boolean state;

	public GPIOPinInputEvent(int pinId, boolean state) {
		super(pinId);
		this.state = state;
	}

	public boolean getState() {
		return state;
	}

	@Override
	public String toString() {
		return "GPIOPinInputEvent [getPinId()=" + getPinId() + ", state="
				+ state + "]";
	}

}
