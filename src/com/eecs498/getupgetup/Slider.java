package com.eecs498.getupgetup;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class Slider extends Activity 
							 implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener{
	
	final private int SWIPE_MIN_DISTANCE = 100;
	final private int SWIPE_MIN_VELOCITY = 100;
	
	private ViewFlipper flipper = null;
	private ArrayList<ImageView> views = null;
	private GestureDetector gesturedetector = null;
	private Integer[] mCardImg = {
            R.drawable.up_arrow, R.drawable.right_arrow,
            R.drawable.down_arrow, R.drawable.left_arrow,
            R.drawable.backdrop
    };
	
	private static int UP = 0;
	private static int RIGHT = 1;
	private static int DOWN = 2;
	private static int LEFT = 3;
	private static int FIN = 4;
	
	private Animation animleftin = null;
	private Animation animleftout = null;
	
	private Animation animrightin = null;
	private Animation animrightout = null;
	
	private Animation animupin = null;
	private Animation animupout = null;
	
	private Animation animdownin = null;
	private Animation animdownout = null;
	
	private boolean isDragMode = false;
	private int currentview = 0;//(int)(4*Math.random());
	private int counter = 0;
	
	//private RingtoneManager rm = new RingtoneManager(getApplicationContext());
	private Uri ringtoneUri = null;
	private Ringtone rt = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
     	ringtoneUri = android.provider.Settings.System.DEFAULT_RINGTONE_URI;
    	rt = RingtoneManager.getRingtone(this, ringtoneUri);
    	if(!Globals.justPlay && !rt.isPlaying()){
     		rt.play();
    	}
        
        flipper = new ViewFlipper(this);
        gesturedetector = new GestureDetector(this, this);
        //vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        gesturedetector.setOnDoubleTapListener(this);
        flipper.setInAnimation(animleftin);
        flipper.setOutAnimation(animleftout);
        flipper.setFlipInterval(3000);
        flipper.setAnimateFirstView(true);
        prepareAnimations();
        prepareViews();
        addViews();
        setViewText();
        setContentView(flipper);
    }

	private void prepareAnimations() {
		animleftin = new TranslateAnimation(
        		Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f);
        		
        animleftout = new TranslateAnimation(
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f);
        
        animrightin = new TranslateAnimation(
        		Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f);
        		
        animrightout = new TranslateAnimation(
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f);
        
        animupin = new TranslateAnimation(
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        		Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,   0.0f);
        		
        animupout = new TranslateAnimation(
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f);
        
        animdownin = new TranslateAnimation(
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        		Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,   0.0f);
        		
        animdownout = new TranslateAnimation(
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f);
        
        animleftin.setDuration(1000);
        animleftin.setInterpolator(new OvershootInterpolator());
        animleftout.setDuration(1000);
        animleftout.setInterpolator(new OvershootInterpolator());
        
        animrightin.setDuration(1000);
        animrightin.setInterpolator(new OvershootInterpolator());
        animrightout.setDuration(1000);
        animrightout.setInterpolator(new OvershootInterpolator());
        
        animupin.setDuration(1000);
        animupin.setInterpolator(new OvershootInterpolator());
        animupout.setDuration(1000);
        animupout.setInterpolator(new OvershootInterpolator());
        
        animdownin.setDuration(1000);
        animdownin.setInterpolator(new OvershootInterpolator());
        animdownout.setDuration(1000);
        animdownout.setInterpolator(new OvershootInterpolator());
	}
	
	private void prepareViews(){
		ImageView view = null;
		views = new ArrayList<ImageView>();

		for(int position=0;position<5;position++)
		{
			view = new ImageView(this);
			view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			view.setImageResource(mCardImg[position]);
			views.add(view);
		}
	}
	
	private void addViews(){
		for(int index=0; index<views.size(); ++index)
		{
			flipper.addView(views.get(index),index,
					new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}
	}
	
	private void setViewText(){
		String text = getString(isDragMode ? R.string.app_info_drag : R.string.app_info_flip);
		for(int index=0; index<views.size(); ++index)
		{
			views.get(index).setTag(text);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gesturedetector.onTouchEvent(event);
	}

	public boolean onDown(MotionEvent e) {
		return false;
	}

	public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX,float velocityY) {
		
		final float ev1x = event1.getX();
		final float ev1y = event1.getY();
		final float ev2x = event2.getX();
		final float ev2y = event2.getY();
		final float xdiff = Math.abs(ev1x - ev2x);
		final float ydiff = Math.abs(ev1y - ev2y);
		final float xvelocity = Math.abs(velocityX);
		final float yvelocity = Math.abs(velocityY);
				
		if(xvelocity > this.SWIPE_MIN_VELOCITY && xdiff > this.SWIPE_MIN_DISTANCE)
		{
			if((ev1x > ev2x)&&(currentview == LEFT)) //Swipe Left
			{
			//	--currentview;
				
				if(counter<10)
				{
					currentview = (int)(3*Math.random()); //DOWN;//views.size() - 1;
					counter++;
				}
				else
				{
					currentview = FIN;
				}
				
				flipper.setInAnimation(animleftin);
				flipper.setOutAnimation(animleftout);
				flipper.scrollTo(0,0);
				flipper.setDisplayedChild(currentview);
			}
			else if((ev2x > ev1x)&&(currentview == RIGHT)) //Swipe Right
			{
				//++currentview;
				
				if(counter<10)
				{
					currentview = (int)(3*Math.random()); //UP; //0;
					if(currentview == 1)
					{
						currentview = 3;
					}
					counter++;
				}
				else
				{
					currentview = FIN;
				}
				flipper.setInAnimation(animrightin);
				flipper.setOutAnimation(animrightout);
				flipper.scrollTo(0,0);
				flipper.setDisplayedChild(currentview);
			}
			
		}
		else if(yvelocity > this.SWIPE_MIN_VELOCITY && ydiff > this.SWIPE_MIN_DISTANCE)
		{
			if((ev1y > ev2y)&&(currentview == UP)) //Swipe Up
			{
			//	--currentview;
				
				if(counter<10)
				{
					currentview = (int)(3*Math.random()); //UP; //0;
					if(currentview == 0)
					{
						currentview = 3;
					}
					counter++;
				}
				else
				{
					currentview = FIN;
				}	
				flipper.setInAnimation(animupin);
				flipper.setOutAnimation(animupout);
				flipper.scrollTo(0,0);
				flipper.setDisplayedChild(currentview);
			}
			else if((ev2y > ev1y)&&(currentview == DOWN)) //Swipe Down
			{
				//++currentview;
				
				if(counter<10)
				{
					currentview = (int)(3*Math.random()); //UP; //0;
					if(currentview == 2)
					{
						currentview = 3;
					}
					counter++;
				}
				else
				{
					currentview = FIN;
				}
				flipper.setInAnimation(animdownin);
				flipper.setOutAnimation(animdownout);
				flipper.scrollTo(0,0);
				flipper.setDisplayedChild(currentview);
			}
			
		}
		
		if(currentview == FIN){
			if(!Globals.justPlay && rt.isPlaying()){
				rt.stop();
			}
			Intent intent = new Intent(this,GetUpGetUp.class);
			startActivity(intent);
		}
				
		return false;
	}

	public void onLongPress(MotionEvent e) {
		}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		return false;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public boolean onDoubleTap(MotionEvent e) {
		flipper.scrollTo(0,0);
		
		return false;
	}

	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}
}