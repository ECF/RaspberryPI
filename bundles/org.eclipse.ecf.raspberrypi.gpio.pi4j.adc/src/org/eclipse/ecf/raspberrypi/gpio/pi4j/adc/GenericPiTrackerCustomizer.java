/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio.pi4j.adc;

import org.eclipse.ecf.raspberrypi.gpio.IGenericPi;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class GenericPiTrackerCustomizer implements
		ServiceTrackerCustomizer<IGenericPi, IGenericPi> {

	private BundleContext context;

	public GenericPiTrackerCustomizer(BundleContext context) {
		this.context = context;
	}

	@Override
	public IGenericPi addingService(ServiceReference<IGenericPi> reference) {
		System.out.println("Service found.");
		return context.getService(reference);
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
