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
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

public class Pi4jGPIOPinOutput extends Pi4jGPIOPin implements IGPIOPinOutput {

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
		Pin pin = getPinForId(pinId);
		if (pin == null)
			throw new IllegalArgumentException("Invalid pinId=" + pinId
					+ ".  pinId must be in range " + IGPIOPin.PIN_00 + "-"
					+ IGPIOPin.PIN_20);

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
		PinState initPinState = null;
		Boolean outputState = IGPIOPin.Util.getOutputState(registerProps);
		// If it's already set, then we use it to get the initPinState
		if (outputState != null) {
			if (outputState.booleanValue())
				initPinState = PinState.HIGH;
			else
				initPinState = PinState.LOW;
		} else {
			// we set it to false/low
			IGPIOPin.Util.setOutputState(registerProps, false);
			initPinState = PinState.LOW;
		}
		// pin name
		String pinName = IGPIOPin.Util.getPinName(registerProps);
		if (pinName == null) {
			pinName = String.valueOf(pinId);
			IGPIOPin.Util.setPinName(registerProps, pinName);
		}
		// Using GPIOController, provision Pi4j digital output pin
		GpioPinDigitalOutput pi4jPin = Activator.getGPIOController()
				.provisionDigitalOutputPin(pin, pinName, initPinState);
		// Now create new Pi4jGPIOPinOutput instance using pi4j pin provisioned,
		// and register
		return context.registerService(IGPIOPinOutput.class,
				new Pi4jGPIOPinOutput(pi4jPin),
				(Dictionary<String, Object>) registerProps);
	}

	private final GpioPinDigitalOutput pinImpl;

	public Pi4jGPIOPinOutput(GpioPinDigitalOutput pinImpl) {
		this.pinImpl = pinImpl;
	}

	@Override
	public boolean getState() {
		return (this.pinImpl.getState() == PinState.HIGH);
	}

	@Override
	public void setState(boolean value) {
		this.pinImpl.setState(value);
	}

	@Override
	public boolean toggle() {
		this.pinImpl.toggle();
		return getState();
	}

	@Override
	public void pulse(long duration, boolean pulseState) {
		this.pinImpl.pulse(duration, pulseState);
	}

	@Override
	public void blink(long delay, long duration, boolean blinkState) {
		this.pinImpl.blink(delay, duration, blinkState ? PinState.HIGH
				: PinState.LOW);
	}

}
