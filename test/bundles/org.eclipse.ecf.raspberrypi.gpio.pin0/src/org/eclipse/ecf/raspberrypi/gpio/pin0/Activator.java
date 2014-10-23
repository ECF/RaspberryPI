/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 *               Wim Jongman - Refactoring
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio.pin0;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
	private ServiceRegistration<IGPIOPinOutput> fPinReg;

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		setupService();
		setupInputListener();
	}

	/**
	 * Sets up a service that clients can acquire to monitor the value of a pin.
	 */
	private void setupInputListener() {
		// create and register IGPIOPinInputListener on
		// IGPIOPin.DEFAULT_INPUT_PIN (2)
		context.registerService(IGPIOPinInputListener.class,
				new TestGPIOPinInputListener(), IGPIOPin.Util
						.createInputListenerProps(IGPIOPin.DEFAULT_INPUT_PIN));
	}

	/**
	 * Registers a service and starts tracking the pin.
	 * 
	 * @throws UnknownHostException
	 */
	protected void setupService() {
		registerPin();
		registerTracker();
	}

	private void registerTracker() {
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
							IGPIOPinOutput pin) {
						System.out.println("Removing GPIO Pin service. id="
								+ reference
										.getProperty(IGPIOPinOutput.PIN_ID_PROP));
						System.out.println("  setting state to LOW");
						pin.setState(false);
					}
				});
		pinTracker.open();
	}

	public void registerPin() {
		Map<String, Object> pinProps = getDefaultPinProps();
		setPin(Pi4jGPIOPinOutput.registerGPIOPinOutput(0, pinProps,
				getContext()));
	}

	/**
	 * Sets the default remote service properties. Override this method if you
	 * want to add additional properties like so:
	 * 
	 * <pre>
	 *  	protected Map<String, Object> getDefaultPinProps() {
	 *         	Map<String, Object> pinProps = super.getDefaultPinProps();
	 *    		pinProps.put("my.property", "my.value");
	 *        }
	 * </pre>
	 * 
	 * @return the properties.
	 */
	protected Map<String, Object> getDefaultPinProps() {
		// Setup properties for export using the ecf generic server
		Map<String, Object> pinProps = new HashMap<String, Object>();
		pinProps.put("service.exported.interfaces", "*");
		pinProps.put("service.exported.configs", "ecf.generic.server");
		pinProps.put("ecf.generic.server.port", "3288");
		try {
			pinProps.put("ecf.generic.server.hostname", InetAddress
					.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		pinProps.put("ecf.exported.async.interfaces", "*");
		Properties systemProps = System.getProperties();
		for (Object pn : systemProps.keySet()) {
			String propName = (String) pn;
			if (propName.startsWith("service.") || propName.startsWith("ecf."))
				pinProps.put(propName, systemProps.get(propName));
		}
		return pinProps;
	}

	/**
	 * Sets the pin. Do not override.
	 * 
	 * @param pPinReg
	 */
	protected void setPin(ServiceRegistration<IGPIOPinOutput> pPinReg) {
		this.fPinReg = pPinReg;
	}

	/**
	 * Gets the pin. Do not override.
	 * 
	 * @return
	 * 
	 */
	protected ServiceRegistration<IGPIOPinOutput> getPin() {
		return fPinReg;
	}

	class TestGPIOPinInputListener implements IGPIOPinInputListener {
		@Override
		public void handleInputEvent(GPIOPinInputEvent event) {
			System.out
					.println("TestGPIOPinInputListener.handleInputEvent(event="
							+ event + ")");
		}
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		if (pinTracker != null) {
			pinTracker.close();
			pinTracker = null;
		}
		if (fPinReg != null) {
			unRegisterServiceAsync();
			fPinReg = null;
		}
	}

	/**
	 * JmDNS has a bug that can cause the unregistration to fail when network is
	 * instable. Use this method to tuck away that unregistration
	 * 
	 * @param pServiceReg
	 */
	private void unRegisterServiceAsync() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				getPin().unregister();
			}
		});
		thread.setName("Unregistering service (see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=448466)");
		thread.setDaemon(true);
		thread.start();
	}

}
