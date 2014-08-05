/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.test.gpio;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.ecf.raspberrypi.gpio.GPIOPinInputEvent;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPin;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinInputListener;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinOutput;
import org.eclipse.ecf.raspberrypi.gpio.pi4j.Pi4jGPIOPinOutput;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private ServiceTracker<IGPIOPinOutput, IGPIOPinOutput> pinTracker;
	private ServiceRegistration<IGPIOPinOutput> reg;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		// Setup properties for export using the ecf generic server
		Map<String, Object> pinProps = new HashMap<String, Object>();
		pinProps.put("service.exported.interfaces", "*");
		pinProps.put("service.exported.configs", "ecf.generic.server");
		pinProps.put("ecf.generic.server.port", "3288");
		pinProps.put("ecf.generic.server.hostname",InetAddress.getLocalHost().getHostAddress());
		pinProps.put("ecf.exported.async.interfaces", "*");
		Properties systemProps = System.getProperties();
		for(Object pn: systemProps.keySet()) {
			String propName = (String) pn;
			if (propName.startsWith("service.") || propName.startsWith("ecf.")) 
				pinProps.put(propName,systemProps.get(propName));
		}

		// register GPIOPin 0 with the above export properties
		reg = Pi4jGPIOPinOutput.registerGPIOPinOutput(0, pinProps, context);

		// Create tracker to print out information from registration above
		pinTracker = new ServiceTracker<IGPIOPinOutput, IGPIOPinOutput>(
				context, IGPIOPinOutput.class,
				new ServiceTrackerCustomizer<IGPIOPinOutput, IGPIOPinOutput>() {

					@Override
					public IGPIOPinOutput addingService(
							ServiceReference<IGPIOPinOutput> reference) {
						System.out.println("Adding GPIO Pin Output service.   id="
								+ reference
										.getProperty(IGPIOPinOutput.PIN_ID_PROP));
						IGPIOPinOutput pin = context.getService(reference);
						System.out.println("  current pin state is "
								+ (pin.getState() ? "HIGH" : "LOW"));
						System.out.println("  setting state to HIGH");
						pin.setState(true);
						return pin;
					}

					@Override
					public void modifiedService(
							ServiceReference<IGPIOPinOutput> reference,
							IGPIOPinOutput service) {
					}

					@Override
					public void removedService(
							ServiceReference<IGPIOPinOutput> reference,
							IGPIOPinOutput service) {
						System.out.println("Removing GPIO Pin service. id="
								+ reference
										.getProperty(IGPIOPinOutput.PIN_ID_PROP));
						System.out.println("  setting state to LOW");
						service.setState(false);
					}
				});
		pinTracker.open();

		// create and register IGPIOPinInputListener on
		// IGPIOPin.DEFAULT_INPUT_PIN (2)
		context.registerService(IGPIOPinInputListener.class,
				new TestGPIOPinInputListener(), IGPIOPin.Util
						.createInputListenerProps(IGPIOPin.DEFAULT_INPUT_PIN));

	}

	class TestGPIOPinInputListener implements IGPIOPinInputListener {
		@Override
		public void handleInputEvent(GPIOPinInputEvent event) {
			System.out
					.println("TestGPIOPinInputListener.handleInputEvent(event="
							+ event + ")");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		if (pinTracker != null) {
			pinTracker.close();
			pinTracker = null;
		}
		if (reg != null) {
			reg.unregister();
			reg = null;
		}
	}

}
