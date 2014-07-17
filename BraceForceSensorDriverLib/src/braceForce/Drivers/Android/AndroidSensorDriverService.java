package braceForce.Drivers.Android;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class AndroidSensorDriverService extends Service {

	private IBinder mSensorServiceBinder;

	public static final String TAG = "AndroidSensorDriverService";

	public void onCreate ()
	{
		mSensorServiceBinder = new AndroidSensorDriverServiceImpl(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mSensorServiceBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

}
