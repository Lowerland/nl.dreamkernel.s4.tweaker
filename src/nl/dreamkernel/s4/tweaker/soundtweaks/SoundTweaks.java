/*
 *  Copyright 2013-2014 Jeroen Gorter <Lowerland@hotmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package nl.dreamkernel.s4.tweaker.soundtweaks;

import nl.dreamkernel.s4.tweaker.util.DialogActivity;
import nl.dreamkernel.s4.tweaker.util.FileCheck;
import nl.dreamkernel.s4.tweaker.util.SysFs;
import nl.dreamkernel.s4.tweaker.util.RootProcess;
import nl.dreamkernel.s4.tweaker.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SoundTweaks extends Activity {
	static final String TAG = "S4Tweaker";
	
	//UI objects
	//the seek bar variable
	private static SeekBar seekbar_gpl;
	private static SeekBar seekbar_mic_gain;
	private static SeekBar seekbar_cam_mic_gain;
	private static SeekBar seekbar_headphone_gain;
	private static SeekBar seekbar_hdmi_spkr_gain;
	private static SeekBar seekbar_headset_mic_gain;
	
	// declare text label objects
    private static TextView textProgress;
    private static TextView textgplmicProgress;
    private static TextView textgplcammicProgress;
    private static TextView textgplheadphoneProcess;
    private static TextView textgplhdmispkrgainProgress;
    private static TextView textgplheadsetmicgainProgress;
    private static TextView textuncompatibel;
    private static TextView textuncompatibel2;
    private static TextView textuncompatibel3;
    private static TextView textuncompatibel4;
    private static TextView textuncompatibel5;
    private static TextView textuncompatibel6;
    
    //private Switch sound_tweaks_set_on_boot;
	
	// Variables to store the value
	private int gpl_speaker_gain;
	private int gpl_mic_gain;
	private int gpl_cam_mic_gain;
	private int gpl_headphone_gain;
	private int gpl_hdmi_spkr_gain;
	private int gpl_headset_mic_gain;
	private int value_default;
	public static int soundtweaks_hide_dialog;
	
	private String headphonevalueconvert;
	private String headphonesubstring;

	// Static Constants for file value checking
	public static final SysFs vCheck_gpl_speaker_gain = new SysFs("/sys/kernel/sound_control/gpl_speaker_gain");
	public static final SysFs vCheck_gpl_mic_gain = new SysFs("/sys/kernel/sound_control/gpl_mic_gain");
	public static final SysFs vCheck_gpl_cam_mic_gain = new SysFs("/sys/kernel/sound_control/gpl_cam_mic_gain");
	public static final SysFs vCheck_gpl_headphone_gain = new SysFs("/sys/kernel/sound_control/gpl_headphone_gain");
	public static final SysFs vCheck_gpl_hdmi_speaker_gain = new SysFs("/sys/kernel/sound_control/gpl_hdmi_spkr_gain");
	public static final SysFs vCheck_gpl_headset_mic_gain = new SysFs("/sys/kernel/sound_control/gpl_headset_mic_gain");
/*
	public static final SysFs vCheck_gpl_speaker_gain = new SysFs("/mnt/sdcard/testfiles/gpl_speaker_gain");
	public static final SysFs vCheck_gpl_mic_gain = new SysFs("/mnt/sdcard/testfiles/gpl_mic_gain");
	public static final SysFs vCheck_gpl_cam_mic_gain = new SysFs("/mnt/sdcard/testfiles/gpl_cam_mic_gain");
	public static final SysFs vCheck_gpl_headphone_gain = new SysFs("/mnt/sdcard/testfiles/gpl_headphone_gain");
	public static final SysFs vCheck_gpl_hdmi_speaker_gain = new SysFs("/mnt/sdcard/testfiles/gpl_hdmi_spkr_gain");
	public static final SysFs vCheck_gpl_headset_mic_gain = new SysFs("/mnt/sdcard/testfiles/gpl_headset_mic_gain");
*/
	private int Value_gpl_speaker_gain;
	private int Value_gpl_mic_gain;
	private int Value_gpl_cam_mic_gain;
	//private int Value_gpl_headphone_gain;
	private int Value_gpl_hdmi_speaker_gain;
	private int Value_gpl_headset_mic_gain;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.soundtweaks);
		setTitle(R.string.menu_soundtweaks);
		getActionBar().hide();

		final SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);

		// Root init s/e
		RootProcess rootProcess = new RootProcess();
        Log.d(TAG, "Root init s");
        rootProcess.init();
        Log.d(TAG, "Root init e");
        // Read in the Values from files
        if (vCheck_gpl_speaker_gain.exists()) {
        	Value_gpl_speaker_gain = Integer.parseInt(vCheck_gpl_speaker_gain.read(rootProcess));
           	gpl_speaker_gain = Value_gpl_speaker_gain;
        } else { gpl_speaker_gain = value_default; }
        
        if (vCheck_gpl_mic_gain.exists()) {
        	Value_gpl_mic_gain = Integer.parseInt(vCheck_gpl_mic_gain.read(rootProcess));
        	gpl_mic_gain = Value_gpl_mic_gain;
        } else { gpl_mic_gain = value_default; }
        
        if (vCheck_gpl_cam_mic_gain.exists()) {
        	Value_gpl_cam_mic_gain = Integer.parseInt(vCheck_gpl_cam_mic_gain.read(rootProcess));
        	gpl_cam_mic_gain = Value_gpl_cam_mic_gain;
        } else { gpl_cam_mic_gain = value_default; }

        if (vCheck_gpl_headphone_gain.exists()) {
        	// This File hold 2 values example: 20 20
        	// so it needs an different aproach

        	headphonevalueconvert = vCheck_gpl_headphone_gain.read(rootProcess);
        	//int nLengthOfString = headphonevalueconvert.length();
            int nStartIndex = 0;
        	int nEndIndex = 2;                    
        	headphonesubstring = headphonevalueconvert.substring(nStartIndex, nEndIndex);         	
        	gpl_headphone_gain = Integer.parseInt(headphonesubstring);      	 	
        	
        } else { gpl_headphone_gain = value_default; }
        
        if (vCheck_gpl_hdmi_speaker_gain.exists()) {
        	Value_gpl_hdmi_speaker_gain = Integer.parseInt(vCheck_gpl_hdmi_speaker_gain.read(rootProcess));
        	gpl_hdmi_spkr_gain = Value_gpl_hdmi_speaker_gain;
        } else { gpl_hdmi_spkr_gain = value_default; }
        
        if (vCheck_gpl_headset_mic_gain.exists()) {
        	Value_gpl_headset_mic_gain = Integer.parseInt(vCheck_gpl_headset_mic_gain.read(rootProcess));
        	gpl_headset_mic_gain = Value_gpl_headset_mic_gain;
        } else { gpl_headset_mic_gain = value_default; }

        rootProcess.term();
        rootProcess = null;
        
        // Start on boot switch
        // sound_tweaks_set_on_boot = (Switch) findViewById(R.id.sound_tweaks_on_boot); 

		// make text label for progress value
		textProgress = (TextView)findViewById(R.id.textViewProgress);
		textgplmicProgress = (TextView)findViewById(R.id.gplmicgainprogress);
		textgplcammicProgress = (TextView)findViewById(R.id.gplcammicgainprogress);
		textgplheadphoneProcess = (TextView)findViewById(R.id.gplheadphonegainprogress);
		textgplhdmispkrgainProgress = (TextView)findViewById(R.id.gplhdmispkrgainprogress);
		textgplheadsetmicgainProgress = (TextView)findViewById(R.id.gplheadsetmicgainprogress);
		
		//get the seek bar from main.xml file
		seekbar_gpl = (SeekBar) findViewById(R.id.sb_gpl_speaker_gain);
		seekbar_mic_gain = (SeekBar) findViewById(R.id.sb_gpl_mic_gain);
		seekbar_cam_mic_gain = (SeekBar) findViewById(R.id.sb_gpl_cam_mic_gain);
		seekbar_headphone_gain = (SeekBar) findViewById(R.id.sb_gpl_headphone_gain);
		seekbar_hdmi_spkr_gain = (SeekBar) findViewById(R.id.sb_gpl_hdmi_spkr_gain);
		seekbar_headset_mic_gain = (SeekBar) findViewById(R.id.sb_gpl_headset_mic_gain);
        
		// TODO Maybe need this function in the future
		//get the current settings
        //SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
        //gpl_speaker_gain = sharedPreferences.getInt("gpl_speaker_gain", 0);
  		//gpl_mic_gain = sharedPreferences.getInt("gpl_mic_gain", 0);
  		//gpl_cam_mic_gain = sharedPreferences.getInt("gpl_cam_mic_gain", 0);
  		//gpl_headphone_gain = sharedPreferences.getInt("gpl_headphone_gain", 0);
  		//gpl_hdmi_spkr_gain = sharedPreferences.getInt("gpl_hdmi_spkr_gain", 0);
  		//gpl_headset_mic_gain = sharedPreferences.getInt("gpl_headset_mic_gain", 0);
  		
  		//set progress text
  		textProgress.setText(""+gpl_speaker_gain);
  		textgplmicProgress.setText(""+gpl_mic_gain);
  		textgplcammicProgress.setText(""+gpl_cam_mic_gain);
  		textgplheadphoneProcess.setText(""+gpl_headphone_gain);
  		textgplhdmispkrgainProgress.setText(""+gpl_hdmi_spkr_gain);
  		textgplheadsetmicgainProgress.setText(""+gpl_headset_mic_gain);
  		
  		//Find Views
  		textuncompatibel = (TextView)findViewById(R.id.gpl_spkr_alert);
  		textuncompatibel2 = (TextView)findViewById(R.id.gpl_spkr_alert2);
  		textuncompatibel3 = (TextView)findViewById(R.id.gpl_spkr_alert3);
  		textuncompatibel4 = (TextView)findViewById(R.id.gpl_spkr_alert4);
  		textuncompatibel5 = (TextView)findViewById(R.id.gpl_spkr_alert5);
  		textuncompatibel6 = (TextView)findViewById(R.id.gpl_spkr_alert6);

  		//seek bar settings//
        //sets the max range to 30 because we add +20 later
  		//seekbar_gpl.setMax(0);
        //seekbar_gpl.setMax(30);
         
        //set the seek bar progress based on the sharedprefs
  		// needs the -20 at the end
        seekbar_gpl.setKeyProgressIncrement(gpl_speaker_gain-20);
        seekbar_mic_gain.setKeyProgressIncrement(gpl_mic_gain-20);
        seekbar_cam_mic_gain.setKeyProgressIncrement(gpl_cam_mic_gain-20);
        seekbar_headphone_gain.setKeyProgressIncrement(gpl_headphone_gain-20);
        seekbar_hdmi_spkr_gain.setKeyProgressIncrement(gpl_hdmi_spkr_gain-20);
        seekbar_headset_mic_gain.setKeyProgressIncrement(gpl_headset_mic_gain-20);
          
        //sets the progress of the seek bar based on the sharedprefs
        // needs the -20 at the end
        seekbar_gpl.setProgress(gpl_speaker_gain-20);
        seekbar_mic_gain.setProgress(gpl_mic_gain-20);
        seekbar_cam_mic_gain.setProgress(gpl_cam_mic_gain-20);
        seekbar_headphone_gain.setProgress(gpl_headphone_gain-20);
        seekbar_hdmi_spkr_gain.setProgress(gpl_hdmi_spkr_gain-20);
        seekbar_headset_mic_gain.setProgress(gpl_headset_mic_gain-20);
        
        Log.d(TAG, "SetProgress gpl_speaker_gain: "+gpl_speaker_gain);
        Log.d(TAG, "SetProgress gpl_mic_gain: "+gpl_mic_gain);
        Log.d(TAG, "SetProgress gpl_cam_mic_gain: "+gpl_cam_mic_gain);
        Log.d(TAG, "SetProgress gpl_headphone_gain: "+gpl_headphone_gain);
        Log.d(TAG, "SetProgress gpl_hdmi_spkr_gain: "+gpl_hdmi_spkr_gain);
        Log.d(TAG, "SetProgress gpl_headset_mic_gain: "+gpl_headset_mic_gain);
        
        //register OnSeekBarChangeListener, so it can actually change values
        seekbar_gpl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
      		{
      			@Override
      			public void onStopTrackingTouch(SeekBar seekBar)
      			{
      			  //set the preferences using the seekbar_gpl variable value
      			 /* SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
      			  SharedPreferences.Editor editor = sharedPreferences.edit();
      			  editor.putInt("gpl_speaker_gain", gpl_speaker_gain);
      			  editor.commit(); */
      			  
      			// Try catch block for if it may go wrong 
      			try {
      				Log.d(TAG,"echo gpl speaker gain: "+ gpl_speaker_gain);
      				//calls RootProcess
					RootProcess process = new RootProcess();
					if (!process.init()) {
					    return;
					}
					// Writing the values to the files 
					process.write("echo "+ gpl_speaker_gain +" > /sys/kernel/sound_control/gpl_speaker_gain\n");
					Log.d(TAG,"echo gpl speaker gain: "+ gpl_speaker_gain);
					process.term();
				} catch (Exception e) {
					Log.e(TAG,"Error crashed "+e);
					Log.d(TAG,"error "+e);
					}
      			}
      			@Override
      			public void onStartTrackingTouch(SeekBar seekBar) {}

      			@Override
      			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
      			{
				// sets the minimal level
      				int siz=progress+20;
      				gpl_speaker_gain = siz;
      				Log.d(TAG,"Progressbar: "+siz);
      				Log.d(TAG,"Progressbar: "+gpl_speaker_gain);
     				
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
      				// TODO Maybe no function for this in the future
      			  /*//set the preferences using the seekbar_gpl variable value
      			  SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
      			  SharedPreferences.Editor editor = sharedPreferences.edit();
      			  editor.putInt("gpl_mic_gain", gpl_mic_gain);
      			  editor.commit(); */
      			  
      			// Try catch block for if it may go wrong 
      			try {
      				Log.d(TAG,"echo gpl mic gain: "+ gpl_mic_gain);
      				//calls RootProcess
					RootProcess process = new RootProcess();
					if (!process.init()) {
					    return;
					}
					// Writing the values to the files
					process.write("echo "+ gpl_mic_gain +" > /sys/kernel/sound_control/gpl_mic_gain\n");
					Log.d(TAG,"echo gpl mic gain: "+ gpl_mic_gain);
					process.term();
				} catch (Exception e) {
					Log.e(TAG,"Error crashed "+e);
					Log.d(TAG,"error "+e);
					}
      			}
      			
      			@Override
      			public void onStartTrackingTouch(SeekBar seekBar){}
     			
      			@Override
      			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
      			{
      				// sets the minimal level
      				int siz=progress+20;
      				gpl_mic_gain = siz;
      				Log.d(TAG,"Progressbar: "+siz);
      				Log.d(TAG,"Progressbar: "+gpl_mic_gain);

      				// change progress text label with current seekbar value
      				textgplmicProgress.setText(""+gpl_mic_gain);
      			}
      		});
        
        //register OnSeekBarChangeListener, so it can actually change values
        seekbar_cam_mic_gain.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
      		{
      			@Override
      			public void onStopTrackingTouch(SeekBar seekBar) 
      			{
      				// TODO Maybe no function for this in the future
      			  /*//set the preferences using the seekbar_gpl variable value
      			  SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
      			  SharedPreferences.Editor editor = sharedPreferences.edit();
      			  editor.putInt("gpl_cam_mic_gain", gpl_cam_mic_gain);
      			  editor.commit(); */
      			  
      			// Try catch block for if it may go wrong 
      			try {
      				Log.d(TAG,"echo gpl cam mic gain: "+ gpl_cam_mic_gain);
      				//calls RootProcess
					RootProcess process = new RootProcess();
					if (!process.init()) {
					    return;
					}
					// Writing the values to the files
					process.write("echo "+ gpl_cam_mic_gain +" > /sys/kernel/sound_control/gpl_cam_mic_gain\n");
					Log.d(TAG,"echo gpl cam mic gain: "+ gpl_cam_mic_gain);
					process.term();
				} catch (Exception e) {
					Log.e(TAG,"Error crashed "+e);
					Log.d(TAG,"error "+e);
					}
      			}
      			
      			@Override
      			public void onStartTrackingTouch(SeekBar seekBar){}

      			@Override
      			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
      			{
      				// sets the minimal level
      				int siz=progress+20;
      				gpl_cam_mic_gain = siz;
      				Log.d(TAG,"Progressbar: "+siz);
      				Log.d(TAG,"Progressbar: "+gpl_cam_mic_gain);
     				
      				// change progress text label with current seekbar value
      				textgplcammicProgress.setText(""+gpl_cam_mic_gain);
      			}
      		});
        
        //register OnSeekBarChangeListener, so it can actually change values
        seekbar_headphone_gain.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
      		{
      			@Override
      			public void onStopTrackingTouch(SeekBar seekBar) 
      			{
      				// TODO Maybe no function for this in the future
      			  /*//set the preferences using the seekbar_gpl variable value
      			  SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
      			  SharedPreferences.Editor editor = sharedPreferences.edit();
      			  editor.putInt("gpl_headphone_gain", gpl_headphone_gain);
      			  editor.commit(); */
      			  
      			// Try catch block for if it may go wrong 
      			try {
      				Log.d(TAG,"echo gpl_headphone_gain: "+ gpl_headphone_gain);
      				//calls RootProcess
					RootProcess process = new RootProcess();
					if (!process.init()) {
					    return;
					    }
					// Writing the values to the files
					process.write("echo "+ gpl_headphone_gain +" "+ gpl_headphone_gain +" > /sys/kernel/sound_control/gpl_headphone_gain\n");
					Log.d(TAG,"echo gpl_headphone_gain: "+ gpl_headphone_gain);
					//Value_gpl_headphone_gain = Integer.parseInt(vCheck_gpl_headphone_gain.read(rootProcess));
		        	process.term();
				} catch (Exception e) {
					Log.e(TAG,"Error crashed "+e);
					Log.d(TAG,"error "+e);
					}
      			}
      			@Override
      			public void onStartTrackingTouch(SeekBar seekBar){}

      			@Override
      			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
      			{
      				// sets the minimal level
      				int siz=progress+20;
      				gpl_headphone_gain = siz;
      				Log.d(TAG,"Progressbar: "+siz);
      				Log.d(TAG,"Progressbar: "+gpl_headphone_gain);

      				// change progress text label with current seekbar value
      				textgplheadphoneProcess.setText(""+gpl_headphone_gain);
      			}
      		});
        
        //register OnSeekBarChangeListener, so it can actually change values
        seekbar_hdmi_spkr_gain.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
      		{
      			@Override
      			public void onStopTrackingTouch(SeekBar seekBar) 
      			{
      			   // TODO Maybe no function for this in the future
      			  /*//set the preferences using the seekbar_gpl variable value
      			  SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
      			  SharedPreferences.Editor editor = sharedPreferences.edit();
      			  editor.putInt("gpl_hdmi_spkr_gain", gpl_hdmi_spkr_gain);
      			  editor.commit(); */
      			  
      			// Try catch block for if it may go wrong 
      			try {
      				Log.d(TAG,"echo gpl hdmi spkr gain: "+ gpl_hdmi_spkr_gain);
      				//calls RootProcess
					RootProcess process = new RootProcess();
					if (!process.init()) {
						return;
						}
					// Writing the values to the files
					process.write("echo "+ gpl_hdmi_spkr_gain +" > /sys/kernel/sound_control/gpl_hdmi_spkr_gain\n");
					Log.d(TAG,"echo gpl hdmi spkr gain: "+ gpl_hdmi_spkr_gain);
					process.term();
				} catch (Exception e) {
					Log.e(TAG,"Error crashed "+e);
					Log.d(TAG,"error "+e);
					}
      			}
      			@Override
      			public void onStartTrackingTouch(SeekBar seekBar){}
     			
      			@Override
      			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
      			{
      				// sets the minimal level
      				int siz=progress+20;
      				gpl_hdmi_spkr_gain = siz;
      				Log.d(TAG,"Progressbar: "+siz);
      				Log.d(TAG,"Progressbar: "+gpl_hdmi_spkr_gain);
     				
      				// change progress text label with current seekbar value
      				textgplhdmispkrgainProgress.setText(""+gpl_hdmi_spkr_gain);
      			}
      		});	
        
        //register OnSeekBarChangeListener, so it can actually change values
        seekbar_headset_mic_gain.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
      		{
      			@Override
      			public void onStopTrackingTouch(SeekBar seekBar) 
      			{
         			// TODO Maybe no function for this in the future
      			  /*//set the preferences using the seekbar_gpl variable value
      			  SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
      			  SharedPreferences.Editor editor = sharedPreferences.edit();
      			  editor.putInt("gpl_headset_mic_gain", gpl_headset_mic_gain);
      			  editor.commit(); */
      			  
      			// Try catch block for if it may go wrong 
      			try {
      				Log.d(TAG,"echo gpl_headset_mic_gain: "+ gpl_headset_mic_gain);
      				//calls RootProcess
					RootProcess process = new RootProcess();
					if (!process.init()) {
					    return;
					    }
					// Writing the values to the files
					process.write("echo "+ gpl_headset_mic_gain +" > /sys/kernel/sound_control/gpl_headset_mic_gain\n");
					Log.d(TAG,"echo gpl_headset_mic_gain: "+ gpl_headset_mic_gain);
					process.term();
				} catch (Exception e) {
					Log.e(TAG,"Error crashed "+e);
					Log.d(TAG,"error "+e);
					}
      			}
      			
      			@Override
      			public void onStartTrackingTouch(SeekBar seekBar){}
     			
      			@Override
      			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
      			{
      				// sets the minimal level
      				int siz=progress+20;
      				gpl_headset_mic_gain = siz;
      				Log.d(TAG,"Progressbar: "+siz);
      				Log.d(TAG,"Progressbar: "+gpl_headset_mic_gain);
     				
      				// change progress text label with current seekbar value
      				textgplheadsetmicgainProgress.setText(""+gpl_headset_mic_gain);
      			}
      		});


		// Filechecking part
        soundtweaks_hide_dialog = sharedPreferences.getInt("soundtweaks_hide_dialog", 0);
        Log.d(TAG,"onCreate cpu_hide_dialog = "+soundtweaks_hide_dialog);

        FileCheck.CheckSoundOptions(SoundTweaks.this);
		OptionsHider();

		if (FileCheck.incompatible == true) {
			if(soundtweaks_hide_dialog == 1){
	    		Log.d(TAG,"hide the dialog");
	    	} else {
	    		Log.d(TAG,"show dialog");
	    		Intent intent = new Intent(SoundTweaks.this, DialogActivity.class);
	    		startActivityForResult(intent, GET_CODE);
	    	}
			Log.d(TAG,"incompatible = "+FileCheck.incompatible);
		} else {
			Log.d(TAG,"incompatible = "+FileCheck.incompatible);
        }
	}

	// Start on boot switch
	public void onBoot(View view) {
	     // Is the toggle on?     
		 boolean on = ((Switch) view).isChecked();
		 if (on) {
			 Log.d(TAG, "onBoot Enabled for SoundTweaks");
			 //Toast.makeText(this,"Sorry Function is not implemented yet !",Toast.LENGTH_LONG).show();
			 //Toast.makeText(this,"!!!! BE PATIENT !!!!",Toast.LENGTH_SHORT).show();
			 //Toast.makeText(this,"...  xD  ...",Toast.LENGTH_LONG).show();
		 // Enable vibrate     
		 } else {
			 Log.d(TAG, "onBoot Disabled for SoundTweaks");
			 //Toast.makeText(this,"Sorry Function is not implemented yet !",Toast.LENGTH_LONG).show();
			 //Toast.makeText(this,"!!!! BE PATIENT !!!!",Toast.LENGTH_SHORT).show();
			 //Toast.makeText(this,"...  xD  ...",Toast.LENGTH_LONG).show();
		 // Disable vibrate    
		 }
		}

	static void OptionsHider() {

		Log.d("gpl_spk_hide","OptionsHider() gpl_spk_hide = "+FileCheck.gpl_spk_hide);
  		if(FileCheck.gpl_spk_hide == 1) {
  			seekbar_gpl.setVisibility(View.GONE);
  	  		textProgress.setVisibility(View.GONE);
  	  	textuncompatibel.setText(R.string.disabled_option_text);
  		}

  		Log.d("gpl_mic_hide","OptionsHider() gpl_mic_hide = "+FileCheck.gpl_mic_hide);
  		if(FileCheck.gpl_mic_hide == 1) {
  			seekbar_mic_gain.setVisibility(View.GONE);
  			textgplmicProgress.setVisibility(View.GONE);
  			textuncompatibel2.setText(R.string.disabled_option_text);
  		}

  		Log.d("gpl_mic_hide","OptionsHider() gpl_cam_mic_hide = "+FileCheck.gpl_cam_mic_hide);
		if(FileCheck.gpl_cam_mic_hide == 1) {
			seekbar_cam_mic_gain.setVisibility(View.GONE);
			textgplcammicProgress.setVisibility(View.GONE);
			textuncompatibel3.setText(R.string.disabled_option_text);
		}

		Log.d("gpl_mic_hide","OptionsHider() gpl_headphone_hide = "+FileCheck.gpl_headphone_hide);
		if(FileCheck.gpl_headphone_hide == 1) {
			seekbar_headphone_gain.setVisibility(View.GONE);
			textgplheadphoneProcess.setVisibility(View.GONE);
			textuncompatibel4.setText(R.string.disabled_option_text);
		}

		Log.d("gpl_mic_hide","OptionsHider() gpl_hdmi_spkr_hide = "+FileCheck.gpl_hdmi_spkr_hide);
		if(FileCheck.gpl_hdmi_spkr_hide == 1) {
			seekbar_hdmi_spkr_gain.setVisibility(View.GONE);
			textgplhdmispkrgainProgress.setVisibility(View.GONE);
			textuncompatibel5.setText(R.string.disabled_option_text);
		}

		Log.d("gpl_mic_hide","OptionsHider() gpl_headset_mic_hide = "+FileCheck.gpl_headset_mic_hide);
		if(FileCheck.gpl_headset_mic_hide == 1) {
			seekbar_headset_mic_gain.setVisibility(View.GONE);
			textgplheadsetmicgainProgress.setVisibility(View.GONE);
			textuncompatibel6.setText(R.string.disabled_option_text);
		}
	}
	
	// Method Used for retreiving data from the AlertDialog
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	Intent data) {
		if (requestCode == GET_CODE) {
			if (resultCode == RESULT_CANCELED) {
				} else {
					@SuppressWarnings("unused")
					String resultlog = Integer.toString(resultCode);
	                if (data != null) {
	                	Log.d(TAG,"RESULT_DATA = "+data.getAction());
	                	SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
	                    SharedPreferences.Editor editor = sharedPreferences.edit();
	            		editor.putInt("soundtweaks_hide_dialog", Integer.parseInt(data.getAction()));
	            		editor.commit(); 
	                }
	            }
	        }
	    }
	// Definition of the one requestCode we use for receiving resuls.
	static final private int GET_CODE = 0;
}
