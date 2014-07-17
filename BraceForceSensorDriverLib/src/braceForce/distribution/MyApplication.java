package braceForce.distribution;

import android.app.Application;
import android.content.Context;
import android.os.Looper;

public class MyApplication extends Application {
	private static Context context;
	private static boolean initialzied = false;
	
    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
    	if ( !initialzied) {
    		initialzied = true;
    		Looper.prepare();
    	}
    	return MyApplication.context;
    }
}
