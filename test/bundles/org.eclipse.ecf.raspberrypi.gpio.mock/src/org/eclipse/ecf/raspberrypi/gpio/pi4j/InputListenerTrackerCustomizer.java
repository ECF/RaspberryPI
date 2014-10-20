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

import org.eclipse.ecf.raspberrypi.gpio.IGPIOPin;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinInputListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class InputListenerTrackerCustomizer implements
		ServiceTrackerCustomizer<IGPIOPinInputListener, IGPIOPinInputListener> {

	private BundleContext context;
	private Map<ServiceReference<IGPIOPinInputListener>, IGPIOPinInputListener> refToListenerMap = new HashMap<ServiceReference<IGPIOPinInputListener>, IGPIOPinInputListener>();

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

		refToListenerMap.put(reference, gPIOInputListener);

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
			refToListenerMap.remove(reference);
		}
	}

	public void close() {
		refToListenerMap.clear();
	}
}
