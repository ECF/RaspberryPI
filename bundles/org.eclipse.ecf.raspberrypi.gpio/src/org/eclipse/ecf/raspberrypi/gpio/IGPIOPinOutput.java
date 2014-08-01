/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio;

public interface IGPIOPinOutput extends IGPIOPin {

	public boolean getState();

	public void setState(boolean value);

	public boolean toggle();

	public void pulse(long duration, boolean pulseState);

	public void blink(long delay, long duration, boolean blinkState);

}
