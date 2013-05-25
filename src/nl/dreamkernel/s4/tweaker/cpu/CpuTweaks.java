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

package nl.dreamkernel.s4.tweaker.cpu;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class CpuTweaks extends Activity {
	
	// variables for the Textviews
	private TextView CpuCurrentValue;
	
	// Variables for file paths
	private static final SysFs vCheck_CPU_GOVERNOR = new SysFs("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
	//private static final SysFs vCheck_CPU_GOVERNOR = new SysFs("/data/data/nl.dreamkernel.s4.tweaker/files/scaling_governor");

	// variables storing the real file values
	private String file_CPU_GOVERNOR;
	private String file_CPU_GOVERNOR_temp;	
	
	// variables to store the shared pref in
	private int CpuGovernorPrefValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.cputweaks);
		setTitle(R.string.menu_cpu_tweaks);
		getActionBar().hide();
		
		// Find current value views
		CpuCurrentValue = (TextView)findViewById(R.id.CpuCurrentValue);
		
		//get the Shared Prefs
		final SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
		CpuGovernorPrefValue = sharedPreferences.getInt("CpuGovernorPref", 0);
		
		// read the files value
		ValueReader();
		
		// Dropdown menu for I/O Scheduler Internal
				Spinner sCPUspinner = (Spinner) findViewById(R.id.cpuspinner);

				ArrayAdapter<CharSequence> internaladapter = ArrayAdapter
						.createFromResource(this, R.array.CPUgovernorArray,
								android.R.layout.simple_spinner_item);
				internaladapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sCPUspinner.setAdapter(internaladapter);
				//set the option based on the sharedprefs
				sCPUspinner.setSelection(CpuGovernorPrefValue, true);
				sCPUspinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent, View view,
							int position, long id) {
				    	SharedPreferences.Editor editor = sharedPreferences.edit();
		      			editor.putInt("CpuGovernorPref", position);
		      			editor.commit(); 
						Log.d("cpuprefadapter","CpuGovernorPref: position=" + position + " id=" + id);
						
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
						    	process.write("echo msm-dcvs > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
						    	Log.d("cpuprefadapter","echo'd msm-dcvs to  CPU Governor");
								break;
						    case 1:
						    	process.write("echo intellidemand > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
						    	Log.d("cpuprefadapter","echo'd intellidemand to  CPU Governor");
								break;
						    case 2:
						    	process.write("echo interactive > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
						    	Log.d("cpuprefadapter","echo'd interactive to  CPU Governor");
								break;
						    case 3:
						    	process.write("echo conservative > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
						    	Log.d("cpuprefadapter","echo'd conservative to  CPU Governor");
								break;
						    case 4:
						    	process.write("echo ondemand > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
						    	Log.d("cpuprefadapter","echo'd ondemand to  CPU Governor");
								break;
						    case 5:
						    	process.write("echo userspace > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
						    	Log.d("cpuprefadapter","echo'd userspace to  CPU Governor");
								break;
						    case 6:
						    	process.write("echo powersave > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
						    	Log.d("cpuprefadapter","echo'd powersave to  CPU Governor");
								break;
						    case 7:
						    	process.write("echo performance > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
						    	Log.d("cpuprefadapter","echo'd performance to  CPU Governor");
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
						Log.d("cpuprefadapter","cpuprefadapter: Nothing selected");
					}
				});
	}

	void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
	
	private void ValueReader(){
		// Read in the Values from files
		RootProcess rootProcess = new RootProcess();
		Log.d("CPUTweaks", "CPU Tweaks, Root init s");
		rootProcess.init();
		Log.d("CPUTweaks", "CPU Tweaks, Root init e");

		if (vCheck_CPU_GOVERNOR.exists()) {
			file_CPU_GOVERNOR_temp = vCheck_CPU_GOVERNOR.read(rootProcess);
			file_CPU_GOVERNOR = file_CPU_GOVERNOR_temp;					
		} else { file_CPU_GOVERNOR = "File Not Found"; 	}
			
		rootProcess.term();
		rootProcess = null;
		////
		
		// Set current value views
		CpuCurrentValue.setText(""+file_CPU_GOVERNOR);
		
		 }
	

}
