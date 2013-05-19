package pkg.talu.lowa.s4.tweaker.soundtweaks;

import pkg.talu.lowa.s4.tweaker.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import pkg.talu.lowa.s4.tweaker.util.RootProcess;

public class SoundTweaks extends Activity {
	
	//UI objects//
	//the seek bar variable
	private SeekBar seekbar_gpl;
	private SeekBar seekbar_mic_gain;
	
	// declare text label objects
    private TextView textProgress;
    private TextView textgplmicProgress;
	
	//a variable to store the value
	private int gpl_speaker_gain;
	private int gpl_mic_gain;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.soundtweaks);
		setTitle(R.string.menu_soundtweaks);
		
		// make text label for progress value
		textProgress = (TextView)findViewById(R.id.textViewProgress);
		textgplmicProgress = (TextView)findViewById(R.id.gplmicgainprogress);
		//get the seek bar from main.xml file
		seekbar_gpl = (SeekBar) findViewById(R.id.sb_gpl_speaker_gain);
		seekbar_mic_gain = (SeekBar) findViewById(R.id.sb_gpl_mic_gain);
        
        
        //get the current settings
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
  		gpl_speaker_gain = sharedPreferences.getInt("gpl_speaker_gain", 0);
  		gpl_mic_gain = sharedPreferences.getInt("gpl_mic_gain", 0);
  		//set progress text
  		textProgress.setText(""+gpl_speaker_gain);
  		textgplmicProgress.setText(""+gpl_mic_gain);
  		//seek bar settings//
        //sets the max range to 30 because we add +20 later
  		//seekbar_gpl.setMax(0);
        //seekbar_gpl.setMax(30);
         
        //set the seek bar progress based on the sharedprefs
  		// needs the -20 at the end
        seekbar_gpl.setKeyProgressIncrement(gpl_speaker_gain-20);
        seekbar_mic_gain.setKeyProgressIncrement(gpl_mic_gain-20);
          
        //sets the progress of the seek bar based on the sharedprefs
        // needs the -20 at the end
        seekbar_gpl.setProgress(gpl_speaker_gain-20);
        seekbar_mic_gain.setProgress(gpl_mic_gain-20);
        Log.d("setProgress", "SetProgress: "+gpl_speaker_gain);
        Log.d("setProgress", "SetProgress: "+gpl_mic_gain);

        
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
      				Log.d("process","echo gpl speaker gain: "+ gpl_speaker_gain);
      				//calls RootProcess
					RootProcess process = new RootProcess();
					if (!process.init()) {
					    return;
					}
					// Writing the values to the files
					process.write("echo "+ gpl_speaker_gain +" > /sys/kernel/sound_control/gpl_speaker_gain\n");
					Log.d("process","echo gpl speaker gain: "+ gpl_speaker_gain);
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
      				int siz=progress+20;
      				gpl_speaker_gain = siz;
      				Log.d("progress","Progressbar: "+siz);
      				Log.d("progress","Progressbar: "+gpl_speaker_gain);
     				
      				// change progress text label with current seekbar value
      		    	textProgress.setText(""+gpl_speaker_gain);
      			}
      		});	
        
        //register OnSeekBarChangeListener, so it can actually change values
        seekbar_mic_gain.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
      		{
      			@Override
      			public void onStopTrackingTouch(SeekBar seekBar) 
      			{
      			  //set the preferences using the seekbar_gpl variable value
      			  SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
      			  SharedPreferences.Editor editor = sharedPreferences.edit();
      			  editor.putInt("gpl_mic_gain", gpl_mic_gain);
      			  editor.commit(); 
      			  
      			// Try catch block for if it may go wrong 
      			try {
      				Log.d("process","echo gpl mic gain: "+ gpl_mic_gain);
      				//calls RootProcess
					RootProcess process = new RootProcess();
					if (!process.init()) {
					    return;
					}
					// Writing the values to the files
					process.write("echo "+ gpl_mic_gain +" > /sys/kernel/sound_control/gpl_mic_gain\n");
					Log.d("process","echo gpl mic gain: "+ gpl_mic_gain);
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
      				int siz=progress+20;
      				gpl_mic_gain = siz;
      				Log.d("progress","Progressbar: "+siz);
      				Log.d("progress","Progressbar: "+gpl_mic_gain);
     				
      				// change progress text label with current seekbar value
      				textgplmicProgress.setText(""+gpl_mic_gain);
      			}
      		});
        

	}
}
