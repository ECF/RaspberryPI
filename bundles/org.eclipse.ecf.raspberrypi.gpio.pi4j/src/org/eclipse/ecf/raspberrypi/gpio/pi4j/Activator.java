package org.eclipse.ecf.raspberrypi.gpio.pi4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinOutput;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Activator implements BundleActivator {

	private static final String[] PINS = System
			.getProperty("ecf.raspberrypi.gpio.pi4j.pins", "0").trim()
			.split("\\s*,\\s*");
	private static final List<String> PINS_EXPORT = Arrays.asList(System
			.getProperty("ecf.raspberrypi.gpio.pi4j.pins.export", "0").trim()
			.split("\\s*,\\s*"));
	private static final String ECF_EXPORTED_ASYNC = "ecf.exported.async.interfaces";

	private static BundleContext context;
	private GpioController gpio;
	private List<ServiceRegistration<IGPIOPinOutput>> pinRegistrations = new ArrayList<ServiceRegistration<IGPIOPinOutput>>();

	@Override
	public void start(BundleContext ctxt) throws Exception {
		context = ctxt;
		// create gpio controller
		gpio = GpioFactory.getInstance();
		// For all pins specified in PINS system property (see PINS above)
		// Create and register a new GPIOPin instance
		for (String pinStr : PINS)
			createAndRegisterPin(new Integer(pinStr).intValue());
	}

	private void createAndRegisterPin(int pinId) {
		Hashtable<String, Object> pinProps = createPinProps(pinId);
		// Finally register as instance of IGPIOPin service.
		// If the pin has been exported, then pinProps will
		// contain the OSGi remote services export properties,
		// and will be exported.
		ServiceRegistration<IGPIOPinOutput> reg = context.registerService(
				IGPIOPinOutput.class,
				new GPIOPin(gpio.provisionDigitalOutputPin(getPin(pinId),
						String.valueOf(pinId), PinState.LOW)), pinProps);
		pinRegistrations.add(reg);
	}

	private Hashtable<String, Object> createPinProps(int pinId) {
		Hashtable<String, Object> pinProps = new Hashtable<String, Object>();
		String name = String.valueOf(pinId);
		// Use the String name of the pinId for the PIN_ID_PROP
		pinProps.put(IGPIOPinOutput.PIN_ID_PROP, name);
		// Also use it as the name
		pinProps.put(IGPIOPinOutput.PIN_NAME_PROP, name);
		// Set the default state to FALSE/off
		pinProps.put(IGPIOPinOutput.PIN_DEFAULTSTATE_PROP, Boolean.FALSE);
		
		// If this pin is specified as one to export
		if (PINS_EXPORT.contains(String.valueOf(pinId))) {
			// export all interfaces (IGPIOPin)
			pinProps.put(Constants.SERVICE_EXPORTED_INTERFACES, "*");
			Properties props = System.getProperties();
			// See if service.exported.configs is specified in
			// properties
			String config = props
					.getProperty(Constants.SERVICE_EXPORTED_CONFIGS);
			// If it is then
			if (config != null) {
				// put in pinProps
				pinProps.put(Constants.SERVICE_EXPORTED_CONFIGS, config);
				String configProps = config + ".";
				// Go through all props and if the prop
				// starts with config then add it to pinProps
				// or if it is the ECF_EXPORTED_ASYNC then
				// also add it to pinProps.
				for (Object k : props.keySet()) {
					if (k instanceof String) {
						String key = (String) k;
						if (key.startsWith(configProps)
								|| key.equals(ECF_EXPORTED_ASYNC))
							pinProps.put(key, props.getProperty(key));
					}
				}
			}
		}
		return pinProps;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		for (ServiceRegistration<IGPIOPinOutput> reg : pinRegistrations)
			reg.unregister();
		pinRegistrations.clear();

		if (gpio != null) {
			gpio.shutdown();
			gpio = null;
		}

		context = null;
	}

	private Pin getPin(int pinId) {
		switch (pinId) {
		case 0:
			return RaspiPin.GPIO_00;
		case 1:
			return RaspiPin.GPIO_01;
		case 2:
			return RaspiPin.GPIO_02;
		case 3:
			return RaspiPin.GPIO_03;
		case 4:
			return RaspiPin.GPIO_04;
		case 5:
			return RaspiPin.GPIO_05;
		case 6:
			return RaspiPin.GPIO_06;
		case 7:
			return RaspiPin.GPIO_07;
		case 8:
			return RaspiPin.GPIO_08;
		case 9:
			return RaspiPin.GPIO_09;
		case 10:
			return RaspiPin.GPIO_10;
		case 11:
			return RaspiPin.GPIO_11;
		case 12:
			return RaspiPin.GPIO_12;
		case 13:
			return RaspiPin.GPIO_13;
		case 14:
			return RaspiPin.GPIO_14;
		case 15:
			return RaspiPin.GPIO_15;
		case 16:
			return RaspiPin.GPIO_16;
		case 17:
			return RaspiPin.GPIO_17;
		case 18:
			return RaspiPin.GPIO_18;
		case 19:
			return RaspiPin.GPIO_19;
		case 20:
			return RaspiPin.GPIO_20;
		default:
			return null;
		}
	}

}
