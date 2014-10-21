// from: http://www.lediouris.net/RaspberryPI/ADC/readme.html
package org.eclipse.ecf.raspberrypi.gpio.pi4j.adc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.ecf.raspberrypi.gpio.ILM35;
import org.eclipse.ecf.raspberrypi.gpio.ILM35Async;
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

	public class LM35TrackerCustomizer implements
			ServiceTrackerCustomizer<ILM35, ILM35> {

		private BundleContext context;

		public LM35TrackerCustomizer(BundleContext context) {
			this.context = context;
		}

		@Override
		public ILM35 addingService(ServiceReference<ILM35> reference) {
			System.out.println("LM35 client found.");
			return context.getService(reference);
		}

		@Override
		public void modifiedService(ServiceReference<ILM35> pReference,
				ILM35 pService) {
		}

		@Override
		public void removedService(ServiceReference<ILM35> reference,
				ILM35 service) {
		}

		public void close() {
		}
	}

	TimerTask fTask = new TimerTask() {

		@Override
		public void run() {
			int lastRead = 0;
			int tolerance = 5;
			int adc = readAdc();
			int postAdjust = Math.abs(adc - lastRead);
			if (postAdjust > tolerance) {
				int volume = (int) (adc / 10.23); // [0, 1023] ~ [0x0000,
													// 0x03FF] ~ [0&0,
													// 0&1111111111]
				double volts = (adc * 3.3) / 1024;
				double temperature = volts / (10.0 / 1000);
				System.out.println("Volume:" + volume + "%  Temp:"
						+ temperature + "C");
				lastRead = adc;
				notifyLM35Services(temperature);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	};

	private final static boolean DISPLAY_DIGIT = false;

	// Note: "Mismatch" 23-24. The wiring says DOUT->#23, DIN->#24
	// 23: DOUT on the ADC is IN on the GPIO. ADC:Slave, GPIO:Master
	// 24: DIN on the ADC, OUT on the GPIO. Same reason as above.
	// SPI: Serial Peripheral Interface
	private Pin spiClk = RaspiPin.GPIO_01; // Pin #18, clock
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

	private ServiceTracker<ILM35, ILM35> fTracker;

	/**
	 * Creates the converter with default values.
	 */
	public MCP3008() {
	}

	protected void notifyLM35Services(double temperature) {
		ILM35[] services = fTracker.getServices(new ILM35[0]);
		for (ILM35 service : services) {
			((ILM35Async) service).setTemperatureAsync(getHostName(),
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

	public MCP3008 init() {
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
	 * Initializes the board and sets up a tracker to monitor for ILM35 remote
	 * services.
	 * 
	 * @param ctxt
	 */
	public void start(BundleContext ctxt) {
		init();
		fTracker = new ServiceTracker<>(ctxt, ILM35.class,
				new LM35TrackerCustomizer(ctxt));
		Timer fTimer = new Timer("MCP3008 Polling Thread", true);
		fTimer.scheduleAtFixedRate(fTask, Calendar.getInstance().getTime(), 500);
	}
}
