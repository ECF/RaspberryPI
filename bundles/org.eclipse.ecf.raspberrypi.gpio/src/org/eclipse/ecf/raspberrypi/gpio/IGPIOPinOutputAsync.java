/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio;

import java.util.concurrent.CompletableFuture;

public interface IGPIOPinOutputAsync {

	public CompletableFuture<Boolean> getStateAsync();

	public CompletableFuture<Void> setStateAsync(boolean value);

	public CompletableFuture<Boolean> toggleAsync();

	public CompletableFuture<Void> pulseAsync(long duration, boolean pulseState);

	public CompletableFuture<Void> blinkAsync(long delay, long duration,
			boolean blinkeState);

}
