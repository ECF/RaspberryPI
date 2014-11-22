/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio.pi4j;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.ecf.raspberrypi.gpio.GPIOPinInputEvent;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinInputListener;

public class Pi4jGPIOPinInputListener implements IGPIOPinInputListener {

	private IGPIOPinInputListener listener;
	private TimerTask fTimerTask;
	private Timer fTimer;

	public Pi4jGPIOPinInputListener(IGPIOPinInputListener listener) {
		this.listener = listener;

		fTimerTask = new TimerTask() {
			@Override
			public void run() {
				handleInputEvent(new GPIOPinInputEvent(5, true));
			}
		};

		fTimer = new Timer(true);
		fTimer.scheduleAtFixedRate(fTimerTask, null, 5000);

	}

	@Override
	public void handleInputEvent(GPIOPinInputEvent event) {
		if (this.listener != null)
			this.listener.handleInputEvent(event);
	}

	public void close() {
		this.listener = null;
	}

}
