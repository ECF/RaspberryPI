/*******************************************************************************
 * Copyright (c) 2014 Weltevree Beheer BV and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 *               Wim Jongman - Refactoring
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio.pi4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinOutput;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * The AbstractPinManager handles the registration and un-registration of a pin
 * service.
 *
 */
public abstract class AbstractPinManager {

	private ServiceTracker<IGPIOPinOutput, IGPIOPinOutput> fPinTracker;
	private ServiceRegistration<IGPIOPinOutput> fPinReg;
	private BundleContext fBundleContext;

	public void setup(BundleContext pBundleContext) {
		this.fBundleContext = pBundleContext;
		registerPin();
		registerTracker();
	}

	/**
	 * @return the pin number
	 */
	public abstract int getPinNumber();

	private void registerTracker() {
		// Create tracker to print out information from registrations
		fPinTracker = new ServiceTracker<IGPIOPinOutput, IGPIOPinOutput>(
				fBundleContext, IGPIOPinOutput.class,
				new ServiceTrackerCustomizer<IGPIOPinOutput, IGPIOPinOutput>() {

					@Override
					public IGPIOPinOutput addingService(
							ServiceReference<IGPIOPinOutput> reference) {
						IGPIOPinOutput pin = fBundleContext
								.getService(reference);
						if (pin.getPinId() != getPinNumber()) {
							return null;
						}

						System.out.println("Adding GPIO Pin Output service.   id="
								+ reference
										.getProperty(IGPIOPinOutput.PIN_ID_PROP));
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
						System.out.println("  setting state to HIGH");
						pin.setState(true);
					}
				});
		fPinTracker.open();
	}

	/**
	 * Registers the pin defined by {@link #getPinNumber()}.
	 */
	protected void registerPin() {
		Map<String, Object> pinProps = getDefaultPinProps();
		ServiceRegistration<IGPIOPinOutput> pinReg = Pi4jGPIOPinOutput
				.registerGPIOPinOutput(getPinNumber(), pinProps, fBundleContext);
		setPin(pinReg);
	}

	/**
	 * Sets the default remote service properties. Override this method if you
	 * want to add additional properties like so:
	 * 
	 * <pre>
	 * protected Map&lt;String, Object&gt; getDefaultPinProps() {
	 * 	Map&lt;String, Object&gt; pinProps = super.getDefaultPinProps();
	 * 	pinProps.put(&quot;my.property&quot;, &quot;my.value&quot;);
	 * }
	 * </pre>
	 * 
	 * @return the properties.
	 */
	public Map<String, Object> getDefaultPinProps() {
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
	 * Sets the pin registration. Do not override.
	 * 
	 * @param pPinReg
	 */
	protected void setPin(ServiceRegistration<IGPIOPinOutput> pPinReg) {
		this.fPinReg = pPinReg;
	}

	/**
	 * Gets the pin registration. Do not override.
	 * 
	 * @return
	 * 
	 */
	protected ServiceRegistration<IGPIOPinOutput> getPin() {
		return fPinReg;
	}

	/**
	 * You may override this method but be sure to call it.
	 * 
	 * @throws Exception
	 */
	public void dispose() throws Exception {
		if (fPinTracker != null) {
			fPinTracker.close();
			fPinTracker = null;
		}
		if (fPinReg != null) {
			unRegisterServiceAsync();
		}
	}

	/**
	 * JmDNS has a bug that can cause the unregistration to fail when network is
	 * unstable. Use this method to tuck away that unregistration
	 * 
	 * @param pServiceReg
	 */
	private void unRegisterServiceAsync() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (getPin() != null) {
						getPin().unregister();
						setPin(null);
					}
				} catch (Exception e) {
				}
			}
		});
		thread.setName("Unregistering service (see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=448466)");
		thread.setDaemon(true);
		thread.start();
	}
}
	