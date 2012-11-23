/*
WifiStaticArp
(c) 2012 Pau Oliva Fora <pof[at]eslack[dot]org>
*/
package org.eslack.wifistaticarp;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;

import java.io.FileReader;
//import android.util.Log;

import android.content.Context;
import com.stericson.RootTools.*;

public class Utils {

        public String exec(String command) {
                final StringBuffer myOutput = new StringBuffer();
                Command command_out = new Command(0, command)
                {
                        @Override
                        public void output(int id, String line)
                        {
                                myOutput.append(line);
                        }
                };
                try {
                        RootTools.getShell(RootTools.useRoot);
                        RootTools.getShell(RootTools.useRoot).add(command_out).waitForFinish();
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return myOutput.toString();
        }



	/**
 	 * stolen from http://www.flattermann.net/2011/02/android-howto-find-the-hardware-mac-address-of-a-remote-host/
 	 */
	public static String getMacFromArpCache(String ip) {
    	    if (ip == null)
        	return null;
    	    BufferedReader br = null;
    	    try {
        	br = new BufferedReader(new FileReader("/proc/net/arp"));
        	String line;
        	while ((line = br.readLine()) != null) {
			//Log.v("WifiStaticArp", "LINE:" + line);
            	    String[] splitted = line.split(" +");
            	    if (splitted != null && splitted.length >= 4 && ip.equals(splitted[0])) {
                	// Basic sanity check
                	String mac = splitted[3];
                	if (mac.matches("..:..:..:..:..:..")) {
                    	    return mac;
                	} else {
                    	    return null;
                	}
            	    }
        	}
    	    } catch (Exception e) {
        	e.printStackTrace();
    	    } finally {
        	try {
            	    br.close();
        	} catch (IOException e) {
            	    e.printStackTrace();
        	}
    	    }
    	    return null;
	}


}
