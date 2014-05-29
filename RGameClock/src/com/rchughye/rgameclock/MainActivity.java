package com.rchughye.rgameclock;

import com.rchughye.rgameclock.Helper;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.media.*;

//import android.util.Log;

public class MainActivity extends ActionBarActivity {
	// initialize
	public static int topPlayerTime,bottomPlayerTime, gameTimeBottom, gameTimeTop, timeControlOption, timeIncrement;
	public SharedPreferences sharedPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
			

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		//Set Default Values from settings
		updateSettingsValues();
		gameTimeBottom = bottomPlayerTime;  
		gameTimeTop = topPlayerTime;
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		Button timer1 = (Button) findViewById(R.id.timerButton1);
		Button timer2 = (Button) findViewById(R.id.timerButton2);
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}
		else if (id == R.id.action_reset){
			updateSettingsValues();
			gameTimeBottom = bottomPlayerTime;  
			gameTimeTop = topPlayerTime;
	        timerHandler.removeCallbacks(timerRunnable1);
	        timerHandler.removeCallbacks(timerRunnable2);
	        timerState1 = false;
	        timerState2 = false;
	        timer1.setText(Helper.timeToString(gameTimeBottom));
	        timer2.setText(Helper.timeToString(gameTimeTop));
	        timer1.setTextColor(Color.BLACK);
	        timer2.setTextColor(Color.BLACK);
	        
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			Button timer1 = (Button) rootView.findViewById(R.id.timerButton1);
			Button timer2 = (Button) rootView.findViewById(R.id.timerButton2);
			timer1.setText(Helper.timeToString(gameTimeBottom));
	        timer2.setText(Helper.timeToString(gameTimeTop));
	        timer1.setTextColor(Color.BLACK);
	        timer2.setTextColor(Color.BLACK);			
			return rootView;
		}

	}
	
	private void updateSettingsValues(){
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		topPlayerTime = Integer.parseInt(sharedPref.getString("top_player_time", "300")) * 1000;
		bottomPlayerTime = Integer.parseInt(sharedPref.getString("bottom_player_time", "300")) * 1000;
		timeControlOption = Integer.parseInt(sharedPref.getString("time_control_list", "-1"));
		timeIncrement = Integer.parseInt(sharedPref.getString("time_increment", "0"));
		if (timeControlOption == -1) {
			timeIncrement = 0;
		}		
	}
		
	// Timer code below
	long startTime1 = 0;
	long startTime2 = 0;
	boolean timerState1 = false;
	boolean timerState2 = false;
    Handler timerHandler = new Handler();
    
    ToneGenerator tone =new ToneGenerator(AudioManager.STREAM_ALARM, 50);
    //ToneGenerator.TONE_DTMF_0
    
    Runnable timerRunnable1 = new Runnable() {
        @Override
        public void run() {
        	gameTimeBottom -= (SystemClock.elapsedRealtime() - startTime1);
            
            Button timer = (Button) findViewById(R.id.timerButton1);
            
            // if time elapsed; stop running
            if (gameTimeBottom < 500 || gameTimeTop < 500){
            	timer.setTextColor(Color.RED);
            	tone.startTone(ToneGenerator.TONE_DTMF_0,1500);
            	return;
            }
            
            String outputTime1 = Helper.timeToString(gameTimeBottom);    
            timer.setText(outputTime1);
            
            startTime1 = SystemClock.elapsedRealtime();
            timerHandler.postDelayed(this, 250);
        }
    };
    
    Runnable timerRunnable2 = new Runnable() {
        @Override
        public void run() {
        	gameTimeTop -= (SystemClock.elapsedRealtime() - startTime2);
            
            Button timer = (Button) findViewById(R.id.timerButton2);
            
            // if time elapsed; stop running
            if (gameTimeBottom < 500 || gameTimeTop < 500){
            	timer.setTextColor(Color.RED);
            	tone.startTone(ToneGenerator.TONE_DTMF_0,1500);
            	return;
            }            
            
            String outputTime2 = Helper.timeToString(gameTimeTop);  
            timer.setText(outputTime2);
            
            startTime2 = SystemClock.elapsedRealtime();
            timerHandler.postDelayed(this, 250);
        }
    };
    
    
	public void onClickTimer1(View view){
		Button timer1 = (Button) findViewById(R.id.timerButton1);
		// game finished, disable buttons
		if (gameTimeBottom < 1000 || gameTimeTop < 1000){
			return;
		}
		// if timer currently running
        if (timerState1 == true) {
        	// stop timer
            timerHandler.removeCallbacks(timerRunnable1);
        	timerState1 = false;
        	
        	// add increment
        	gameTimeBottom += timeIncrement * 1000;
        	timer1.setText(Helper.timeToString(gameTimeBottom));
        	
        	
        	// start other timer
        	startTime2 = SystemClock.elapsedRealtime();
        	timerState2 = true;
        	timerHandler.postDelayed(timerRunnable2, 0);
            
        } else if (timerState1 == false & timerState2 == false) {
        	// start this timer
        	startTime1 = SystemClock.elapsedRealtime();
    
            timerHandler.postDelayed(timerRunnable1, 0);
            timerState1 = true;
        }
        else{
        	return;
        }

        
	}
	
	public void onClickTimer2(View view){
		Button timer2 = (Button) findViewById(R.id.timerButton2);
		// game finished, disable buttons
		if (gameTimeBottom < 1000 || gameTimeTop < 1000){
			return;
		}
		
		// if timer currently running
        if (timerState2 == true) {
        	// stop timer
            timerHandler.removeCallbacks(timerRunnable2);
        	timerState2 = false;
        	
        	// add increment
        	gameTimeTop += timeIncrement * 1000;
        	timer2.setText(Helper.timeToString(gameTimeTop));
        	
        	// start other timer
        	startTime1 = SystemClock.elapsedRealtime();
        	timerState1 = true;
        	timerHandler.postDelayed(timerRunnable1, 0);
            
        } else if (timerState1 == false & timerState2 == false) {
        	// start this timer
        	startTime2 = SystemClock.elapsedRealtime();

            timerHandler.postDelayed(timerRunnable2, 0);
            timerState2 = true;
        }
        else {
        	return;
        }
	}

	
	@Override
	protected void onResume() {
	    super.onResume();
	    // update settings when returning from settings screen
	    updateSettingsValues();

	}

	  @Override
	    public void onPause() {
	        super.onPause();
	        //timerHandler.removeCallbacks(timerRunnable);
	        //timer_state = true;

	    }
	  @Override
	    public void onStop() {
	        super.onStop();
	        timerHandler.removeCallbacks(timerRunnable1);
	        timerHandler.removeCallbacks(timerRunnable2);
	        timerState1 = false;
	        timerState2 = false;
	    }
	  @Override
	    public void onDestroy() {
	        super.onDestroy();
	        timerHandler.removeCallbacks(timerRunnable1);
	        timerHandler.removeCallbacks(timerRunnable2);
	        timerState1 = false;
	        timerState2 = false;
	    }	
	

}

