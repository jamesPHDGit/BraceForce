package BraceForce.SensorLink;

/**
 * Ported from ODK from university of washington
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public interface DiscoverableDevice {

	public String getDeviceId();

	public String getDeviceName();
	
	public DiscoverableDeviceState getDeviceState();

	void connectionLost();

	void connectionRestored();
}
