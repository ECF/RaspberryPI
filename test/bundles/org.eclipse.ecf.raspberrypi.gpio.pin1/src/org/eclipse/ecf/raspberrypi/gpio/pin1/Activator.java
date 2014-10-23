/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio.pin1;

import java.util.Map;

import org.eclipse.ecf.raspberrypi.gpio.pi4j.Pi4jGPIOPinOutput;
import org.osgi.framework.BundleContext;

public class Activator extends org.eclipse.ecf.raspberrypi.gpio.pin0.Activator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		
	}

	public void registerPin(Map<String, Object> pinProps) {
		// register GPIOPin 0 with the above export properties
		setPin(Pi4jGPIOPinOutput.registerGPIOPinOutput(1, pinProps, getContext()));
	}
}
