/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio.pi4j;

import org.eclipse.ecf.raspberrypi.gpio.GPIOPinInputEvent;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinInputListener;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Pi4jGPIOPinInputListener implements IGPIOPinInputListener,
		GpioPinListenerDigital {

	private GpioPinDigitalInput inputListener;
	private IGPIOPinInputListener listener;

	public Pi4jGPIOPinInputListener(GpioPinDigitalInput inputListener,
			IGPIOPinInputListener listener) {
		this.inputListener = inputListener;
		this.listener = listener;
		this.inputListener.addListener(this);
	}

	@Override
	public void handleInputEvent(GPIOPinInputEvent event) {
		if (this.listener != null)
			this.listener.handleInputEvent(event);
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(
			GpioPinDigitalStateChangeEvent arg0) {
		handleInputEvent(new GPIOPinInputEvent(arg0.getPin().getPin()
				.getAddress(), arg0.getState().isHigh()));
	}

	public void close() {
		if (this.inputListener != null) {
			this.inputListener.removeListener(this);
			this.inputListener = null;
		}
		this.listener = null;
	}

}
