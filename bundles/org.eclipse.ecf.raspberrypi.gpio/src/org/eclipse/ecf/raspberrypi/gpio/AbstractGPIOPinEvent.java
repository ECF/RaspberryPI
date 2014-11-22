/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio;

import java.io.Serializable;

public abstract class AbstractGPIOPinEvent implements Serializable {
	private static final long serialVersionUID = 6733232691622858769L;

	private final int pinId;

	private Object fData;

	public AbstractGPIOPinEvent(int pinId) {
		this.pinId = pinId;
	}

	public int getPinId() {
		return pinId;
	}

	public void setData(Object pData) {
		this.fData = pData;
	}

	public Object getData() {
		return fData;
	}
}
