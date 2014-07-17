package BraceForce.SensorData.Android;

import BraceForce.SensorLink.CommunicationChannelType;
import BraceForce.SensorLink.DriverType;

/**
 * 
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public class AndroidDriverTypeImpl implements DriverType {

	private final String driverType;
	private final String packageName;
	private final String driverAddress;
	private final CommunicationChannelType communicationChannelType;
	private final String readingUiIntentStr;
	private final String configUiIntentStr;

	public AndroidDriverTypeImpl(String driverType, String packageName,
			String driverAddress, CommunicationChannelType communicationType, String readingUiIntentStr, String configUiIntentStr) {

		this.driverType = driverType;
		this.packageName = packageName;
		this.driverAddress = driverAddress;
		this.communicationChannelType = communicationType;
		this.readingUiIntentStr = readingUiIntentStr;
		this.configUiIntentStr = configUiIntentStr;
		
	}

	@Override
	public String getSensorType() {
		return driverType;
	}

	@Override
	public String getSensorPackageName() {
		return packageName;
	}

	@Override
	public String getSensorDriverAddress() {
		return driverAddress;
	}

	@Override
	public String toString() {
		return driverType;
	}

	@Override
	public CommunicationChannelType getCommunicationChannelType() {
		return communicationChannelType;
	}



	
}