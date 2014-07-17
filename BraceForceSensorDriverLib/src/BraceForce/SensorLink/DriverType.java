package BraceForce.SensorLink;


/**
 * Ported from ODK from University of Washington
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public interface DriverType {
	
	String getSensorType();
	
	String getSensorPackageName();
	
	String getSensorDriverAddress();		
	
	CommunicationChannelType getCommunicationChannelType();
	
	
}
