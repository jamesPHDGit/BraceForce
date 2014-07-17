package BraceForce.SensorLink.USB;

import BraceForce.SensorLink.Android.USB.AndroidUSBCommon;
import android.os.Bundle;

public class USBUtil {

		public static int convertNibble(int i){
			//debug.d("Converting a " + i);
			if((char) i >= 'a' &&
					(char) i <= 'f'){
					i = (byte)(((char)i - 'a') + 10);
				} else if((char) i >= 'A' &&
					(char) i <= 'F'){
					i = (byte)(((char)i - 'A') + 10);
				} else if((char) i >= '0' &&
					(char) i <= '9'){
					i = (byte)((char)i - '0');
				} else {
					i = (byte) 0x0;
				}
			//debug.d(" into a " + i);
			return i;
		}
		
	    public static int byteToIntUnsigned(byte toConvert){
	    	int toReturn = (int) (toConvert & 0x7F);
	    	if((int) toConvert < 0){
				toReturn = 128 + toReturn;
			}
	    	return toReturn;
		}
	    
	    //TODO: parameter checking
	    public static long twoBytesToLongUnsigned(byte highByte, byte lowByte){
	    	long toReturn = 0;
	    	
	    	toReturn = byteToIntUnsigned(highByte) << 8;
	    	toReturn = toReturn | byteToIntUnsigned(lowByte);
	    	
	    	return toReturn;
	    }
	    
	    public static int composeInt(int upper, int lower){
	    	int toReturn = upper << 8;
			toReturn += lower;
			
	    	return toReturn;
	    }
	    
	    // expects upper in [0] and lower in [1], i.e. [MSB][LSB], 0x0007 = [00000000][00000111]
	    public static int getIntFromTwoBytesBigEndian(byte[] source){
	    	return (source.length == 2 ?
	    			composeInt(byteToIntUnsigned(source[0]),byteToIntUnsigned(source[1])) :
	    			-1);
	    }
	    
	    // expects lower in [0] and upper in [1], i.e. [LSB][MSB], 0x0007 = [00000111][00000000]
	    public static int getIntFromTwoBytesLittleEndian(byte[] source){
	    	return (source.length == 2 ?
	    			composeInt(byteToIntUnsigned(source[1]),byteToIntUnsigned(source[0])) :
	    			-1);
	    }
	    
	    public static byte[] getTwoBytesFromInt(int toSplit){
	    	byte[] toReturn = new byte[2];
	    	toReturn[0] = (byte)(toSplit >> 8); 
	    	toReturn[1] = (byte)(toSplit);
	    	return toReturn;
	    }
	    
		public static byte orBytes(byte one, byte two){
			return (byte) (one | two);
		}
		
		public static byte xorBytes(byte one, byte two){
			return (byte) (one ^ two);
		}
	    
		public static byte orWithAll(byte target, byte[] toOr){
			for(byte b : toOr){
				target = AndroidUSBCommon.orBytes(target, b);
			}
			return target;
		}
		
		public static byte xorWithAll(byte target, byte[] toOr){
			for(byte b : toOr){
				target = AndroidUSBCommon.xorBytes(target, b);
			}
			return target;
		}
		
		public static String byte2Hex(byte toConvert){
			return Integer.toHexString(byteToIntUnsigned(toConvert));
		}
		

}
