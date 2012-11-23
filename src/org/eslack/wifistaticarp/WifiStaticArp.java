/*
WifiStaticArp
(c) 2012 Pau Oliva Fora <pof[at]eslack[dot]org>
*/

package org.eslack.wifistaticarp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class WifiStaticArp extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("Thanks for using WifiStaticArp!");
        setContentView(tv);
    }
}
