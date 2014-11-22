/*******************************************************************************
 * Copyright (c) 2014 Remain B.V. All rights reserved. 
 * This program and the accompanying materials are made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Wim Jongman - initial API and implementation
 ******************************************************************************/
// copied from / inspired by: http://www.lediouris.net/RaspberryPI/ADC/readme.html
package org.eclipse.ecf.internal.raspberrypi.gpio.pi4j.MCP3008;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.ecf.raspberrypi.gpio.pi4j.adc.IAnalogService;
import org.eclipse.ecf.raspberrypi.gpio.pi4j.adc.IAnalogServiceAsync;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * MPC3008 Analog to Digital Converter
 * <p/>
 * from: http://www.lediouris.net/RaspberryPI/ADC/readme.html
 *
 */
public class MCP3008 {

	private static final int LM35_POLLING_INTERVAL_MS = 100;
	private double fCurrentTemp = 0;

	public class LM35TrackerCustomizer implements
			ServiceTrackerCustomizer<IAnalogService, IAnalogService> {

		private BundleContext context;

		public LM35TrackerCustomizer(BundleContext context) {
			this.context = context;
		}

		@Override
		public IAnalogService addingService(ServiceReference<IAnalogService> reference) {
			System.out.println("IAnalogService client found.");
			IAnalogService service = context.getService(reference);
			service.setValue(getHostName(), "LM35", getCurrentTemp());
			return service;
		}

		@Override
		public void modifiedService(ServiceReference<IAnalogService> pReference,
				IAnalogService pService) {
		}

		@Override
		public void removedService(ServiceReference<IAnalogService> reference,
				IAnalogService service) {
			System.out.println("IAnalogService client lost.");
		}

		public void close() {
		}
	}

	TimerTask fTask = new TimerTask() {

		int tolerance = 5;
		int lastRead = 0;
		boolean run = true;

		@Override
		public void run() {
			while (run) {
				int adc = readAdc();
				int postAdjust = Math.abs(adc - lastRead);
				if (postAdjust > tolerance) {
					double volts = (adc * 3.3) / 1024;
					double temperature = volts / (10.0 / 1000);
					lastRead = adc;
					setCurrentTemp(temperature);
					notifyAnalogClientServices(temperature);
				}
				try {
					Thread.sleep(LM35_POLLING_INTERVAL_MS);
				} catch (InterruptedException e) {
				}
			}
		}

		public boolean cancel() {
			run = false;
			return false;
		};
	};

	private final static boolean DISPLAY_DIGIT = false;

	// Note: "Mismatch" 23-24. The wiring says DOUT->#23, DIN->#24
	// 23: DOUT on the ADC is IN on the GPIO. ADC:Slave, GPIO:Master
	// 24: DIN on the ADC, OUT on the GPIO. Same reason as above.
	// SPI: Serial Peripheral Interface
	private Pin spiClk = RaspiPin.GPIO_03; // Pin #15, clock
	private Pin spiMiso = RaspiPin.GPIO_04; // Pin #23, data in. MISO: Master In
											// Slave Out
	private Pin spiMosi = RaspiPin.GPIO_05; // Pin #24, data out. MOSI: Master
											// Out Slave In
	private Pin spiCs = RaspiPin.GPIO_06; // Pin #25, Chip Select

	private int ADC_CHANNEL = 0; // Between 0 and 7, 8 channels on the MCP3008

	private GpioPinDigitalInput misoInput = null;
	private GpioPinDigitalOutput mosiOutput = null;
	private GpioPinDigitalOutput clockOutput = null;
	private GpioPinDigitalOutput chipSelectOutput = null;

	private ServiceTracker<IAnalogService, IAnalogService> fTracker;

	private Timer fTimer;

	/**
	 * Creates the converter with default values.
	 */
	public MCP3008() {
	}

	protected void notifyAnalogClientServices(double temperature) {
		IAnalogService[] services = fTracker.getServices(new IAnalogService[0]);
		System.out.println("Notyfing " + services.length + " services.");
		for (IAnalogService service : services) {
			((IAnalogServiceAsync) service).setValueAsync(getHostName(), "LM35",
					temperature); // do not wait for result.
		}
	}

	private String getHostName() {
		String hostName = System.getProperties().getProperty(
				"ecf.generic.server.hostname");
		if (hostName == null) {
			try {
				hostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				return "unknown host";
			}
		}
		return hostName;
	}

