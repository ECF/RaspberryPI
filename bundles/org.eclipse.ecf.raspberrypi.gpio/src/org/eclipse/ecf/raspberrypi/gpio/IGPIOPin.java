/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.ServiceReference;

public interface IGPIOPin {
	public static final int PIN_00 = 0;
	public static final int PIN_01 = 1;
	public static final int PIN_02 = 2;
	public static final int PIN_03 = 3;
	public static final int PIN_04 = 4;
	public static final int PIN_05 = 5;
	public static final int PIN_06 = 6;
	public static final int PIN_07 = 7;
	public static final int PIN_08 = 8;
	public static final int PIN_09 = 9;
	public static final int PIN_10 = 10;
	public static final int PIN_11 = 11;
	public static final int PIN_12 = 12;
	public static final int PIN_13 = 13;
	public static final int PIN_14 = 14;
	public static final int PIN_15 = 15;
	public static final int PIN_16 = 16;
	public static final int PIN_17 = 17;
	public static final int PIN_18 = 18;
	public static final int PIN_19 = 19;
	public static final int PIN_20 = 20;
	
	public static final int DEFAULT_OUTPUT_PIN = PIN_00;
	public static final int DEFAULT_INPUT_PIN = PIN_02;

	// Expected type of values is: *String* "0" - "20"
	public static final String PIN_ID_PROP = "org.eclipse.ecf.raspberrypi.gpio.pin.id";
	// Expected type of values is: *String*
	public static final String PIN_NAME_PROP = "org.eclipse.ecf.raspberrypi.gpio.pin.name";
	// Type of value is: *String* "true" or "false"
	public static final String PIN_OUTPUTSTATE_PROP = "org.eclipse.ecf.raspberrypi.gpio.pin.outputstate";
	// Type of value is: *String" "0"==OFF,"1"==PULL_DOWN,"2"==PULL_UP
	public static final String PIN_INPUTPULLRESISTANCE_PROP = "org.eclipse.ecf.raspberrypi.gpio.pin.inputpullresistance";

	public static final int PIN_INPUTPULLRESISTANCE_OFF = 0;
	public static final int PIN_INPUTPULLRESISTANCE_PULL_DOWN = 1;
	public static final int PIN_INPUTPULLRESISTANCE_PULL_UP = 2;
	
	public static final int PIN_DEFAULTINPUTPULLRESISTANCE = PIN_INPUTPULLRESISTANCE_PULL_DOWN;

	public static class Util {
		public static String convertPinId(int pinId) {
			return String.valueOf(pinId);
		}

		public static Integer convertPinId(String pinId) {
			return Integer.getInteger(pinId);
		}

		public static String convertOutputState(boolean state) {
			return String.valueOf(state);
		}

		public static Boolean convertOutputState(String state) {
			return Boolean.getBoolean(state);
		}

		public static Integer convertInputPullResistance(String pullResistance) {
			Integer pr = Integer.getInteger(pullResistance);
			if (pr != null) {
				int prVal = pr.intValue();
				if (prVal < 0 || prVal > 2)
					return null;
			}
			return pr;
		}

		public static String convertInputPullResistance(int pullResistance) {
			if (pullResistance < 0 || pullResistance > 2)
				return null;
			return String.valueOf(pullResistance);
		}

		private static Object getValue(Map<String, ?> props, String key) {
			return props.get(key);
		}

		private static Object getValue(ServiceReference<?> sr, String key) {
			return sr.getProperty(key);
		}

		private static Integer getPinId(Object o) {
			if (o instanceof String)
				return convertPinId((String) o);
			return null;
		}

		public static Integer getPinId(Map<String, ?> props) {
			return getPinId(getValue(props, PIN_ID_PROP));
		}

		public static Integer getPinId(ServiceReference<?> sr) {
			return getPinId(getValue(sr, PIN_ID_PROP));
		}

		public static void setPinId(Map<String, Object> props, int pinId) {
			props.put(PIN_ID_PROP, convertPinId(pinId));
		}

		private static String getPinName(Object o) {
			if (o instanceof String)
				return (String) o;
			return null;
		}

		public static String getPinName(Map<String, ?> props) {
			return getPinName(getValue(props, PIN_NAME_PROP));
		}

		public static String getPinName(ServiceReference<?> sr) {
			return getPinName(getValue(sr, PIN_NAME_PROP));
		}

		public static void setPinName(Map<String, Object> props, String name) {
			props.put(PIN_NAME_PROP, name);
		}

		private static Boolean getOutputState(Object o) {
			if (o instanceof String)
				return convertOutputState((String) o);
			return null;
		}

		public static Boolean getOutputState(Map<String, ?> props) {
			return getOutputState(getValue(props, PIN_OUTPUTSTATE_PROP));
		}

		public static Boolean getOutputState(ServiceReference<?> sr) {
			return getOutputState(getValue(sr, PIN_OUTPUTSTATE_PROP));
		}

		public static void setOutputState(Map<String, Object> props,
				boolean state) {
			props.put(PIN_OUTPUTSTATE_PROP, convertOutputState(state));
		}

		private static Integer getInputPullResistance(Object o) {
			if (o instanceof String)
				return convertInputPullResistance((String) o);
			return null;
		}

		public static Integer getInputPullResistance(Map<String, ?> props) {
			return getInputPullResistance(getValue(props,
					PIN_INPUTPULLRESISTANCE_PROP));
		}

		public static Integer getInputPullResistance(ServiceReference<?> sr) {
			return getInputPullResistance(getValue(sr,
					PIN_INPUTPULLRESISTANCE_PROP));
		}

		public static void setInputPullResistance(Map<String, Object> props,
				int pullResistance) {
			String pr = convertInputPullResistance(pullResistance);
			if (pr == null)
				throw new IllegalArgumentException(
						"Invalid value for pullResistance=" + pullResistance
								+ ".  Must be 0, 1, 2");
			props.put(PIN_INPUTPULLRESISTANCE_PROP, pr);
		}

		public static Hashtable<String, Object> createOutputPinProps(int pinId,
				String pinName, boolean outputState) {
			Hashtable<String, Object> props = new Hashtable<String, Object>();
			// Use the String name of the pinId for the PIN_ID_PROP
			setPinId(props, pinId);
			// Also use it as the default name
			setPinName(props, (pinName == null) ? String.valueOf(pinId)
					: pinName);
			// Set the default state to FALSE/off
			setOutputState(props, outputState);
			return props;
		}

		public static Hashtable<String, Object> createOutputPinProps(int pinId,
				String pinName) {
			return createOutputPinProps(pinId, pinName, false);
		}

		public static Hashtable<String, Object> createOutputPinProps(int pinId) {
			return createOutputPinProps(pinId, null);
		}

		public static Hashtable<String, Object> createInputListenerProps(
				int pinId, String pinName, int pullResistance) {
			Hashtable<String, Object> props = new Hashtable<String, Object>();
			// Use the String name of the pinId for the PIN_ID_PROP
			setPinId(props, pinId);
			// Also use it as the default name
			setPinName(props, (pinName == null) ? String.valueOf(pinId)
					: pinName);
			setInputPullResistance(props, pullResistance);
			return props;
		}

		public static Hashtable<String, Object> createInputListenerProps(
				int pinId, String pinName) {
			return createInputListenerProps(pinId, pinName, 1);
		}

		public static Hashtable<String, Object> createInputListenerProps(
				int pinId) {
			return createInputListenerProps(pinId, null);
		}

	}
}
