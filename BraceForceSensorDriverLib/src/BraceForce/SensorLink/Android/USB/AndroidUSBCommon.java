package BraceForce.SensorLink.Android.USB;


import BraceForce.SensorLink.USB.USBUtil;
import android.os.Bundle;

/**
 * Ported from ODK from university of washington
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public class AndroidUSBCommon extends USBUtil {


	public static String textOfBundle(Bundle nextPacket) {
		String toReturn = "";
		
		for(String s : nextPacket.keySet()){
			toReturn += s + " : " + nextPacket.get(s).toString() + "\n";
		}
		
		return toReturn;
	}
	
    
}