package com.eecs498.getupgetup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class alarmReceiver extends BroadcastReceiver {
	

	
    @Override 
    public void onReceive(Context context, Intent intent) 
    { 
    	Globals.justPlay = false;
    	Intent i = new Intent(context, Slider.class);
    	i.setFlags(268435456);
        context.startActivity(i);
    }
} 
