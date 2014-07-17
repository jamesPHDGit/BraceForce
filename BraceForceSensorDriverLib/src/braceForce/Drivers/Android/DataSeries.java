package braceForce.Drivers.Android;

public class DataSeries {
	public static final String SENSOR_ID = "sensorid";
	public static final String NUM_SAMPLES = "numSamples";
	public static final String SERIES_TIMESTAMP = "series-timestamp";
	public static final String DATA_AS_CSV = "csvData";
	public static final String SENSOR_TYPE = "sensortype";
	public static final String MSG_TYPE = "msgtype";
	public static final String SAMPLE = "sample";
	
	//msg types handled by sensors 
	public static byte CONFIGURE_SENSOR = 0x1;
	public static byte START_SENSOR = 0x2;
	public static byte STOP_SENSOR = 0x3;
}
