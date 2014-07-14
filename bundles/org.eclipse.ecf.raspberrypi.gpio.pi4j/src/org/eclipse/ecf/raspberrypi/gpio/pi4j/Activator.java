package org.eclipse.ecf.raspberrypi.gpio.pi4j;

import java.util.Hashtable;

import org.eclipse.ecf.raspberrypi.gpio.IGPIOPin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
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

	private void createAndRegisterPins() {
        // provision gpio pin #01 as an output pin and set 
        Hashtable<String,Object> pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 1);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "1");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "1", PinState.HIGH)), pinProps);
        
        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 2);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "2");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "2", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 3);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "3");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "3", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 4);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "4");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "4", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 5);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "5");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "5", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 6);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "6");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "6", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 7);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "7");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "7", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 8);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "8");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08, "8", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 9);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "9");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09, "9", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 10);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "10");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10, "10", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 11);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "11");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_11, "11", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 12);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "12");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, "12", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 13);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "13");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_13, "13", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 14);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "14");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14, "14", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 15);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "15");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_15, "15", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 16);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "16");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_16, "16", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 17);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "17");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_17, "17", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 18);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "18");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_18, "18", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 19);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "19");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_19, "19", PinState.HIGH)), pinProps);

        pinProps = new Hashtable<String,Object>();
        pinProps.put(IGPIOPin.PIN_ID_PROP, 20);
        pinProps.put(IGPIOPin.PIN_NAME_PROP, "20");
        pinProps.put(IGPIOPin.PIN_DEFAULTSTATE_PROP, Boolean.TRUE);
        context.registerService(new String[] { IGPIOPin.class.getName() }, new GPIOPin(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_20, "20", PinState.HIGH)), pinProps);

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
