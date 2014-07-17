package BraceForce.SensorData.Android;



import java.util.ArrayList;
import java.util.List;

import braceForce.Drivers.Android.ManifestMetadata;

import BraceForce.SensorLink.CommunicationChannelType;
import BraceForce.SensorLink.DriverType;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author wbrunette@gmail.com
 * @author rohitchaudhri@gmail.com
 * 
 */
public class AndroidSensorDriverDiscovery {

	private static final String LOGTAG = "SensorDriverDiscovery";
		
	public static List<DriverType> getAllDriversForChannel(Context context, CommunicationChannelType commChannelType1) {
		
		List<DriverType> drivers = new ArrayList<DriverType>();
		PackageManager pkgManager = context.getPackageManager();
		List<ApplicationInfo> packages = pkgManager.getInstalledApplications(PackageManager.GET_META_DATA);

		for (ApplicationInfo packageInfo : packages) 
		{
			try
			{  
				if(packageInfo.metaData != null ) {  

					Bundle data = packageInfo.metaData; 
					
					String frameworkVersion = data.getString(ManifestMetadata.BRACE_FRAMEWORK_VERSION);
					String driverCommChannel = data.getString(ManifestMetadata.DRIVER_COMMUNICATION_CHANNEL);
					String driverType = data.getString(ManifestMetadata.DRIVER_TYPE);
					String driverPackageName = packageInfo.packageName;
					String driverAddress = data.getString(ManifestMetadata.DRIVER_ADDRESS);
					String readUiIntentStr = data.getString(ManifestMetadata.DRIVER_READ_UI);
					String configUiIntentStr = data.getString(ManifestMetadata.DRIVER_CONFIG_UI);
					
					if(frameworkVersion == null) {
						continue;
					}
					
					CommunicationChannelType commChannel = CommunicationChannelType.valueOf(driverCommChannel);
					
					if( commChannelType1.equals(commChannel) && driverCommChannel != null && driverType != null && driverAddress != null) {
						Log.d(LOGTAG ,"Adding Driver for Package: "+ driverPackageName + "  Driver address: " + driverAddress);  
						DriverType driver = new AndroidDriverTypeImpl(driverType, driverPackageName, driverAddress, commChannel, readUiIntentStr, configUiIntentStr);
						drivers.add(driver);        										
					}
				}
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
			}          
		}
		return drivers;
	}

}
