package org.eclipse.ecf.raspberrypi.test.gpio;

import org.eclipse.ecf.raspberrypi.gpio.GPIOPinInputEvent;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPin;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinInputListener;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinOutput;
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

	private ServiceTracker<IGPIOPinOutput,IGPIOPinOutput> pinTracker;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		pinTracker = new ServiceTracker<IGPIOPinOutput,IGPIOPinOutput>(context, IGPIOPinOutput.class, new ServiceTrackerCustomizer<IGPIOPinOutput,IGPIOPinOutput>() {

			@Override
			public IGPIOPinOutput addingService(ServiceReference<IGPIOPinOutput> reference) {
				System.out.println("Adding GPIO Pin Output service.   id="+reference.getProperty(IGPIOPinOutput.PIN_ID_PROP));
				IGPIOPinOutput pin = context.getService(reference);
				System.out.println("  current pin state is "+(pin.getState()?"HIGH":"LOW"));
				System.out.println("  setting state to HIGH");
				pin.setState(true);
				return pin;
			}

			@Override
			public void modifiedService(ServiceReference<IGPIOPinOutput> reference,
					IGPIOPinOutput service) {
			}

			@Override
			public void removedService(ServiceReference<IGPIOPinOutput> reference,
					IGPIOPinOutput service) {
				System.out.println("Removing GPIO Pin service. id="+reference.getProperty(IGPIOPinOutput.PIN_ID_PROP));
				System.out.println("  setting state to LOW");
				service.setState(false);
			}
		});
		pinTracker.open();
		
		// create and register IGPIOPinInputListener on IGPIOPin.DEFAULT_INPUT_PIN (2)
		context.registerService(IGPIOPinInputListener.class,new TestGPIOPinInputListener(),IGPIOPin.Util.createInputListenerProps(IGPIOPin.DEFAULT_INPUT_PIN));
		
	}

	class TestGPIOPinInputListener implements IGPIOPinInputListener {
		@Override
		public void handleInputEvent(GPIOPinInputEvent event) {
			System.out.println("TestGPIOPinInputListener.handleInputEvent(event="+event+")");
		}
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
