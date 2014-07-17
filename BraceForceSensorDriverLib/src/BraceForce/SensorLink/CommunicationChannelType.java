package BraceForce.SensorLink;


/**
 * 
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public enum CommunicationChannelType {
	USB("USB"),
	BLUETOOTH("BLUETOOTH"),
	BUILTIN("BUILTIN"),
	NFC("NFC"),
	WIFI("WIFI"),
	DUMMY("DUMMY");
	
	
	
	private String commChannelTypeString;
		
	private CommunicationChannelType(String channelStr) {
		commChannelTypeString = channelStr;
	}
	
	public String getCommChannelTypeString() {
		return commChannelTypeString;
	}
	
	public static CommunicationChannelType getCommChannelTypeByName(String name) {
		CommunicationChannelType commType = null;
		for(CommunicationChannelType type : CommunicationChannelType.values()) {
			if(type.getCommChannelTypeString().equals(name)) {
				commType = type;
			}
		}
		return commType;
	}
}
