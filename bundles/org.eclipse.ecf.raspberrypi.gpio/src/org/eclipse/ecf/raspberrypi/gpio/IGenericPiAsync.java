/*******************************************************************************
 * Copyright (c) 2014 Remain B.V. All rights reserved. 
 * This program and the accompanying materials are made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Wim Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio;

import java.util.concurrent.CompletableFuture;

/**
 * An async version of {@link IGenericPi}.
 * 
 * @author Wim Jongman
 * @see IGenericPi
 *
 */
public interface IGenericPiAsync {

	/**
	 * @param data
	 * @see IGenericPi#setData(Object)
	 */
	public CompletableFuture<Void> setDataAsync(Object data);

	/**
	 * @return data
	 * @see IGenericPi#getData(Object)
	 */
	public CompletableFuture<Object> getDataAsync();

}
