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

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;

public class InputListenerTrackerCustomizer implements
		ServiceTrackerCustomizer<IGPIOPinInputListener, IGPIOPinInputListener> {

	private BundleContext context;
	private Map<ServiceReference<IGPIOPinInputListener>, ListenerHolder> refToListenerMap = new HashMap<ServiceReference<IGPIOPinInputListener>, ListenerHolder>();

	public InputListenerTrackerCustomizer(BundleContext context) {
		this.context = context;
	}

	class ListenerHolder {
		final GpioPinDigitalInput inputListener;
		Pi4jGPIOPinInputListener listener;

		public ListenerHolder(GpioPinDigitalInput inputListener) {
			this.inputListener = inputListener;
			setListener(listener);
		}

		public void setListener(Pi4jGPIOPinInputListener listener) {
			this.listener = listener;
		}
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
			synchronized (refToListenerMap) {
				// Find reference
				ListenerHolder listenerHolder = refToListenerMap.get(reference);
				GpioPinDigitalInput inputListener = null;
				// We've not seen this request before
				if (listenerHolder == null) {
					inputListener = Activator.getGPIOController()
							.provisionDigitalInputPin(pin, pinName, pr);
					listenerHolder = new ListenerHolder(inputListener);
				} else
					// We've seen it before so we use the old inputListener
					inputListener = listenerHolder.inputListener;
				// create new Pi4jGPIOPinInputListener
				Pi4jGPIOPinInputListener listener = new Pi4jGPIOPinInputListener(
						inputListener, gPIOInputListener);
				// set on the listener holder
				listenerHolder.setListener(listener);
				// put into map
				refToListenerMap.put(reference, listenerHolder);
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
			ListenerHolder listenerHolder = refToListenerMap.remove(reference);
			if (listenerHolder != null) {
				// Close our listener first
				if (listenerHolder.listener != null)
					listenerHolder.listener.close();
				// Then unprovision the pin
				if (listenerHolder.inputListener != null)
					Activator.getGPIOController().unprovisionPin(
							listenerHolder.inputListener);
			}
		}
	}

	public void close() {
		refToListenerMap.clear();
	}
}
