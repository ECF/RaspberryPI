/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio.pi4j;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.internal.raspberrypi.gpio.pi4j.Activator;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPin;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinInputListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;

public class InputListenerTrackerCustomizer implements
		ServiceTrackerCustomizer<IGPIOPinInputListener, IGPIOPinInputListener> {

	private BundleContext context;
	private Map<ServiceReference<IGPIOPinInputListener>, Pi4jGPIOPinInputListener> refToListenerMap = new HashMap<ServiceReference<IGPIOPinInputListener>, Pi4jGPIOPinInputListener>();

	public InputListenerTrackerCustomizer(BundleContext context) {
		this.context = context;
	}

	@Override
	public IGPIOPinInputListener addingService(
			ServiceReference<IGPIOPinInputListener> reference) {
		// get service
		IGPIOPinInputListener gPIOInputListener = context.getService(reference);
		if (gPIOInputListener == null)
			return null;
		// Get pinId
		Integer pinId = IGPIOPin.Util.getPinId(reference);
		if (pinId == null)
			pinId = new Integer(IGPIOPin.DEFAULT_INPUT_PIN);
		Pin pin = Pi4jGPIOPin.getPinForId(pinId.intValue());
		if (pin != null) {
			// Get pinName
			String pinName = IGPIOPin.Util.getPinName(reference);
			if (pinName == null)
				pinName = String.valueOf(pinId);
			// Get pullresistance
			Integer pullResistance = IGPIOPin.Util
					.getInputPullResistance(reference);
			// If it's not set, set to PUL
			if (pullResistance == null)
				pullResistance = new Integer(
						IGPIOPin.PIN_DEFAULTINPUTPULLRESISTANCE);
			PinPullResistance pr = Pi4jGPIOPin
					.getPinPullResistance(pullResistance.intValue());
			if (pr == null)
				pr = PinPullResistance.PULL_DOWN;
			// Get controller GPIO
			GpioController controller = Activator.getGPIOController();
			if (controller != null) {
				GpioPinDigitalInput inputListener = controller
						.provisionDigitalInputPin(pin, pinName, pr);
				// create new listener
				Pi4jGPIOPinInputListener pi4jListener = new Pi4jGPIOPinInputListener(
						inputListener, gPIOInputListener);
				synchronized (refToListenerMap) {
					refToListenerMap.put(reference, pi4jListener);
				}
			}
		} else {
			System.err
					.println("adding IGPIOPinInputListener service...pinId is not available for pinId="
							+ pinId);
		}
		return gPIOInputListener;
	}

	@Override
	public void modifiedService(
			ServiceReference<IGPIOPinInputListener> reference,
			IGPIOPinInputListener service) {
	}

	@Override
	public void removedService(
			ServiceReference<IGPIOPinInputListener> reference,
			IGPIOPinInputListener service) {
		synchronized (refToListenerMap) {
			Pi4jGPIOPinInputListener pi4j = refToListenerMap.remove(reference);
			if (pi4j != null)
				pi4j.close();
		}
	}

	public void close() {
		refToListenerMap.clear();
	}
}
