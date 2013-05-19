package pkg.talu.lowa.s4.tweaker.soundtweaks;

import pkg.talu.lowa.s4.tweaker.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import pkg.talu.lowa.s4.tweaker.util.RootProcess;

public class SoundTweaks extends Activity {
	
	//UI objects//
	//the seek bar variable
	private SeekBar seekbar_gpl;
	
	//a variable to store the system brightness
	private int gpl_speaker_gain;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.soundtweaks);
		setTitle(R.string.menu_soundtweaks);
		
		//get the seek bar from main.xml file
		seekbar_gpl = (SeekBar) findViewById(R.id.sb_gpl_speaker_gain);
		
		//seek bar settings//
        //sets the range between 0 and 50
        seekbar_gpl.setMax(50);
        
        try 
        {
        	//get the current settings
        	SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
  		  	gpl_speaker_gain = sharedPreferences.getInt("gpl_speaker_gain", 0);
        } 
        catch (Exception e) 
		{
        	//throw an error case it couldn't be retrieved
			Log.e("Error", "Cannot access preferences");
			e.printStackTrace();
		}
        
        //set the seek bar progress based on the sharedprefs
        seekbar_gpl.setKeyProgressIncrement(gpl_speaker_gain);
        
        //sets the progress of the seek bar based on the sharedprefs
        seekbar_gpl.setProgress(gpl_speaker_gain);
        
        //register OnSeekBarChangeListener, so it can actually change values
        seekbar_gpl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
      		{
      			@Override
      			public void onStopTrackingTouch(SeekBar seekBar) 
      			{
      			  //set the preferences using the seekbar_gpl variable value
      			  SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
      			  SharedPreferences.Editor editor = sharedPreferences.edit();
      			  editor.putInt("gpl_speaker_gain", gpl_speaker_gain);
      			  editor.commit(); 
      			  
      			// Try catch block for if it may go wrong 
      			try {
      				//calls RootProcess
					RootProcess process = new RootProcess();
					if (!process.init()) {
					    return;
					}
					// Writing the values to the files
					process.write("echo "+ gpl_speaker_gain +" > /sys/kernel/sound_control/gpl_speaker_gain\n");
					process.term();
				} catch (Exception e) {
					Log.e("Error","crashed"+e);
				}
      			  

      			}
      			
      			@Override
      			public void onStartTrackingTouch(SeekBar seekBar) 
      			{
      			}
      			
      			@Override
      			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
      			{
      				// sets the minimal level
      				// if seek bar is 20 or any value below
      				if(progress<=20)
      				{
      					// set the level to 20
      					gpl_speaker_gain=20;
      				}
      				else // if level is greater than 20
      				{
      					// sets level variable based on the progress bar 
      					gpl_speaker_gain = progress;
      				}
      			}
      		});	
        

	}
}
