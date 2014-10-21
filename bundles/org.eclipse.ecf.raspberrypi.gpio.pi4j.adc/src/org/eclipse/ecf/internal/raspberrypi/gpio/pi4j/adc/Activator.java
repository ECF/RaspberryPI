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
import org.eclipse.ecf.raspberrypi.gpio.pi4j.adc.MCP3008;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.pi4j.io.gpio.GpioController;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static GpioController gpio;

	private ServiceTracker<IGenericPi, IGenericPi> serviceTracker;
	private GenericPiTrackerCustomizer genericPiTrackerCustomizer;
	private MCP3008 fMCP3008;

	@Override
	public void start(BundleContext ctxt) throws Exception {

		context = ctxt;
		
		fMCP3008 = new MCP3008().init();
		fMCP3008.startService(ctxt);

		this.genericPiTrackerCustomizer = new GenericPiTrackerCustomizer(
				context);
		// setup ServiceTracker for IGPIOPinInputListener whiteboard
		// services
		serviceTracker = new ServiceTracker<IGenericPi, IGenericPi>(context,
				IGenericPi.class, genericPiTrackerCustomizer);
		serviceTracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		fMCP3008.stopService();
		fMCP3008 = null;
		serviceTracker.close();
		serviceTracker = null;
		genericPiTrackerCustomizer.close();
		genericPiTrackerCustomizer = null;
		context = null;
	}
}
