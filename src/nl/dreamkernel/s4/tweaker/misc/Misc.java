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

package nl.dreamkernel.s4.tweaker.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.dreamkernel.s4.tweaker.util.SysFs;
import nl.dreamkernel.s4.tweaker.util.RootProcess;
import nl.dreamkernel.s4.tweaker.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class Misc extends Activity  {
	    
	// variables for the Textviews
	private TextView InternalValue;
	private TextView ExternalValue;

	// Variables for file paths
	private static final SysFs vCheck_internalscheduler = new SysFs("/sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler");
	private static final SysFs vCheck_externalscheduler = new SysFs("/sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler");
	private static final SysFs vCheck_vibrator_intensity = new SysFs("/sys/vibrator/pwm_val");
	private static final SysFs vCheck_Usb_Fast_charge = new SysFs("/sys/kernel/fast_charge/force_fast_charge");
	//private static final SysFs vCheck_Usb_Fast_charge = new SysFs("/data/data/nl.dreamkernel.s4.tweaker/files/force_fast_charge");
	//private static final SysFs vCheck_internalscheduler = new SysFs("/data/data/nl.dreamkernel.s4.tweaker/files/internalscheduler");
	//private static final SysFs vCheck_externalscheduler = new SysFs("/data/data/nl.dreamkernel.s4.tweaker/files/externalscheduler");
		
	// variables storing the real file values
	private String file_value_internal;
	private String file_value_external;	
	private String file_value_temp;	
	private String file_value_temp2;
	private int value_vibrator;
	private int value_vibrator_temp;
	private boolean usb_switch_value;
	private int usb_switch_value_temp;

	
	//the seek bar variable
	private SeekBar seekbar_vibrator;
		
	// declare text label objects
    private TextView vibratorProgress;
    
	// declare text label objects
    private Switch usbfastchargeswitch;
	
	// variables to store the shared pref in
	private int InternalPrefValue;
	private int ExternalPrefValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.misctweaks);
		setTitle(R.string.menu_misc_tweaks);
		getActionBar().hide();
		
		// Find current value views
		InternalValue = (TextView)findViewById(R.id.InternalValue);
		ExternalValue = (TextView)findViewById(R.id.ExternalValue);
		vibratorProgress = (TextView)findViewById(R.id.value_vibrator_intensity);

		//get the seek bar
		seekbar_vibrator = (SeekBar) findViewById(R.id.sb_vibrator_intensity);
		
		// Start on boot switch   		
		usbfastchargeswitch = (Switch) findViewById(R.id.usb_fast_charge_switch); 
		
		//get the Shared Prefs
		final SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
		InternalPrefValue = sharedPreferences.getInt("InternalPref", 0);
		ExternalPrefValue = sharedPreferences.getInt("ExternalPref", 0);
		
		
		// read the files value
		ValueReader();
		
 		//set progress text
		vibratorProgress.setText(""+value_vibrator);
		
        //set the seek bar progress
		seekbar_vibrator.setKeyProgressIncrement(value_vibrator);
                 
        //sets the progress of the seek bar
		seekbar_vibrator.setProgress(value_vibrator);
		
		//register OnSeekBarChangeListener, so it can actually change values
		seekbar_vibrator.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
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
      				Log.d("process","vibrator: "+ value_vibrator);
      				//calls RootProcess
					RootProcess process = new RootProcess();
					if (!process.init()) {
					    return;
					}
					// Writing the values to the files 
					process.write("echo "+ value_vibrator +" > /sys/vibrator/pwm_val\n");
					Log.d("process","echo value_vibrator : "+ value_vibrator);
					process.term();
				} catch (Exception e) {
					Log.e("Error","crashed"+e);
					Log.d("Error","error"+e);
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
      				int siz=progress;
      				value_vibrator = siz;
      				Log.d("progress","Progressbar: "+siz);
      				Log.d("progress","Progressbar: "+value_vibrator);
     				
      				// change progress text label with current seekbar value
      				vibratorProgress.setText(""+value_vibrator);
      			}
      		});	
		
		// Dropdown menu for I/O Scheduler Internal
		Spinner sInternal = (Spinner) findViewById(R.id.internalspinner);

		ArrayAdapter<CharSequence> internaladapter = ArrayAdapter
				.createFromResource(this, R.array.ioInternal,
						android.R.layout.simple_spinner_item);
		internaladapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sInternal.setAdapter(internaladapter);
		//set the option based on the sharedprefs
		sInternal.setSelection(InternalPrefValue, true);
		sInternal.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
		    	SharedPreferences.Editor editor = sharedPreferences.edit();
      			editor.putInt("InternalPref", position);
      			editor.commit(); 
				Log.d("internaladapter","internaladapter: position=" + position + " id=" + id);
				
      			// Try catch block for if it may go wrong 
      			try {
      				//calls RootProcess
					RootProcess process = new RootProcess();
					if (!process.init()) {
					    return;
					}
					// Writing the selected value to file 
					switch (position){				     
				    case 0:
				    	process.write("echo noop > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
				    	Log.d("process","echo'd noop to internalscheduler");
						break;
				    case 1:
				    	process.write("echo deadline > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
				    	Log.d("process","echo'd deadline to internalscheduler");
				    	break;
				    case 2:
				    	process.write("echo cfq > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
				    	Log.d("process","echo'd cfq to internalscheduler");
				    	break;
				    case 3:
				    	process.write("echo bfq > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
				    	Log.d("process","echo'd bfq to internalscheduler");
				    	break;
				    case 4:
				    	process.write("echo fiops > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
				    	Log.d("process","echo'd fiops to internalscheduler");
				    	break;
				    case 5:
				    	process.write("echo sio > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
				    	Log.d("process","echo'd sio to internalscheduler");
				    	break;
				    default:
				    	break;
				     }

					process.term();
					ValueReader();
				} catch (Exception e) {
					Log.e("Error","crashed"+e);
					Log.d("Error","error"+e);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				Log.d("internaladapter","internaladapter: Nothing selected");
			}
		});
		
		

		
		// Dropdown menu for I/O Scheduler External
		Spinner sExternal = (Spinner) findViewById(R.id.externalspinner);
		
		ArrayAdapter<CharSequence> externaladapter = ArrayAdapter
				.createFromResource(this, R.array.ioExternal,
						android.R.layout.simple_spinner_item);
		externaladapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sExternal.setAdapter(externaladapter);
		//set the option based on the sharedprefs
		sExternal.setSelection(ExternalPrefValue, true);
		sExternal.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
								
		    	SharedPreferences.Editor editor = sharedPreferences.edit();
      			editor.putInt("ExternalPref", position);
      			editor.commit(); 
				Log.d("externaladapter","externaladapter: position=" + position + " id=" + id);
				
      				//calls RootProcess
					RootProcess process = new RootProcess();
					if (!process.init()) {
					    return;
					}
					// Writing the selected value to file 
					switch (position){				     
				    case 0:
						process.write("echo noop > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n");
						Log.d("process","echo'd noop to externalscheduler");
				    	break;
				    case 1:
				    	process.write("echo deadline > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n");
				       	Log.d("process","echo'd deadline to externalscheduler");
				    	break;
				    case 2:
				    	process.write("echo cfq > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n");
				       	Log.d("process","echo'd cfq to externalscheduler");
				    	break;
				    case 3:
				    	process.write("echo bfq > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n");
				       	Log.d("process","echo'd bfq to externalscheduler");
				    	break;
				    case 4:
				    	process.write("echo fiops > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n");
				       	Log.d("process","echo'd fiops to externalscheduler");
				    	break;
				    case 5:
				    	process.write("echo sio > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n");
				       	Log.d("process","echo'd sio to externalscheduler");
				    	break;
				    default:
				    	break;
				     }

					process.term();		
					ValueReader();
				
			}

			public void onNothingSelected(AdapterView<?> parent) {
				Log.d("externaladapter","externaladapter: Nothing selected");
			}
		});
		

	}
	
	void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

	private void ValueReader(){
		// Read in the Values from files
		RootProcess rootProcess = new RootProcess();
		Log.d("MiscTweaks", "Misc Tweaks, Root init s");
		rootProcess.init();
		Log.d("MiscTweaks", "Misc Tweaks, Root init e");

		if (vCheck_internalscheduler.exists()) {
			file_value_temp = vCheck_internalscheduler.read(rootProcess);
		
			Pattern p = Pattern.compile("\\[((.*?)\\])");
			Matcher m = p.matcher(file_value_temp);
			if (m.find()){
				file_value_internal = m.toMatchResult().group(0);
				file_value_internal = file_value_internal.replace("[", "").replace("]", "");
			}			
		} else { file_value_internal = "File Not Found"; 	}
		
		if (vCheck_externalscheduler.exists()) {
			file_value_temp2 = vCheck_externalscheduler.read(rootProcess);
			
			Pattern a = Pattern.compile("\\[((.*?)\\])");
			Matcher b = a.matcher(file_value_temp2);
			if (b.find()){
				file_value_external = b.toMatchResult().group(0);
				file_value_external = file_value_external.replace("[", "").replace("]", "");
			}
		} else { file_value_external = "File Not Found";	}
		
		 if (vCheck_vibrator_intensity.exists()) {
			 value_vibrator_temp = Integer.parseInt(vCheck_vibrator_intensity.read(rootProcess));
			 value_vibrator = value_vibrator_temp;
	        } else { value_vibrator = 0; }
		 
		 
		 if (vCheck_Usb_Fast_charge.exists()) {
			usb_switch_value_temp = Integer.parseInt(vCheck_Usb_Fast_charge.read(rootProcess));
			if (usb_switch_value_temp == 1) {
				usb_switch_value = true;
			}
			if (usb_switch_value_temp == 0) {
				usb_switch_value = false;
			}
			 //usb_switch_value_temp = Boolean.parseBoolean(vCheck_Usb_Fast_charge.read(rootProcess));
			 //usb_switch_value = usb_switch_value_temp;
			 Log.d("","Boolean usb_switch_value_temp = "+usb_switch_value_temp);
			 Log.d("","Boolean usb_switch_value = "+usb_switch_value);
			} else { usb_switch_value = false; 	}

		rootProcess.term();
		rootProcess = null;
		////
		
		// Set current value views
		InternalValue.setText(""+file_value_internal);
		ExternalValue.setText(""+file_value_external);
		usbfastchargeswitch.setChecked(usb_switch_value);
		 }
	
	// Start on boot switch
	public void onUSBFASTSWITCH(View view) {
	     // Is the toggle on?     
		 boolean on = ((Switch) view).isChecked();
		 if (on) {
			//calls RootProcess
				RootProcess process = new RootProcess();
				if (!process.init()) {
				    return;
				}
			 process.write("echo 1 > /sys/kernel/fast_charge/force_fast_charge\n");
				//process.write("echo 1 > /data/data/nl.dreamkernel.s4.tweaker/files/force_fast_charge\n");
			 Log.d("onUSBFASTSWITCH", "on USB FAST SWITCH Enabled");
			 process.term();		
			 ValueReader();

		     
		 } else {
				//calls RootProcess
				RootProcess process = new RootProcess();
				if (!process.init()) {
				    return;
				}
			 process.write("echo 0 > /sys/kernel/fast_charge/force_fast_charge\n");
			//process.write("echo 0 > /data/data/nl.dreamkernel.s4.tweaker/files/force_fast_charge\n");
			 Log.d("onUSBFASTSWITCH", "on USB FAST SWITCH Disabled");
			 process.term();		
			 ValueReader();
		
		 }
		 }
}
