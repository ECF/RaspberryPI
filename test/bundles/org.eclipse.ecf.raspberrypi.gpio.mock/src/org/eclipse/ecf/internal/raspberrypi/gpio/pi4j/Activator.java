/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.raspberrypi.gpio.pi4j;

import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinInputListener;
import org.eclipse.ecf.raspberrypi.gpio.pi4j.InputListenerTrackerCustomizer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private static BundleContext context;

	private ServiceTracker<IGPIOPinInputListener, IGPIOPinInputListener> inputListenerTracker;
	private InputListenerTrackerCustomizer inputListenerTrackerCustomizer;

	@Override
	public void start(BundleContext ctxt) throws Exception {
		context = ctxt;
		// create gpio controller
		this.inputListenerTrackerCustomizer = new InputListenerTrackerCustomizer(
				context);
		// setup ServiceTracker for IGPIOPinInputListener whiteboard
		// services
		inputListenerTracker = new ServiceTracker<IGPIOPinInputListener, IGPIOPinInputListener>(
				context, IGPIOPinInputListener.class,
				inputListenerTrackerCustomizer);
		inputListenerTracker.open();
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
		if (inputListenerTrackerCustomizer != null) {
			inputListenerTrackerCustomizer.close();
			inputListenerTrackerCustomizer = null;
		}
		context = null;
	}

}
