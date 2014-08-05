/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.internal.test.gpio.ide.views;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.ecf.raspberrypi.test.gpio.ide";
	
	private static BundleContext bundleContext;
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		bundleContext = context;
	}
	
	public static Image getIconImage(String imageFile) {
		return imageDescriptorFromPlugin(PLUGIN_ID,imageFile).createImage();
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		bundleContext = null;
	}

	public static BundleContext getContext() {
		return bundleContext;
	}

}
