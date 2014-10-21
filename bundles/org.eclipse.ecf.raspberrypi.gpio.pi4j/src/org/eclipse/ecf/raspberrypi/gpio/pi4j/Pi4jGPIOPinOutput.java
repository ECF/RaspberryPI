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
		final Pin pin = getPinForId(pinId);
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
		PinState ips = null;
		Boolean outputState = IGPIOPin.Util.getOutputState(registerProps);
		// If it's already set, then we use it to get the initPinState
		if (outputState != null) {
			if (outputState.booleanValue())
				ips = PinState.HIGH;
			else
				ips = PinState.LOW;
		} else {
			// we set it to false/low
			IGPIOPin.Util.setOutputState(registerProps, false);
			ips = PinState.LOW;
		}
		final PinState initPinState = ips;
		// pin name
		String pn = IGPIOPin.Util.getPinName(registerProps);
		if (pn == null) {
			pn = String.valueOf(pinId);
			IGPIOPin.Util.setPinName(registerProps, pn);
		}
		final String pinName = pn;

		// pin id
		Integer pId = IGPIOPin.Util.getPinId(registerProps);
		if (pId == null)
			IGPIOPin.Util.setPinId(registerProps, pinId);

		// We only provision a pin once
		if (isProvisioned(pinId))
			throw new IllegalArgumentException(
					"pinId="
							+ pinId
							+ " has already been provisioned and can't be re-provisioned");
		final GpioPinDigitalOutput pi4jPin = Activator.getGPIOController()
				.provisionDigitalOutputPin(pin, pinName, initPinState);
		// Now create new Pi4jGPIOPinOutput instance using pi4j pin provisioned,
		// and register
		return context.registerService(IGPIOPinOutput.class,
				new ServiceFactory<IGPIOPinOutput>() {
					@Override
					public IGPIOPinOutput getService(Bundle bundle,
							ServiceRegistration<IGPIOPinOutput> registration) {
						return new Pi4jGPIOPinOutput(pi4jPin);
					}

					@Override
					public void ungetService(Bundle bundle,
							ServiceRegistration<IGPIOPinOutput> registration,
							IGPIOPinOutput service) {
						System.out.println("Unprovisioning pin");
						Activator.getGPIOController().unprovisionPin(
								((Pi4jGPIOPinOutput) service).pinImpl);
					}
				}, (Dictionary<String, Object>) registerProps);
	}

	private GpioPinDigitalOutput pinImpl;

	public Pi4jGPIOPinOutput(GpioPinDigitalOutput pinOutput) {
		this.pinImpl = pinOutput;
	}

	public int getPinId() {
		if (this.pinImpl == null)
			return -1;
		return this.pinImpl.getPin().getAddress();
	}

	@Override
	public boolean getState() {
		if (this.pinImpl == null)
			return false;
		return (this.pinImpl.getState() == PinState.HIGH);
	}

	@Override
	public void setState(boolean value) {
		if (this.pinImpl != null)
			this.pinImpl.setState(value);
	}

	@Override
	public boolean toggle() {
		if (this.pinImpl == null)
			return false;
		this.pinImpl.toggle();
		return getState();
	}

	@Override
	public void pulse(long duration, boolean pulseState) {
		if (this.pinImpl == null)
			return;
		this.pinImpl.pulse(duration, pulseState);
	}

	@Override
	public void blink(long delay, long duration, boolean blinkState) {
		if (this.pinImpl == null)
			return;
		this.pinImpl.blink(delay, duration, blinkState ? PinState.HIGH
				: PinState.LOW);
	}

	synchronized void close() {
		if (this.pinImpl != null) {
			Activator.getGPIOController().unprovisionPin(this.pinImpl);
			this.pinImpl = null;
		}
	}

}
