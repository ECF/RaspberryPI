/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.raspberrypi.gpio.pi4j.adc;

import org.eclipse.ecf.raspberrypi.gpio.IGenericPi;
import org.eclipse.ecf.raspberrypi.gpio.pi4j.adc.GenericPiTrackerCustomizer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static GpioController gpio;

	private ServiceTracker<IGenericPi, IGenericPi> inputListenerTracker;
	private GenericPiTrackerCustomizer genericPiTrackerCustomizer;

	@Override
	public void start(BundleContext ctxt) throws Exception {
		context = ctxt;
		// create gpio controller
		gpio = GpioFactory.getInstance();
		this.genericPiTrackerCustomizer = new GenericPiTrackerCustomizer(
				context);
		// setup ServiceTracker for IGPIOPinInputListener whiteboard
		// services
		inputListenerTracker = new ServiceTracker<IGenericPi, IGenericPi>(
				context, IGenericPi.class, genericPiTrackerCustomizer);
		inputListenerTracker.open();
	}

	public static GpioController getGPIOController() {
		return gpio;
	}

	public static BundleContext getContext() {
		return context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (inputListenerTracker != null) {
			inputListenerTracker.close();
			inputListenerTracker = null;
		}
		if (genericPiTrackerCustomizer != null) {
			genericPiTrackerCustomizer.close();
			genericPiTrackerCustomizer = null;
		}
		context = null;
	}
}
