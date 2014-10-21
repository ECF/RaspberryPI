// from: http://www.lediouris.net/RaspberryPI/ADC/readme.html
package org.eclipse.ecf.raspberrypi.gpio.pi4j.adc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.eclipse.ecf.raspberrypi.gpio.IGenericPi;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

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

	private ServiceRegistration<IGenericPi> fServiceRegistration;

	/**
	 * Creates the converter with default values.
	 */
	public MCP3008() {
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

	public void startService(BundleContext context) {
		Dictionary<String, Object> props = createProperties();
		registerService(context, props);
	}
	
	public void stopService() {
		fServiceRegistration.unregister();
	}

	private void registerService(BundleContext context,
			Dictionary<String, Object> props) {
		fServiceRegistration = context.registerService(IGenericPi.class,
				new MCP3008Service(this), props);
	}

	private Dictionary<String, Object> createProperties() {
		// Setup properties for export using the ecf generic server
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put("service.exported.interfaces", "*");
		props.put("service.exported.configs", "ecf.generic.server");
		props.put("ecf.generic.server.port", "3288");
		try {
			props.put("ecf.generic.server.hostname", InetAddress.getLocalHost()
					.getHostAddress());
		} catch (UnknownHostException e) {
		}
		props.put("ecf.exported.async.interfaces", "*");
		Properties systemProps = System.getProperties();
		for (Object pn : systemProps.keySet()) {
			String propName = (String) pn;
			if (propName.startsWith("service.") || propName.startsWith("ecf."))
				props.put(propName, systemProps.get(propName));
		}
		return props;
	}
}
