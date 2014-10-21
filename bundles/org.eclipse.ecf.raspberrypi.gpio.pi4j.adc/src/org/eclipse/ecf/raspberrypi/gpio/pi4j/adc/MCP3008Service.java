package org.eclipse.ecf.raspberrypi.gpio.pi4j.adc;

import java.io.Serializable;

import org.eclipse.ecf.raspberrypi.gpio.IGenericPi;

public class MCP3008Service implements IGenericPi, Serializable {

	private static final long serialVersionUID = -1615374525865005297L;

	private MCP3008 fService;

	public MCP3008Service(MCP3008 pService) {
		this.fService = pService;
	}
	
	@Override
	public void setData(Object data) {
	}

	@Override
	public Object getData() {
		return fService.getTemperatureLM35();
	}
}
