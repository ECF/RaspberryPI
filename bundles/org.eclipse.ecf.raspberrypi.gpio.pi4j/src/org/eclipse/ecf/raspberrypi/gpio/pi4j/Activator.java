package org.eclipse.ecf.raspberrypi.gpio.pi4j;

import java.util.Hashtable;

import org.eclipse.ecf.raspberrypi.gpio.IGPIOPin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private GpioController gpio;
	
	@Override
	public void start(BundleContext ctxt) throws Exception {
		context = ctxt;
        // create gpio controller
        gpio = GpioFactory.getInstance();
        createAndRegisterPins();
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
	
	private void createAndRegisterPin(int pinId) {
        Hashtable<String,Object> pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, pinId);
        String name = String.valueOf(pinId);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, name);
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.FALSE);
        Pin pin = getPin(pinId);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(pin, name, PinState.LOW)), pinProps);
	}
	
	private void createAndRegisterPins() {
		
		String pinsProp = System.getProperty("ecf.gpio.pi4j.pins","0");
		if (pinsProp != null) {
			// Parse the pinsProp
			String[] pinStrs = pinsProp.trim().split("\\s*,\\s*");
			for(String pinStr: pinStrs) 
				createAndRegisterPin(new Integer(pinStr).intValue());
		}
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		if (gpio != null) {
			gpio.shutdown();
			gpio = null;
		}
		context = null;
	}

}
