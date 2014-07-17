package BraceForce.Drivers;

/**
 * 
 * @author jameszhengxi1979@gmail.com
 * 
 */

public interface SensorDataPacket {
	long getTime();

	byte[] getPayload();
}
