package org.eclipse.ecf.raspberrypi.gpio.pi4j;

import org.eclipse.ecf.raspberrypi.gpio.IGPIOPin;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

public class GPIOPin implements IGPIOPin {

	private final GpioPinDigitalOutput pinImpl;

	public GPIOPin(GpioPinDigitalOutput pinImpl) {
		this.pinImpl = pinImpl;
	}

	@Override
	public boolean getState() {
		return (this.pinImpl.getState() == PinState.HIGH);
	}

	@Override
	public void setState(boolean value) {
		this.pinImpl.setState(value);
	}

	@Override
	public boolean toggle() {
		this.pinImpl.toggle();
		return getState();
	}

	@Override
	public void pulse(long duration, boolean pulseState) {
		this.pinImpl.pulse(duration, pulseState);
	}

	@Override
	public void blink(long delay, long duration, boolean blinkState) {
		this.pinImpl.blink(delay, duration, blinkState ? PinState.HIGH
				: PinState.LOW);
	}

}
