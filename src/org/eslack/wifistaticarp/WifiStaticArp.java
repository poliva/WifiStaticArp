/*
WifiStaticArp
(c) 2012 Pau Oliva Fora <pof[at]eslack[dot]org>
*/

package org.eslack.wifistaticarp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

import com.stericson.RootTools.*;

public class WifiStaticArp extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);

		RootTools.useRoot=true;
		if (RootTools.isAccessGiven()) {
			checkBox.setChecked(true);
		} else {
			checkBox.setChecked(false);
		}
	}
}
