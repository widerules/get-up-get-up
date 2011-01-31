package com.eecs498.getupgetup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

public class alarmReceiver extends BroadcastReceiver {
	
	private Uri ringtoneUri = null;
	
    @Override 
    public void onReceive(Context context, Intent intent) 
    { 
    	ringtoneUri = android.provider.Settings.System.DEFAULT_RINGTONE_URI;
    	Globals.rt = RingtoneManager.getRingtone(context,ringtoneUri);
    	if(!Globals.rt.isPlaying()){
    		Globals.rt.play();
    	}
    	Intent i = new Intent();
    	i.setClass(context, Slider.class);
        context.startActivity(i);
    }
} 
