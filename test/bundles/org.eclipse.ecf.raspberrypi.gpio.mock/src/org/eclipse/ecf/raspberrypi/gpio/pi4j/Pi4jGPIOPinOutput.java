/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio.pi4j;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.ecf.internal.raspberrypi.gpio.pi4j.Activator;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPin;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinOutput;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class Pi4jGPIOPinOutput implements IGPIOPinOutput {

	public static ServiceRegistration<IGPIOPinOutput> registerGPIOPinOutput(
			int pinId) {
		return registerGPIOPinOutput(pinId, null);
	}

	public static ServiceRegistration<IGPIOPinOutput> registerGPIOPinOutput(
			int pinId, Map<String, Object> pinProps) {
		return registerGPIOPinOutput(pinId, pinProps, null);
	}

	@SuppressWarnings("unchecked")
	public static ServiceRegistration<IGPIOPinOutput> registerGPIOPinOutput(
			int pinId, Map<String, Object> pinProps, BundleContext context) {

		if (context == null)
			context = Activator.getContext();

		Map<String, Object> registerProps = null;
		if (pinProps == null) {
			registerProps = IGPIOPin.Util.createOutputPinProps(pinId);
		} else {
			registerProps = new Hashtable<String, Object>();
			registerProps.putAll(pinProps);
		}

		// Initial output state.
		Boolean outputState = IGPIOPin.Util.getOutputState(registerProps);
		// If it's already set, then we use it to get the initPinState
		if (outputState != null) {
		}

		// pin name
		String pn = IGPIOPin.Util.getPinName(registerProps);
		if (pn == null) {
			pn = String.valueOf(pinId);
			IGPIOPin.Util.setPinName(registerProps, pn);
		}

		// pin id
		Integer pId = IGPIOPin.Util.getPinId(registerProps);
		if (pId == null)
			IGPIOPin.Util.setPinId(registerProps, pinId);


		// Now create new Pi4jGPIOPinOutput instance using pi4j pin provisioned,
		// and register
		return context.registerService(IGPIOPinOutput.class,
				new ServiceFactory<IGPIOPinOutput>() {
					@Override
					public IGPIOPinOutput getService(Bundle bundle,
							ServiceRegistration<IGPIOPinOutput> registration) {
						return new Pi4jGPIOPinOutput();
					}

					@Override
					public void ungetService(Bundle bundle,
							ServiceRegistration<IGPIOPinOutput> registration,
							IGPIOPinOutput service) {
					}
				}, (Dictionary<String, Object>) registerProps);
	}

	private boolean fPinState;

	@Override
	public boolean getState() {
		return fPinState;
	}

	@Override
	public void setState(boolean value) {
		fPinState = value;
	}

	@Override
	public boolean toggle() {
		fPinState = !fPinState;
		return getState();
	}

	@Override
	public void pulse(long duration, boolean pulseState) {
	}

	@Override
	public void blink(long delay, long duration, boolean blinkState) {
	}

	synchronized void close() {
	}

	@Override
	public int getPinId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
