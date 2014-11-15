/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 *               Wim Jongman - Refactoring
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio.pin1;

import org.eclipse.ecf.raspberrypi.gpio.pi4j.AbstractPinManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractPinManager implements BundleActivator {

	@Override
	public void start(BundleContext pBundleContext) throws Exception {
		((AbstractPinManager) this).setup(pBundleContext);
	}

	@Override
	public void stop(BundleContext pBundleContext) throws Exception {
		((AbstractPinManager) this).dispose();
	}

	@Override
	public int getPinNumber() {
		return 1;
	}

}
