package org.eclipse.ecf.raspberrypi.test.gpio;

import org.eclipse.ecf.raspberrypi.gpio.IGPIOPin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private ServiceTracker<IGPIOPin,IGPIOPin> pinTracker;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		pinTracker = new ServiceTracker<IGPIOPin,IGPIOPin>(context, IGPIOPin.class, new ServiceTrackerCustomizer<IGPIOPin,IGPIOPin>() {

			@Override
			public IGPIOPin addingService(ServiceReference<IGPIOPin> reference) {
				IGPIOPin pin = null;
				if (reference != null) {
					System.out.println("Adding GPIO Pin service. ref="+reference);
					pin = context.getService(reference);
				}
				return pin;
			}

			@Override
			public void modifiedService(ServiceReference<IGPIOPin> reference,
					IGPIOPin service) {
			}

			@Override
			public void removedService(ServiceReference<IGPIOPin> reference,
					IGPIOPin service) {
			}
		});
		pinTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		if (pinTracker != null) {
			pinTracker.close();
			pinTracker = null;
		}
	}

}
