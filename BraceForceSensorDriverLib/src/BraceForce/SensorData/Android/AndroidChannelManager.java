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
package BraceForce.SensorData.Android;


import java.util.List;

import BraceForce.SensorData.ChannelManager;
import BraceForce.SensorLink.CommunicationChannelType;
import BraceForce.SensorLink.DiscoverableDevice;
import BraceForce.SensorLink.DriverType;
import BraceForce.SensorLink.SensorNotFoundException;
import BraceForce.SensorLink.Android.AndroidSensorLinkManager;

/**
 * 
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public interface AndroidChannelManager extends ChannelManager {
	
	// Set the sensor manager
	public void setSensorManager(AndroidSensorDataManager sensorManager);
	
	
	// Get sensor status
	public AndroidSensorStateMachine getSensorStatus(String id);
	
	
	// List all available sensors
	public List<AndroidSensorLinkManager> getRegisteredSensorList(CommunicationChannelType commType);
		
}