	public MCP3008(Pin pSpiClk, Pin pSpiMiso, Pin pSpiMosi, Pin pSpiCs,
			int pChannel) {
		this.spiClk = pSpiClk;
		this.spiMiso = pSpiMiso;
		this.spiMosi = pSpiMosi;
		this.spiCs = pSpiCs;
		this.ADC_CHANNEL = pChannel;
	}

	private MCP3008 provionsPins() {
		GpioController gpio = GpioFactory.getInstance();
		mosiOutput = gpio.provisionDigitalOutputPin(spiMosi, "MOSI",
				PinState.LOW);
		clockOutput = gpio.provisionDigitalOutputPin(spiClk, "CLK",
				PinState.LOW);
		chipSelectOutput = gpio.provisionDigitalOutputPin(spiCs, "CS",
				PinState.LOW);
		misoInput = gpio.provisionDigitalInputPin(spiMiso, "MISO");
		return this;
	}

	private void unprovisionPins() {
		GpioController gpio = GpioFactory.getInstance();
		gpio.unprovisionPin(mosiOutput);
		gpio.unprovisionPin(clockOutput);
		gpio.unprovisionPin(chipSelectOutput);
		gpio.unprovisionPin(misoInput);
	}

	/**
	 * If you have hooked up an LM35 you can use this formula.
	 * 
	 * @return
	 */
	public double getTemperatureLM35() {
		int adc = readAdc();
		double volts = (adc * 3.3) / 1024;
		double temperature = volts / (10.0 / 1000);
		System.out.println("Temp:" + temperature + "C");
		return temperature;
	}

	/**
	 * If you have hooked up a potentiometer you can use this formula.
	 * 
	 * @return
	 */
	public double getPotReading() {
		int adc = readAdc();
		int volume = (int) (adc / 10.23);
		System.out.println("Pot Level:" + volume + "%");
		return volume;
	}

	/**
	 * Reads current information from the ADC, a value between and including 0
	 * and 1023.
	 * 
	 * @return
	 */
	public int readAdc() {
		chipSelectOutput.high();

		clockOutput.low();
		chipSelectOutput.low();

		int adccommand = ADC_CHANNEL;
		adccommand |= 0x18; // 0x18: 00011000
		adccommand <<= 3;
		// Send 5 bits: 8 - 3. 8 input channels on the MCP3008.
		for (int i = 0; i < 5; i++) //
		{
			if ((adccommand & 0x80) != 0x0) // 0x80 = 0&10000000
				mosiOutput.high();
			else
				mosiOutput.low();
			adccommand <<= 1;
			clockOutput.high();
			clockOutput.low();
		}

		int adcOut = 0;
		for (int i = 0; i < 12; i++) // Read in one empty bit, one null bit and
										// 10 ADC bits
		{
			clockOutput.high();
			clockOutput.low();
			adcOut <<= 1;

			if (misoInput.isHigh()) {
				// System.out.println("    " + misoInput.getName() +
				// " is high (i:" + i + ")");
				// Shift one bit on the adcOut
				adcOut |= 0x1;
			}
			if (DISPLAY_DIGIT)
				System.out.println("ADCOUT: 0x"
						+ Integer.toString(adcOut, 16).toUpperCase() + ", 0&"
						+ Integer.toString(adcOut, 2).toUpperCase());
		}
		chipSelectOutput.high();

		adcOut >>= 1; // Drop first bit
		return adcOut;
	}

	/**
	 * Initializes the board and sets up a tracker to monitor for IAnalogService remote
	 * services.
	 * 
	 * @param ctxt
	 */
	public void start(BundleContext ctxt) {
		provionsPins();
		fTracker = new ServiceTracker<>(ctxt, IAnalogService.class,
				new LM35TrackerCustomizer(ctxt));
		fTracker.open();
		fTimer = new Timer("MCP3008 Polling Thread", true);
		fTimer.schedule(fTask, 5000);
	}

	/**
	 * Stops monitoring the ADC and closes all trackers.
	 * 
	 * @param ctxt
	 */
	public void stop(BundleContext ctxt) {
		fTracker.close();
		fTask.cancel();
		fTimer.cancel();
		fTimer.purge();
		unprovisionPins();
	}

	private void setCurrentTemp(double pTemp) {
		fCurrentTemp = pTemp;
	}

	public double getCurrentTemp() {
		return fCurrentTemp;
	}
}
