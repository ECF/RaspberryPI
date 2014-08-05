/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.internal.test.gpio.ide.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ecf.raspberrypi.gpio.IGPIOPin;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinOutput;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinOutputAsync;
import org.eclipse.ecf.remoteservice.IRemoteServiceProxy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class RpiGPIOPinView extends ViewPart implements
		ServiceTrackerCustomizer<IGPIOPinOutput, IGPIOPinOutput> {

	// static fields to hold the images
	private static final Image HIGH_IMAGE = Activator
			.getIconImage("icons/high.gif");
	private static final Image LOW_IMAGE = Activator
			.getIconImage("icons/low.gif");
	private static final Image WAITING_IMAGE = Activator
			.getIconImage("icons/waiting.gif");

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.ecf.raspberrypi.internal.test.gpio.ide.views.RpiGPIOPinView";

	public RpiGPIOPinView() {
	}

	// Model class to represent the GPIOPin in this UI
	class GPIOPin {

		private ServiceReference<IGPIOPinOutput> pinReference;
		private IGPIOPinOutput pinOutput;
		private IGPIOPinOutputAsync pinOutputAsync;
		private boolean state;
		private boolean waiting = false;

		public GPIOPin(ServiceReference<IGPIOPinOutput> pinReference,
				IGPIOPinOutput service, boolean initState) {
			this.pinReference = pinReference;
			this.pinOutput = service;
			if (this.pinOutput instanceof IGPIOPinOutputAsync)
				pinOutputAsync = (IGPIOPinOutputAsync) this.pinOutput;
			this.state = initState;
		}

		public String getPinId() {
			Integer i = IGPIOPin.Util.getPinId(this.pinReference);
			return String.valueOf(i.intValue());
		}

		public String getPinName() {
			return IGPIOPin.Util.getPinName(this.pinReference);
		}

		public boolean isHigh() {
			return state;
		}

		public boolean isWaiting() {
			return waiting;
		}

		public void toggle() {
			// Set waiting to true
			waiting = true;
			boolean newState = !this.state;
			// If we have asynchronous access to service,
			// then use it
			if (pinOutputAsync != null) {
				// Set state asynchronously to newState,
				// and when complete change the UI state
				// and refresh the viewer
				pinOutputAsync.setStateAsync(newState).whenComplete(
						(result, exception) -> {
							this.waiting = false;
							if (viewer != null) {
								// No exception means success
								if (exception == null) {
									// Set UI state to newState
									state = newState;
									asyncRefresh();
								} else
									showCommErrorDialog(exception);
							}
						});

			} else {
				// If we do not have access to async service, then
				// call pinOutput synchronously
				pinOutput.setState(newState);
				state = newState;
			}
		}

		public void close() {
			this.pinReference = null;
			this.pinOutput = null;
			this.pinOutputAsync = null;
		}

		public String getServiceID() {
			if (this.pinOutput instanceof IRemoteServiceProxy) {
				IRemoteServiceProxy proxy = (IRemoteServiceProxy) this.pinOutput;
				return proxy.getRemoteServiceReference().getID().getContainerID().getName();
			}
			return null;
		}
	}

	private List<GPIOPin> elements = new ArrayList<GPIOPin>();
	private TableViewer viewer;

	private ServiceTracker<IGPIOPinOutput, IGPIOPinOutput> tracker;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.FULL_SELECTION);

		String[] titles = { "Pin #", "Pin Name", "Pin State (click to toggle)", "Remote Service Container ID" };
		int[] bounds = { 100, 100, 150, 250 };

		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((GPIOPin) element).getPinId();
			}
		});

		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((GPIOPin) element).getPinName();
			}
		});

		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return null;
			}

			@Override
			public Image getImage(Object element) {
				GPIOPin pin = (GPIOPin) element;
				if (pin.isWaiting())
					return WAITING_IMAGE;
				return pin.isHigh() ? HIGH_IMAGE : LOW_IMAGE;
			}
		});
		
		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((GPIOPin) element).getServiceID();
			}
		});

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);

		table.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				TableItem item = table.getItem(pt);
				if (item == null)
					return;
				Rectangle rect = item.getBounds(2);
				if (rect.contains(pt))
					((GPIOPin) item.getData()).toggle();
			}
		});

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(elements);

		// Setup and open service tracker to get IGPIOPinOutput
		// instances
		tracker = new ServiceTracker<IGPIOPinOutput, IGPIOPinOutput>(
				Activator.getContext(), IGPIOPinOutput.class, this);
		tracker.open();
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound,
			final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

	@Override
	public void dispose() {
		viewer = null;
		if (tracker != null) {
			tracker.close();
			tracker = null;
		}
		removePin(null);
		super.dispose();
	}

	@Override
	public IGPIOPinOutput addingService(
			final ServiceReference<IGPIOPinOutput> reference) {
		IGPIOPinOutput service = Activator.getContext().getService(reference);
		if (viewer == null)
			return service;
		if (service instanceof IGPIOPinOutputAsync) {
			// Get the current state
			((IGPIOPinOutputAsync) service).getStateAsync().whenComplete(
					(result, throwable) -> {
						// If we have a non-null result
						// then add the pin
						if (result != null)
							addPin(reference, service, result);
						else
							showCommErrorDialog(throwable);
					});
		} else {
			// If no async available, potentially block using
			// synchronous IGPIOPinOutput.getState()
			try {
				boolean initState = service.getState();
				addPin(reference, service, initState);
			} catch (Throwable t) {
				showCommErrorDialog(t);
			}
		}
		return service;
	}

	@Override
	public void modifiedService(ServiceReference<IGPIOPinOutput> reference,
			IGPIOPinOutput service) {
	}

	@Override
	public void removedService(ServiceReference<IGPIOPinOutput> reference,
			IGPIOPinOutput service) {
		if (viewer == null)
			return;
		removePin(reference);
		asyncRefresh();
	}

	// addPin and removePin methods used in addedService, and removedService
	private void addPin(ServiceReference<IGPIOPinOutput> reference,
			IGPIOPinOutput service, boolean initValue) {
		if (this.viewer == null)
			return;
		synchronized (elements) {
			elements.add(new GPIOPin(reference, service, initValue));
		}
		asyncRefresh();
	}

	private void removePin(ServiceReference<IGPIOPinOutput> reference) {
		synchronized (elements) {
			for (Iterator<GPIOPin> i = elements.iterator(); i.hasNext();) {
				GPIOPin pinUI = i.next();
				if (reference == null || pinUI.pinReference.equals(reference)) {
					i.remove();
					pinUI.close();
				}
			}
		}
	}

	// UI Utility methods
	void asyncExec(Runnable runnable) {
		TableViewer v = this.viewer;
		if (v == null)
			return;
		v.getControl().getDisplay().asyncExec(runnable);
	}

	void asyncRefresh() {
		asyncExec(new Runnable() {
			@Override
			public void run() {
				if (viewer == null)
					return;
				viewer.refresh();
			}
		});
	}

	void showCommErrorDialog(Throwable exception) {
		if (viewer == null)
			return;
		MessageDialog.openError(viewer.getControl().getShell(),
				"GPIO Pin Error",
				"Error accessing GPIO Pin to get initial state.  Message: "
						+ ((exception == null) ? "no exception message"
								: exception.getMessage()));
	}

}