package com.rchughye.rgameclock;

public class Helper {
	
	public static String timeToString(long time){
        int seconds = (int) (time/ 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        
        //String min = Integer.toString(minutes);
        //String sec = String.format("%02d",seconds);
        // String outputTime = String.format("%02d:%02d", minutes,seconds);
        
        String outputTime = String.format("%d:%02d", minutes,seconds);
        
        return outputTime;
		
	}

}
