/*******************************************************************************
 * Copyright (c) 2014 Remain B.V. All rights reserved. 
 * This program and the accompanying materials are made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Wim Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio;

/**
 * A generic data swapper. You set up a service with this class under a specific
 * service property agreed with the remote.
 * 
 * @author Wim Jongman
 *
 */
public interface IGenericPi {

	/**
	 * Sets data that the other end can understand.
	 * 
	 * @param data
	 */
	public void setData(Object data);

	/**
	 * Gets data from the other end that you understand.
	 * 
	 * @return data
	 */
	public Object getData();

}
