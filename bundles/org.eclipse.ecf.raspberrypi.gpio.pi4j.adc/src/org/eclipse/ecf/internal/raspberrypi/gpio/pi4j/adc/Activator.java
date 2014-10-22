/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.raspberrypi.gpio.pi4j.adc;

import org.eclipse.ecf.raspberrypi.gpio.ILM35;
import org.eclipse.ecf.raspberrypi.gpio.pi4j.adc.MCP3008;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private ServiceTracker<ILM35, ILM35> serviceTracker;
	private MCP3008 fMCP3008;

	@Override
	public void start(BundleContext ctxt) throws Exception {
		fMCP3008 = new MCP3008();
		fMCP3008.start(ctxt);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		fMCP3008.stop(context);
		fMCP3008 = null;
		context = null;
	}
}
