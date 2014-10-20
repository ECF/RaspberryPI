/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio.pi4j.adc;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.internal.raspberrypi.gpio.pi4j.adc.Activator;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPin;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinInputListener;
import org.eclipse.ecf.raspberrypi.gpio.IGenericPi;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;

public class GenericPiTrackerCustomizer implements
		ServiceTrackerCustomizer<IGenericPi, IGenericPi> {

	private BundleContext context;

	public GenericPiTrackerCustomizer(BundleContext context) {
		this.context = context;
	}

	@Override
	public IGenericPi addingService(ServiceReference<IGenericPi> reference) {

		return null;
	}

	@Override
	public void modifiedService(ServiceReference<IGenericPi> pReference,
			IGenericPi pService) {
	}

	@Override
	public void removedService(ServiceReference<IGenericPi> reference,
			IGenericPi service) {
	}

	public void close() {
	}
}
