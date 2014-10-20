// from: http://www.lediouris.net/RaspberryPI/ADC/readme.html
package org.eclipse.ecf.raspberrypi.gpio.pi4j.adc;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Read an Analog to Digital Converter
 */
public class ADCReader {
	private final static boolean DISPLAY_DIGIT = false;
	private final static boolean DEBUG = false;
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

	private static boolean go = true;

	public ADCReader(Pin pSpiClk, Pin pSpiMiso, Pin pSpiMosi, Pin pSpiCs,
			int pChannel) {
		this.spiClk = pSpiClk;
		this.spiMiso = pSpiMiso;
		this.spiMosi = pSpiMosi;
		this.spiCs = pSpiCs;
		this.ADC_CHANNEL = pChannel;
	}

	public void run() {
		GpioController gpio = GpioFactory.getInstance();
		mosiOutput = gpio.provisionDigitalOutputPin(spiMosi, "MOSI",
				PinState.LOW);
		clockOutput = gpio.provisionDigitalOutputPin(spiClk, "CLK",
				PinState.LOW);
		chipSelectOutput = gpio.provisionDigitalOutputPin(spiCs, "CS",
				PinState.LOW);

		misoInput = gpio.provisionDigitalInputPin(spiMiso, "MISO");

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutting down.");
				go = false;
			}
		});
		int lastRead = 0;
		int tolerance = 5;
		while (go) {
			int adc = readAdc();
			int postAdjust = Math.abs(adc - lastRead);
			if (postAdjust > tolerance) {

				int volume = (int) (adc / 10.23); // [0, 1023] ~ [0x0000,
													// 0x03FF] ~ [0&0,
													// 0&1111111111]
				if (DEBUG)
					System.out.println("readAdc:"
							+ Integer.toString(adc)
							+ " (0x"
							+ lpad(Integer.toString(adc, 16).toUpperCase(),
									"0", 2) + ", 0&"
							+ lpad(Integer.toString(adc, 2), "0", 8) + ")");
				System.out.println("Volume:" + volume + "%");
				lastRead = adc;
			}
			try {
				Thread.sleep(100L);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		System.out.println("Bye...");
		gpio.shutdown();
	}

	private  int readAdc() {
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

	private static String lpad(String str, String with, int len) {
		String s = str;
		while (s.length() < len)
			s = with + s;
		return s;
	}
}
