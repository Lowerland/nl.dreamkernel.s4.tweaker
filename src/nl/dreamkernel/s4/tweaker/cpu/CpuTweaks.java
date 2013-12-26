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

import java.util.ArrayList;
import com.google.analytics.tracking.android.EasyTracker;

import nl.dreamkernel.s4.tweaker.util.DialogActivity;
import nl.dreamkernel.s4.tweaker.util.FileCheck;
import nl.dreamkernel.s4.tweaker.util.OptionsHider;
import nl.dreamkernel.s4.tweaker.util.RootCheck;
import nl.dreamkernel.s4.tweaker.util.SysFs;
import nl.dreamkernel.s4.tweaker.util.RootProcess;
import nl.dreamkernel.s4.tweaker.R;
//import nl.dreamkernel.s4.tweaker.cpu.CpuFreqSorting;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class CpuTweaks extends Activity {
	static final String TAG = "S4Tweaker";
	static RootProcess rootProcess = new RootProcess();

	// variables for the Textviews
	public static TextView CpuCurrentValue;
	public static TextView CpuMinFREQValue;
	public static TextView CpuMaxFREQValue;
	public static TextView textuncompatibel;
	public static TextView textuncompatibel2;
	public static TextView textuncompatibel3;

	// variables for touch blocks
	public static View Touch_block_governor;
	public static View Touch_block_min_freq;
	public static View Touch_block_max_freq;

	// Variables for file paths

	public static final SysFs vCheck_SCALING_AVAILABLE_GOVERNOR = new SysFs("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors");
	public static final SysFs vCheck_CPU_GOVERNOR = new SysFs("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");

	public static final SysFs vCheck_CPU_AVAILABLE_FREQ_PATH = new SysFs("/sys/power/cpufreq_table");
	public static final SysFs vCheck_CPU_AVAILABLE_FREQ_OPTIONAL_PATH = new SysFs("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");
	public static final SysFs vCheck_CPU_CpuMinFREQ = new SysFs("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
	public static final SysFs vCheck_CPU_CpuMaxFREQ = new SysFs("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");

	public static final SysFs vCheck_CPU_AVAILABLE = new SysFs("/sys/devices/system/cpu/present");
	public static final SysFs vCheck_CPU0_ONLINE = new SysFs("/sys/devices/system/cpu/cpu0/online");
	public static final SysFs vCheck_CPU1_ONLINE = new SysFs("/sys/devices/system/cpu/cpu1/online");
	public static final SysFs vCheck_CPU2_ONLINE = new SysFs("/sys/devices/system/cpu/cpu2/online");
	public static final SysFs vCheck_CPU3_ONLINE = new SysFs("/sys/devices/system/cpu/cpu3/online");

	public static final SysFs vCheck_CPU0_CUR_FREQ = new SysFs("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
	public static final SysFs vCheck_CPU1_CUR_FREQ = new SysFs("/sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq");
	public static final SysFs vCheck_CPU2_CUR_FREQ = new SysFs("/sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq");
	public static final SysFs vCheck_CPU3_CUR_FREQ = new SysFs("/sys/devices/system/cpu/cpu3/cpufreq/scaling_cur_freq");

	// public static final SysFs vCheck_CPU_GOVERNOR = new
	// SysFs("/mnt/sdcard/testfiles/scaling_governor");
	// public static final SysFs vCheck_CPU_AVAILABLE_FREQ_PATH = new
	// SysFs("/storage/sdcard1/testfiles/cpufreq_table");
	// public static final SysFs vCheck_CPU_AVAILABLE_FREQ_OPTIONAL_PATH = new
	// SysFs("/storage/sdcard1/testfiles/scaling_available_frequencies");
	// public static final SysFs vCheck_CPU_CpuMinFREQ = new SysFs(
	// "/storage/sdcard1/testfiles/scaling_min_freq");
	// public static final SysFs vCheck_CPU_CpuMaxFREQ = new SysFs(
	// "/storage/sdcard1/testfiles/scaling_max_freq");

	// public static final SysFs vCheck_CPU1_ONLINE = new SysFs(
	// "/storage/sdcard1/testfiles/cpu1_online");
	// public static final SysFs vCheck_CPU2_ONLINE = new SysFs(
	// "/storage/sdcard1/testfiles/cpu2_online");
	// public static final SysFs vCheck_CPU3_ONLINE = new SysFs(
	// "/storage/sdcard1/testfiles/cpu3_online");

	// variables storing the real file values
	private static String file_CPU_GOVERNOR;
	private static String file_CPU_GOVERNOR_temp;
	private static String file_CPU_MinFREQ;
	private static int file_CPU_MinFREQ_temp;
	private static String file_CPU_MaxFREQ;
	private static int file_CPU_MaxFREQ_temp;

	private static int file_CPU1_ONLINE;
	private static int file_CPU1_ONLINE_temp;
	private static int file_CPU2_ONLINE;
	private static int file_CPU2_ONLINE_temp;
	private static int file_CPU3_ONLINE;
	private static int file_CPU3_ONLINE_temp;
	private static int CPU1_RETURN_STATE;
	private static int CPU2_RETURN_STATE;
	private static int CPU3_RETURN_STATE;

	// variables to store the shared pref in
	public static int CpuGovernorPrefValue;
	public static int CpuMinFREQPrefValue;
	public static int CpuMaxFREQPrefValue;
	public static int Cpu_Available;
	public static int cpu_hide_dialog;

	// TEMP Int used by dialogs
	private static int dialog_temp_cpu_gov;
	private static int dialog_temp_min_scheduler;
	private static int dialog_temp_max_scheduler;

	// Variables for setOnBoot
	public Switch onBootSwitch_CpuTweaks;
	public boolean onBootCpuTweaks_pref;

	public static boolean noRoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cputweaks);
		setTitle(R.string.menu_cpu_tweaks);
		getActionBar().hide();

		noRoot = false;

		RootCheck rootcheck = new RootCheck();
		if (!rootcheck.init()) {
			noRoot = true;
			finish();
			return;
		} else {
		}

		rootProcess.init(); // Must be initialized here

		final SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);

		// Find current value views
		CpuCurrentValue = (TextView) findViewById(R.id.CpuCurrentValue);
		CpuMinFREQValue = (TextView) findViewById(R.id.CpuMinFreqValue);
		CpuMaxFREQValue = (TextView) findViewById(R.id.CpuMaxFreqValue);

		// Find Views
		textuncompatibel = (TextView) findViewById(R.id.uncompatible_alert);
		textuncompatibel2 = (TextView) findViewById(R.id.uncompatible_alert2);
		textuncompatibel3 = (TextView) findViewById(R.id.uncompatible_alert3);

		// Find Thouch Blocks so we can could disable them
		Touch_block_governor = (View) findViewById(R.id.cpugovernortouchblock);
		Touch_block_min_freq = (View) findViewById(R.id.minfreqscalingtouchblock);
		Touch_block_max_freq = (View) findViewById(R.id.maxfreqscalingtouchblock);

		// get the Shared Prefs
		CpuGovernorPrefValue = sharedPreferences.getInt("CpuGovernorPref", -1);	// -1 so if no value nothing gets selected

		CpuMinFREQPrefValue = sharedPreferences.getInt("CpuMinFREQPref", -1);	// -1 so if no value nothing gets selected
		CpuMaxFREQPrefValue = sharedPreferences.getInt("CpuMaxFREQPref", -1);	// -1 so if no value nothing gets selected

		Cpu_Available = sharedPreferences.getInt("Cpu_Available", 0);

		// get onBoot Pref
		onBootCpuTweaks_pref = sharedPreferences.getBoolean("onBootCpuTweaks_pref", false);

		// on boot switch
		onBootSwitch_CpuTweaks = (Switch) findViewById(R.id.onBootSwitch_CpuTweaks);
		onBootSwitch_CpuTweaks.setChecked(onBootCpuTweaks_pref);
		onBootSwitch_CpuTweaks
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							// The toggle is enabled
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putBoolean("onBootCpuTweaks_pref", true);
							editor.commit();
							//Log.d(TAG, "onBoot Enabled");
						} else {
							// The toggle is disabled
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putBoolean("onBootCpuTweaks_pref", false);
							editor.commit();
							//Log.d(TAG, "onBoot Disabled");
						}
					}
				});

		// read the files value
		ValueReader();
		AvailableFreqReader();

		// Filechecking part
		cpu_hide_dialog = sharedPreferences.getInt("cpu_hide_dialog", 0);
		//Log.d(TAG, "onCreate cpu_hide_dialog = " + cpu_hide_dialog);

		// Options Compatible Check
		FileCheck.CheckCPUOptions(CpuTweaks.this);

		// Hide Options if it isn't compatible
		OptionsHider.CpuTweaksHider(CpuTweaks.this);

		if (FileCheck.incompatible == true) {
			if (cpu_hide_dialog == 1) {
				// Log.d(TAG, "hide the dialog");
			} else {
				// Log.d(TAG, "show dialog");
				Intent intent = new Intent(CpuTweaks.this, DialogActivity.class);
				startActivityForResult(intent, GET_CODE);
			}
			//Log.d(TAG, "incompatible = " + FileCheck.incompatible);
		} else {
			//Log.d(TAG, "incompatible = " + FileCheck.incompatible);
		}
	}


	String[] AvailableGovernors = getAvailableGovernors();
	public static String cpu_scaling_governor_array;

	public String[] getAvailableGovernors() {
		String values = vCheck_SCALING_AVAILABLE_GOVERNOR.read(rootProcess);
		if (values != null) {
			return values.split(" ");
		}
		return null;
	}

	public void onCPUGOVERNOR(View View) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(AvailableGovernors, CpuGovernorPrefValue,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {

						/* User clicked on a radio button do some stuff */
						//Log.d(TAG, "User clicked on radio button "+ whichButton);
						dialog_temp_cpu_gov = whichButton;
						cpu_scaling_governor_array = AvailableGovernors[whichButton];

					}
				});
		final AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.cpu_governor);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {

						SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putInt("CpuGovernorPref", dialog_temp_cpu_gov);
						//editor.putString("cpu_scaling_governor_array", cpu_scaling_governor_array);
						editor.commit();

						CPUGovernorDialogSaver();
						ValueReader();
					}
				});
		alertDialog.show();
	}

	private void CPUGovernorDialogSaver() {

		// Write Values to the filesystem
		rootProcess.write("chmod 664 /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
		rootProcess.write("echo " + cpu_scaling_governor_array + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");

		SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("cpu_scaling_governor_pref",cpu_scaling_governor_array);
		editor.commit();
	}


	//String AvailableFrequencies;

	public static String cpu_min_freq_array;

	String[] AvailableMinFrequencies;
	String[] availableMinFreqEntries;

	String[] Sorted_Min_Freq_Entries_Array;
	String[] Sorted_Min_Frequencies_Array;

	public String[] getAvailableMinFrequencies() {
		String values = null;
		if (CpuTweaks.vCheck_CPU_AVAILABLE_FREQ_PATH.exists()) {
			//rootProcess.write("chmod 664 /sys/power/cpufreq_table\n");
			values = vCheck_CPU_AVAILABLE_FREQ_PATH.read(rootProcess);
		}

		if (CpuTweaks.vCheck_CPU_AVAILABLE_FREQ_OPTIONAL_PATH.exists()) {
			//rootProcess.write("chmod 664 /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies\n");
			values = vCheck_CPU_AVAILABLE_FREQ_OPTIONAL_PATH.read(rootProcess);
		}

		if (values != null) {
			String[] items = values.replaceAll(" ", ",").split(",");
			int[] results = new int[items.length];

			// Convert String array to int array
			for (int i = 0; i < items.length; i++) {
			    try {
			        results[i] = Integer.parseInt(items[i]);
			    } catch (NumberFormatException nfe) {};
			}

			// Sorting the new int array
			int j=0;
			int hold;
			boolean looper= false;

			//sort the array low to high
			//using a bubble sort method
			while((looper==false)&&(j<results.length)){
				for(int i=0; i<results.length-1;i++){
					if(results[i]>results[i+1]){
						hold=results[i];
						results[i]=results[i+1];
						results[i+1]=hold;
					}
				}

				// If it is done shifting numbers
				// between the list slots and the hold slot
				if(j==results.length-1) {
					looper=true;
				} else {
					j++; // If list was shifted then keep shifting until everything is in place
				}
			}

			// Convert int array back to a String array
			String[] results2 = new String[results.length];
			for (int i = 0; i < results.length; i++) {
			    try {
			        results2[i] = String.valueOf(results[i]);
			    } catch (NumberFormatException nfe) {};
			}
			return results2;
		}
		return null;
	}

	// Making the Freqs more human readable
	public static String[] getFrequencyMinEntries(String[] availableMinFrequencies2) {
		ArrayList<String> list = new ArrayList<String>();
        if (availableMinFrequencies2 != null) {
                for (String freq : availableMinFrequencies2) {
                        list.add(String.valueOf(Integer.parseInt(freq) / 1000) + " MHz");
                }
                return list.toArray(new String[0]);
        }
        return null;
	}

	/*public static String[] getAvailableMinFrequencies(int[] availableFrequencies2) {
		ArrayList<String> list = new ArrayList<String>();
		if (availableFrequencies2 != null) {
			for (int freq : availableFrequencies2) {
				//Collections.sort(list, Collections.reverseOrder());  //Reverses the cpu freq list
				list.add(String.valueOf(freq));
			}
			return list.toArray(new String[0]);
		}
		return null;
	}*/

	public void onMINFREQSCALING(View View) {
//TODO
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(Sorted_Min_Freq_Entries_Array,
		//builder.setSingleChoiceItems(availableMinFreqEntries,
				CpuMinFREQPrefValue, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {

						/* User clicked on a radio button do some stuff */
						//Log.d(TAG, "User clicked on radio button "+ whichButton);
						dialog_temp_min_scheduler = whichButton;
						cpu_min_freq_array = Sorted_Min_Frequencies_Array[whichButton];
						//Log.d(TAG,"Sorted_Min_Frequencies_Array = "+Sorted_Min_Frequencies_Array[whichButton]);
						//Log.d(TAG,"Sorted_Min_Freq_Entries_Array = "+Sorted_Min_Freq_Entries_Array[whichButton]);

					}
				});
		final AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.min_frequency_scaling);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {

						SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putInt("CpuMinFREQPref",dialog_temp_min_scheduler);
						//editor.putString("cpu_min_freq_array",cpu_min_freq_array);
						editor.commit();

						MIN_FREQ_DialogSaver();
						// ValueReader();
					}
				});
		alertDialog.show();
	}


	private void MIN_FREQ_DialogSaver() {

		// read values for cpu's Online check
		ValueReader();

		// Cpu's Online state and Force Online if the are Offline
		CpuCurrentState();

		//rootProcess.write("chmod 664 /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
		// .write("chmod 664 storage/sdcard1/testfiles/scaling_min_freq\n");

		SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
		Cpu_Available = sharedPreferences.getInt("Cpu_Available", 0);

		// Write Values to the filesystem
		if (Cpu_Available > -1){
			//Log.d(TAG, "Writing to cpu0 *");
			rootProcess.write("echo " + cpu_min_freq_array + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
		}
		if (Cpu_Available > 0){
			//Log.d(TAG, "Writing to cpu1 **");
			rootProcess.write("echo " + cpu_min_freq_array + " > /sys/devices/system/cpu/cpu1/cpufreq/scaling_min_freq\n");
		}
		if (Cpu_Available > 1){
			//Log.d(TAG, "Writing to cpu2 ***");
			rootProcess.write("echo " + cpu_min_freq_array + " > /sys/devices/system/cpu/cpu2/cpufreq/scaling_min_freq\n");
		}
		if (Cpu_Available > 2){
			//Log.d(TAG, "Writing to cpu3 ****");
			rootProcess.write("echo " + cpu_min_freq_array + " > /sys/devices/system/cpu/cpu3/cpufreq/scaling_min_freq\n");
		}


		//rootProcess.write("echo " + cpu_min_freq_array + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
		//rootProcess.write("echo " + cpu_min_freq_array + " > /sys/devices/system/cpu/cpu1/cpufreq/scaling_min_freq\n");
		//rootProcess.write("echo " + cpu_min_freq_array + " > /sys/devices/system/cpu/cpu2/cpufreq/scaling_min_freq\n");
		//rootProcess.write("echo " + cpu_min_freq_array + " > /sys/devices/system/cpu/cpu3/cpufreq/scaling_min_freq\n");
		// rootProcess.write("echo " + cpu_min_freq_array
		// + " > storage/sdcard1/testfiles/scaling_min_freq\n");

		//SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("scaling_min_freq_pref", cpu_min_freq_array);
		editor.commit();
		ReturnCpuState(); // Return Cpu State
		ValueReader(); // read values for Gui
	}

	public static String cpu_max_freq_array;
	String[] AvailableMaxFrequencies;
	String[] availableMaxFreqEntries;

	String[] Sorted_Max_Freq_Entries_Array;
	String[] Sorted_Max_Frequencies_Array;

	// Making the Freqs more human readable
	public static String[] getFrequencyMaxEntries(String[] availableMaxFrequencies2) {
		ArrayList<String> list = new ArrayList<String>();
		if (availableMaxFrequencies2 != null) {
			for (String freq : availableMaxFrequencies2) {
				list.add(String.valueOf(Integer.parseInt(freq) / 1000) + " MHz");
			}
			return list.toArray(new String[0]);
		}
		return null;
	}

	public String[] getAvailableMaxFrequencies() {
		String values = null;
		if (CpuTweaks.vCheck_CPU_AVAILABLE_FREQ_PATH.exists()) {
			//rootProcess.write("chmod 664 /sys/power/cpufreq_table\n");
			values = vCheck_CPU_AVAILABLE_FREQ_PATH.read(rootProcess);
		}

		if (CpuTweaks.vCheck_CPU_AVAILABLE_FREQ_OPTIONAL_PATH.exists()) {
			//rootProcess.write("chmod 664 /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies\n");
			values = vCheck_CPU_AVAILABLE_FREQ_OPTIONAL_PATH.read(rootProcess);
		}

		if (values != null) {
			String[] items = values.replaceAll(" ", ",").split(",");
			int[] results = new int[items.length];

			// Convert String array to int array
			for (int i = 0; i < items.length; i++) {
			    try {
			        results[i] = Integer.parseInt(items[i]);
			    } catch (NumberFormatException nfe) {};
			}

			// Sorting the new int array
			int j=0;
			int hold;
			boolean looper= false;

			//sort the array high to low
			//using a bubble sort method
			while((looper==false)&&(j<results.length)){
				for(int i=0; i<results.length-1;i++){
					if(results[i]<results[i+1]){
						hold=results[i];
						results[i]=results[i+1];
						results[i+1]=hold;
					}
				}

				// If it is done shifting numbers
				// between the list slots and the hold slot
				if(j==results.length-1) {
					looper=true;
				} else {
					j++; // If list was shifted then keep shifting until everything is in place
				}
			}

			// Convert int array back to a String array
			String[] results2 = new String[results.length];
			for (int i = 0; i < results.length; i++) {
			    try {
			        results2[i] = String.valueOf(results[i]);
			    } catch (NumberFormatException nfe) {};
			}
			return results2;
		}
		return null;
	}

	public void onMAXFREQSCALING(View View) {
		// FIXME
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(Sorted_Max_Freq_Entries_Array,
		//builder.setSingleChoiceItems(availableMaxFreqEntries,
				CpuMaxFREQPrefValue, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {

						//Log.d(TAG, "User clicked on radio button " + whichButton);
						dialog_temp_max_scheduler = whichButton;
						cpu_max_freq_array = Sorted_Max_Frequencies_Array[whichButton];
						//Log.d(TAG,"Sorted_Max_Frequencies_Array = "+Sorted_Max_Frequencies_Array[whichButton]);
						//Log.d(TAG,"Sorted_Max_Freq_Entries_Array = "+Sorted_Max_Freq_Entries_Array[whichButton]);
					}
				});
		final AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.max_fequency_scaling);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {

						SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putInt("CpuMaxFREQPref",dialog_temp_max_scheduler);
						//editor.putString("cpu_max_freq_array",cpu_max_freq_array);
						editor.commit();

						MAX_FREQ_DialogSaver();
						// ValueReader();
					}
				});
		alertDialog.show();
	}

	private void MAX_FREQ_DialogSaver() {

		// read values for cpu's Online check
		ValueReader();

		// Cpu's Online state and Force Online if they are Offline
		CpuCurrentState();

		//rootProcess.write("chmod 664 /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
		// .write("chmod 664 storage/sdcard1/testfiles/scaling_max_freq\n");

		SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
		Cpu_Available = sharedPreferences.getInt("Cpu_Available", 0);

		// Write Values to the filesystem
		if (Cpu_Available > -1){
			//Log.d(TAG, "Writing to cpu0 *");
			rootProcess.write("echo " + cpu_max_freq_array + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
		}
		if (Cpu_Available > 0){
			//Log.d(TAG, "Writing to cpu1 **");
			rootProcess.write("echo " + cpu_max_freq_array + " > /sys/devices/system/cpu/cpu1/cpufreq/scaling_max_freq\n");
		}
		if (Cpu_Available > 1){
			//Log.d(TAG, "Writing to cpu2 ***");
			rootProcess.write("echo " + cpu_max_freq_array + " > /sys/devices/system/cpu/cpu2/cpufreq/scaling_max_freq\n");
		}
		if (Cpu_Available > 2){
			//Log.d(TAG, "Writing to cpu3 ****");
			rootProcess.write("echo " + cpu_max_freq_array + " > /sys/devices/system/cpu/cpu3/cpufreq/scaling_max_freq\n");
		}

		//rootProcess.write("echo " + cpu_max_freq_array + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
		//rootProcess.write("echo " + cpu_max_freq_array + " > /sys/devices/system/cpu/cpu1/cpufreq/scaling_max_freq\n");
		//rootProcess.write("echo " + cpu_max_freq_array + " > /sys/devices/system/cpu/cpu2/cpufreq/scaling_max_freq\n");
		//rootProcess.write("echo " + cpu_max_freq_array + " > /sys/devices/system/cpu/cpu3/cpufreq/scaling_max_freq\n");
		// rootProcess.write("echo " + cpu_max_freq_array
		// + " > storage/sdcard1/testfiles/scaling_max_freq\n");


		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString("scaling_max_freq_pref", cpu_max_freq_array);
		editor.commit();
		ReturnCpuState(); // Return Cpu State
		ValueReader(); // read values for Gui
	}

	void showToast(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public static void CpuCurrentState() {


		if (Cpu_Available > 0){
			file_CPU1_ONLINE_temp = Integer.parseInt(vCheck_CPU1_ONLINE.read(rootProcess));
			file_CPU1_ONLINE = file_CPU1_ONLINE_temp;
			//Log.d(TAG, "Read Cpu 1 State ");

			if (file_CPU1_ONLINE == 0) {
				rootProcess.write("echo 1 > /sys/devices/system/cpu/cpu1/online\n");
				CPU1_RETURN_STATE = 0;
				//Log.d(TAG, "Force CPU 1 ONLINE ");
			} else {
				CPU1_RETURN_STATE = 1;
				//Log.d(TAG, "CPU 1 is ONLINE ");
			}
		}
		if (Cpu_Available > 1){
			file_CPU2_ONLINE_temp = Integer.parseInt(vCheck_CPU2_ONLINE.read(rootProcess));
			file_CPU2_ONLINE = file_CPU2_ONLINE_temp;
			//Log.d(TAG, "Read Cpu 2 State ");

			if (file_CPU2_ONLINE == 0) {
				rootProcess.write("echo 1 > /sys/devices/system/cpu/cpu2/online\n");
				CPU2_RETURN_STATE = 0;
				//Log.d(TAG, "Force CPU 2 ONLINE ");
			} else {
				CPU2_RETURN_STATE = 1;
				//Log.d(TAG, "CPU 2 is ONLINE ");
			}
		}
		if (Cpu_Available > 2){
			file_CPU3_ONLINE_temp = Integer.parseInt(vCheck_CPU3_ONLINE.read(rootProcess));
			file_CPU3_ONLINE = file_CPU3_ONLINE_temp;
			//Log.d(TAG, "Read Cpu 3 State ");

			if (file_CPU3_ONLINE == 0) {
				rootProcess.write("echo 1 > /sys/devices/system/cpu/cpu3/online\n");
				CPU3_RETURN_STATE = 0;
				//Log.d(TAG, "Force CPU 3 ONLINE ");
			} else {
				CPU3_RETURN_STATE = 1;
				//Log.d(TAG, "CPU 3 is ONLINE ");
			}
		}

		/*if (vCheck_CPU1_ONLINE.exists()) {
			file_CPU1_ONLINE_temp = Integer.parseInt(vCheck_CPU1_ONLINE.read(rootProcess));
			file_CPU1_ONLINE = file_CPU1_ONLINE_temp;
			Log.d(TAG, "Read Cpu 1 State ");

			if (file_CPU1_ONLINE == 0) {
				rootProcess.write("echo 1 > /sys/devices/system/cpu/cpu1/online\n");
				CPU1_RETURN_STATE = 0;
				Log.d(TAG, "Force CPU 1 ONLINE ");
			} else {
				CPU1_RETURN_STATE = 1;
				Log.d(TAG, "CPU 1 is ONLINE ");
			}
		} else {
		}*/
		/*if (vCheck_CPU2_ONLINE.exists()) {
			file_CPU2_ONLINE_temp = Integer.parseInt(vCheck_CPU2_ONLINE.read(rootProcess));
			file_CPU2_ONLINE = file_CPU2_ONLINE_temp;
			Log.d(TAG, "Read Cpu 2 State ");

			if (file_CPU2_ONLINE == 0) {
				rootProcess.write("echo 1 > /sys/devices/system/cpu/cpu2/online\n");
				CPU2_RETURN_STATE = 0;
				Log.d(TAG, "Force CPU 2 ONLINE ");
			} else {
				CPU2_RETURN_STATE = 1;
				Log.d(TAG, "CPU 2 is ONLINE ");
			}
		} else {
		}*/
		/*if (vCheck_CPU3_ONLINE.exists()) {
			file_CPU3_ONLINE_temp = Integer.parseInt(vCheck_CPU3_ONLINE.read(rootProcess));
			file_CPU3_ONLINE = file_CPU3_ONLINE_temp;
			Log.d(TAG, "Read Cpu 3 State ");

			if (file_CPU3_ONLINE == 0) {
				rootProcess.write("echo 1 > /sys/devices/system/cpu/cpu3/online\n");
				CPU3_RETURN_STATE = 0;
				Log.d(TAG, "Force CPU 3 ONLINE ");
			} else {
				CPU3_RETURN_STATE = 1;
				Log.d(TAG, "CPU 3 is ONLINE ");
			}
		} else {
		}*/



	}

	public static void ReturnCpuState() {
		if (Cpu_Available > 0){
			if (CPU1_RETURN_STATE == 0) {
				//Log.d(TAG, "Force CPU 1 Back Offline ");
				rootProcess.write("echo 0 > /sys/devices/system/cpu/cpu1/online\n");
			}
		}
		if (Cpu_Available > 1){
			if (CPU2_RETURN_STATE == 0) {
				//Log.d(TAG, "Force CPU 2 Back Offline ");
				rootProcess.write("echo 0 > /sys/devices/system/cpu/cpu2/online\n");
			}
		}
		if (Cpu_Available > 2){
			if (CPU3_RETURN_STATE == 0) {
				//Log.d(TAG, "Force CPU 3 Back Offline ");
				rootProcess.write("echo 0 > /sys/devices/system/cpu/cpu3/online\n");
			}
		}
	}

	private void ValueReader() {
		SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);

		// Read in the Shared Prefs
		CpuGovernorPrefValue = sharedPreferences.getInt("CpuGovernorPref", -1); // -1 so if no value nothing gets selected
		CpuMinFREQPrefValue = sharedPreferences.getInt("CpuMinFREQPref", -1); // -1 so if no value nothing gets selected
		CpuMaxFREQPrefValue = sharedPreferences.getInt("CpuMaxFREQPref", -1); // -1 so if no value nothing gets selected
		Cpu_Available = sharedPreferences.getInt("Cpu_Available", 0);

		// Check how much cpu's are present
		if (vCheck_CPU_AVAILABLE.exists()) {
			SharedPreferences.Editor editor = sharedPreferences.edit();

			String temp = vCheck_CPU_AVAILABLE.read(rootProcess);
			if (temp.length() == 0) {
				editor.putInt("Cpu_Available", 0);
			}
			if (temp.length() > 0) {
				// This File hold 2 values example: 0-1
				// so it needs an different aproach
				try {
					//headphonevalueconvert = vCheck_gpl_headphone_gain
					//		.read(rootProcess);
					int nStartIndex = 2;
					int nEndIndex = 3;
					String substring = temp.substring(nStartIndex, nEndIndex);
					int temp_int = Integer.parseInt(substring);
					editor.putInt("Cpu_Available", temp_int);

				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			editor.commit();
		}



		// Read in the Values from files
		if (vCheck_CPU_GOVERNOR.exists()) {
			//rootProcess.write("chmod 664 /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			try {
				//if (vCheck_CPU_GOVERNOR.read(rootProcess) != null && vCheck_CPU_GOVERNOR.read(rootProcess).length() > 0) {
				file_CPU_GOVERNOR_temp = vCheck_CPU_GOVERNOR.read(rootProcess);
				file_CPU_GOVERNOR = file_CPU_GOVERNOR_temp;
				CpuCurrentValue.setText("" + file_CPU_GOVERNOR);
				//} else {
				//	file_CPU_GOVERNOR = "";
				//	CpuCurrentValue.setText("Problems Reading File value  :-/");
				//}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		if (vCheck_CPU_CpuMinFREQ.exists()) {
			//rootProcess.write("chmod 664 /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");

			try {
				//if (vCheck_CPU_CpuMinFREQ.read(rootProcess) != null && vCheck_CPU_CpuMinFREQ.read(rootProcess).length() > 0) {
				file_CPU_MinFREQ_temp = Integer.parseInt(vCheck_CPU_CpuMinFREQ.read(rootProcess));
				file_CPU_MinFREQ = file_CPU_MinFREQ_temp / 1000 + " MHz";
				CpuMinFREQValue.setText("" + file_CPU_MinFREQ);
				//} else {
				//	file_CPU_MinFREQ = "0";
				//	CpuMinFREQValue.setText("Problems Reading File value  :-/");
				//}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		if (vCheck_CPU_CpuMaxFREQ.exists()) {
			//rootProcess.write("chmod 664 /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");

			try {
				//if (vCheck_CPU_CpuMaxFREQ.read(rootProcess) != null && vCheck_CPU_CpuMaxFREQ.read(rootProcess).length() > 0) {
				file_CPU_MaxFREQ_temp = Integer.parseInt(vCheck_CPU_CpuMaxFREQ.read(rootProcess));
				file_CPU_MaxFREQ = file_CPU_MaxFREQ_temp / 1000 + " MHz";
				CpuMaxFREQValue.setText("" + file_CPU_MaxFREQ);
				//} else {
				//	file_CPU_MaxFREQ_temp = 0;
				//	CpuMaxFREQValue.setText("Problems Reading File value  :-/");
				//}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		if (vCheck_CPU1_ONLINE.exists()) {
			file_CPU1_ONLINE_temp = Integer.parseInt(vCheck_CPU1_ONLINE.read(rootProcess));
			file_CPU1_ONLINE = file_CPU1_ONLINE_temp;
			//Log.d(TAG, "Read Cpu 1 State " + file_CPU1_ONLINE);
		}
		if (vCheck_CPU2_ONLINE.exists()) {
			file_CPU2_ONLINE_temp = Integer.parseInt(vCheck_CPU2_ONLINE.read(rootProcess));
			file_CPU2_ONLINE = file_CPU2_ONLINE_temp;
			//Log.d(TAG, "Read Cpu 2 State " + file_CPU2_ONLINE);
		}
		if (vCheck_CPU3_ONLINE.exists()) {
			file_CPU3_ONLINE_temp = Integer.parseInt(vCheck_CPU3_ONLINE.read(rootProcess));
			file_CPU3_ONLINE = file_CPU3_ONLINE_temp;
			//Log.d(TAG, "Read Cpu 3 State " + file_CPU3_ONLINE);
		}

		// Set current value views
		//CpuCurrentValue.setText("" + file_CPU_GOVERNOR); // old
		//CpuMinFREQValue.setText("" + file_CPU_MinFREQ);  // old
		//CpuMaxFREQValue.setText("" + file_CPU_MaxFREQ);  // old

	}

	private void AvailableFreqReader() {
		if (vCheck_CPU_AVAILABLE_FREQ_PATH.exists()) {
			//rootProcess.write("chmod 444 /sys/power/cpufreq_table\n");
			//TODO
			try {
				//if (vCheck_CPU_AVAILABLE_FREQ_PATH.read(rootProcess) != null && vCheck_CPU_AVAILABLE_FREQ_PATH.read(rootProcess).length() > 0) {
					AvailableMinFrequencies = getAvailableMinFrequencies();
					availableMinFreqEntries = getFrequencyMinEntries(AvailableMinFrequencies);
					AvailableMaxFrequencies = getAvailableMaxFrequencies();
					availableMaxFreqEntries = getFrequencyMaxEntries(AvailableMaxFrequencies);

					Sorted_Min_Frequencies_Array = new String [5];
					{
						Sorted_Min_Frequencies_Array[0] = AvailableMinFrequencies[0];
						Sorted_Min_Frequencies_Array[1] = AvailableMinFrequencies[1];
						Sorted_Min_Frequencies_Array[2] = AvailableMinFrequencies[2];
						Sorted_Min_Frequencies_Array[3] = AvailableMinFrequencies[3];
						Sorted_Min_Frequencies_Array[4] = AvailableMinFrequencies[4];
					}

					Sorted_Min_Freq_Entries_Array = new String [5];
					{
						Sorted_Min_Freq_Entries_Array[0] = availableMinFreqEntries[0];
						Sorted_Min_Freq_Entries_Array[1] = availableMinFreqEntries[1];
						Sorted_Min_Freq_Entries_Array[2] = availableMinFreqEntries[2];
						Sorted_Min_Freq_Entries_Array[3] = availableMinFreqEntries[3];
						Sorted_Min_Freq_Entries_Array[4] = availableMinFreqEntries[4];
					}

					Sorted_Max_Frequencies_Array = new String [5];
					{
						Sorted_Max_Frequencies_Array[0] = AvailableMaxFrequencies[4];
						Sorted_Max_Frequencies_Array[1] = AvailableMaxFrequencies[3];
						Sorted_Max_Frequencies_Array[2] = AvailableMaxFrequencies[2];
						Sorted_Max_Frequencies_Array[3] = AvailableMaxFrequencies[1];
						Sorted_Max_Frequencies_Array[4] = AvailableMaxFrequencies[0];
					}

					Sorted_Max_Freq_Entries_Array = new String [5];
					{
						Sorted_Max_Freq_Entries_Array[0] = availableMaxFreqEntries[4];
						Sorted_Max_Freq_Entries_Array[1] = availableMaxFreqEntries[3];
						Sorted_Max_Freq_Entries_Array[2] = availableMaxFreqEntries[2];
						Sorted_Max_Freq_Entries_Array[3] = availableMaxFreqEntries[1];
						Sorted_Max_Freq_Entries_Array[4] = availableMaxFreqEntries[0];
					}
				//}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (vCheck_CPU_AVAILABLE_FREQ_OPTIONAL_PATH.exists()) {
			//rootProcess.write("chmod 444 /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies\n");
			//TODO
			try {
				//if (vCheck_CPU_AVAILABLE_FREQ_OPTIONAL_PATH.read(rootProcess) != null && vCheck_CPU_AVAILABLE_FREQ_OPTIONAL_PATH.read(rootProcess).length() > 0) {
					AvailableMinFrequencies = getAvailableMinFrequencies();
					availableMinFreqEntries = getFrequencyMinEntries(AvailableMinFrequencies);
					AvailableMaxFrequencies = getAvailableMaxFrequencies();
					availableMaxFreqEntries = getFrequencyMaxEntries(AvailableMaxFrequencies);

					Sorted_Min_Frequencies_Array = new String [5];
					{
						Sorted_Min_Frequencies_Array[0] = AvailableMinFrequencies[0];
						Sorted_Min_Frequencies_Array[1] = AvailableMinFrequencies[1];
						Sorted_Min_Frequencies_Array[2] = AvailableMinFrequencies[2];
						Sorted_Min_Frequencies_Array[3] = AvailableMinFrequencies[3];
						Sorted_Min_Frequencies_Array[4] = AvailableMinFrequencies[4];
					}

					Sorted_Min_Freq_Entries_Array = new String [5];
					{
						Sorted_Min_Freq_Entries_Array[0] = availableMinFreqEntries[0];
						Sorted_Min_Freq_Entries_Array[1] = availableMinFreqEntries[1];
						Sorted_Min_Freq_Entries_Array[2] = availableMinFreqEntries[2];
						Sorted_Min_Freq_Entries_Array[3] = availableMinFreqEntries[3];
						Sorted_Min_Freq_Entries_Array[4] = availableMinFreqEntries[4];
					}

					Sorted_Max_Frequencies_Array = new String [5];
					{
						Sorted_Max_Frequencies_Array[0] = AvailableMaxFrequencies[4];
						Sorted_Max_Frequencies_Array[1] = AvailableMaxFrequencies[3];
						Sorted_Max_Frequencies_Array[2] = AvailableMaxFrequencies[2];
						Sorted_Max_Frequencies_Array[3] = AvailableMaxFrequencies[1];
						Sorted_Max_Frequencies_Array[4] = AvailableMaxFrequencies[0];
					}

					Sorted_Max_Freq_Entries_Array = new String [5];
					{
						Sorted_Max_Freq_Entries_Array[0] = availableMaxFreqEntries[4];
						Sorted_Max_Freq_Entries_Array[1] = availableMaxFreqEntries[3];
						Sorted_Max_Freq_Entries_Array[2] = availableMaxFreqEntries[2];
						Sorted_Max_Freq_Entries_Array[3] = availableMaxFreqEntries[1];
						Sorted_Max_Freq_Entries_Array[4] = availableMaxFreqEntries[0];
					}
				//}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	// Method Used for retreiving data from the AlertDialog
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GET_CODE) {
			if (resultCode == RESULT_CANCELED) {
			} else {
				@SuppressWarnings("unused")
				String resultlog = Integer.toString(resultCode);
				if (data != null) {
					//Log.d(TAG, "RESULT_DATA = " + data.getAction());
					SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putInt("cpu_hide_dialog", Integer.parseInt(data.getAction()));
					editor.commit();
				}
			}
		}
	}

	// Definition of the one requestCode we use for receiving resuls.
	static final private int GET_CODE = 0;

	@Override
	protected void onResume() {
		super.onResume();

		// rootProcess running?
		if (!rootProcess.init()) {
			rootProcess.init();
			return;
		} else {
		}
		if (Sorted_Min_Frequencies_Array == null && Sorted_Min_Frequencies_Array.length < 0) {
			AvailableFreqReader();
			//Log.d(TAG,"Sorted_Min_Frequencies_Array == null ");
			} else {
			if (Sorted_Max_Frequencies_Array == null && Sorted_Max_Frequencies_Array.length < 0) {
				AvailableFreqReader();
			//Log.d(TAG,"Sorted_Min_Frequencies_Array == null ");
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		rootProcess.term(); // Terminates our rootProcess
	}

	@Override
	protected void onStart() {
		super.onStart();

		EasyTracker.getInstance().activityStart(this); // Needs to be last
	}

	@Override
	protected void onStop() {
		super.onStop();

		EasyTracker.getInstance().activityStop(this); // Needs to be last
	}

}

