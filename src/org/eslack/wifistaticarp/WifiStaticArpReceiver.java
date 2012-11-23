/*
WifiStaticArp
(c) 2012 Pau Oliva Fora <pof[at]eslack[dot]org>
*/

package org.eslack.wifistaticarp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import android.os.AsyncTask;

import com.stericson.RootTools.*;

public class WifiStaticArpReceiver extends BroadcastReceiver {

	private static final String TAG = "WifiStaticArpReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {

		int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_UNKNOWN);
		//Log.d(TAG, (new StringBuilder("onReceive() intent: ")).append(intent).toString());

		switch(wifiState){
			case WifiManager.WIFI_STATE_ENABLED:
				Log.v(TAG, "WIFI_STATE_ENABLED");
				new SetStaticArpTask().execute("WIFI_STATE_ENABLED");
			break;
		}
	}
}

class SetStaticArpTask extends AsyncTask<String, Void, Boolean> {

	private static final String TAG = "SetStaticArpTask";

        private Utils mUtils = new Utils();

	private String interfaceName;
	private String gatewayIP;
	private String gatewayMAC;

	protected Boolean doInBackground(String... str) {

		interfaceName = android.os.SystemProperties.get ("wifi.interface", "unknown");
		if (interfaceName.equals("unknown")) {
			Log.v(TAG, "Aborting: can't get wifi interface name");
			return false;
		}

		gatewayIP = android.os.SystemProperties.get("dhcp." + interfaceName + ".gateway", "unknown");
		if (gatewayIP.equals("unknown")) {
			Log.v(TAG, "Aborting: can't get gateway IP address");
			return false;
		}

		// make sure the gateway mac is in the arp cache
		try {
			InetAddress.getByName(gatewayIP).isReachable(4000);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		gatewayMAC = mUtils.getMacFromArpCache(gatewayIP);
		if (gatewayMAC == null || gatewayMAC.equals("00:00:00:00:00:00")) {
			// retry getting the gateway mac address
			Log.v("WifiStaticArp", "RETRYING: "+ interfaceName + " " + gatewayIP + " " + gatewayMAC);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mUtils.exec("ping -c 2 -w 2 " + gatewayIP);
			gatewayMAC = mUtils.getMacFromArpCache(gatewayIP);
		}

		if (gatewayMAC == null || gatewayMAC.equals("00:00:00:00:00:00")) {
			return false;
		}

		return true;
	}

	protected void onPostExecute(Boolean result) {

		String output;
		if (result == true) {
			RootTools.useRoot=true;
			if (RootTools.isAccessGiven()) {
				mUtils.exec("ip neighbor add " + gatewayIP + " lladdr " + gatewayMAC + " nud permanent dev " + interfaceName);
				mUtils.exec("ip neighbor change " + gatewayIP + " lladdr " + gatewayMAC + " nud permanent dev " + interfaceName);
				output = "MAC address " + gatewayMAC + " static for " + gatewayIP + " on " + interfaceName;
			} else {
				output = "Could not set static ARP on the gateway mac, no root access given.";
			}

		} else {
			output = "Could not set static ARP on the gateway mac";
		}
		Log.v(TAG, output);
	}
}
