/*
 * Copyright (C) 2013 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package BraceForce.Drivers.Android.BuiltInDevices;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import braceForce.Drivers.Android.AndroidSensorDataPacket;
import braceForce.Drivers.Android.AndroidSensorDataParseResponse;
import braceForce.Drivers.Android.AndroidSensorParameter;

import BraceForce.Drivers.SensorDataPacket;
import BraceForce.Drivers.SensorDataResponse;

import android.os.Bundle;

/**
 * 
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public class PressureDriver extends AbstractBuiltinDriver {

	private static final String PRESSURE = "pressure";

	public PressureDriver() {
		sensorParams.add(new AndroidSensorParameter(PRESSURE, AndroidSensorParameter.Type.FLOAT, AndroidSensorParameter.Purpose.DATA, "Ambient air pressure."));
		sensorParams.add(new AndroidSensorParameter(timestamp_param, AndroidSensorParameter.Type.LONG, AndroidSensorParameter.Purpose.TIME, "sensor data timestamp") );
	}
	
	@Override
	public AndroidSensorDataParseResponse getSensorData(long maxNumReadings,
			List<AndroidSensorDataPacket> rawSensorData, byte[] remainingData) {

		List<Bundle> sensorData = new ArrayList<Bundle>();
		for (AndroidSensorDataPacket pkt : rawSensorData) {
			String tmp = new String(pkt.getPayload());
			if (tmp != null) {
				String[] values = tmp.split("\n");
				if (values.length >= 1) {
					Bundle data = new Bundle();
					data.putFloat(PRESSURE, Float.valueOf(values[0]));
					data.putLong(timestamp_param, Long.valueOf(pkt.getTime()));
					sensorData.add(data);
				}
			}
		}
		return new AndroidSensorDataParseResponse(sensorData, remainingData);
	}

	
}