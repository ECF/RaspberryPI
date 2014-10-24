/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio.pin1;

import org.osgi.framework.BundleContext;

public class Activator extends org.eclipse.ecf.raspberrypi.gpio.pin0.Activator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}
	
	@Override
	public void start(BundleContext pBundleContext) throws Exception {
		Activator.context = pBundleContext;
		registerPin();
	}
	
	protected int getPinNumber(){
		return 1;
	}
}
