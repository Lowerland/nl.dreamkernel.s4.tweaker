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

import nl.dreamkernel.s4.tweaker.util.DialogActivity;
import nl.dreamkernel.s4.tweaker.util.FileCheck;
import nl.dreamkernel.s4.tweaker.util.OptionsHider;
import nl.dreamkernel.s4.tweaker.util.SysFs;
import nl.dreamkernel.s4.tweaker.util.RootProcess;
import nl.dreamkernel.s4.tweaker.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
	static final String TAG = "S4Tweaker";

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

	public static final SysFs vCheck_CPU_GOVERNOR = new SysFs(
			"/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
	public static final SysFs vCheck_CPU_CpuMinFREQ = new SysFs(
			"/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
	public static final SysFs vCheck_CPU_CpuMaxFREQ = new SysFs(
			"/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
	/*
	 * public static final SysFs vCheck_CPU_GOVERNOR = new SysFs(
	 * "/mnt/sdcard/testfiles/scaling_governor"); public static final SysFs
	 * vCheck_CPU_CpuMinFREQ = new SysFs(
	 * "/mnt/sdcard/testfiles/scaling_min_freq"); public static final SysFs
	 * vCheck_CPU_CpuMaxFREQ = new SysFs(
	 * "/mnt/sdcard/testfiles/scaling_max_freq");
	 */
	// variables storing the real file values
	private String file_CPU_GOVERNOR;
	private String file_CPU_GOVERNOR_temp;
	private int file_CPU_MinFREQ;
	private int file_CPU_MinFREQ_temp;
	private int file_CPU_MaxFREQ;
	private int file_CPU_MaxFREQ_temp;

	// variables to store the shared pref in
	private int CpuGovernorPrefValue;
	private int CpuMinFREQPrefValue;
	private int CpuMaxFREQPrefValue;
	public static int cpu_hide_dialog;

	// variables for the spinners
	// private static Spinner sCPUspinner;
	// private static Spinner sCPUminFREQspinner;
	// private static Spinner sCPUmaxFREQspinner;

	// TEMP Int used by dialogs
	private static int dialog_temp_cpu_gov;
	private static int dialog_temp_min_scheduler;
	private static int dialog_temp_max_scheduler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cputweaks);
		setTitle(R.string.menu_cpu_tweaks);
		getActionBar().hide();

		final SharedPreferences sharedPreferences = getSharedPreferences(
				"MY_SHARED_PREF", 0);

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
		CpuGovernorPrefValue = sharedPreferences.getInt("CpuGovernorPref", 0);

		CpuMinFREQPrefValue = sharedPreferences.getInt("CpuMinFREQPref", 0);
		CpuMaxFREQPrefValue = sharedPreferences.getInt("CpuMaxFREQPref", 0);

		// read the files value
		ValueReader();

		// Dropdown menu for I/O Scheduler Internal
		/*
		 * sCPUspinner = (Spinner) findViewById(R.id.cpuspinner);
		 * 
		 * ArrayAdapter<CharSequence> internaladapter = ArrayAdapter
		 * .createFromResource(this, R.array.CPUgovernorArray,
		 * android.R.layout.simple_spinner_item); internaladapter
		 * .setDropDownViewResource
		 * (android.R.layout.simple_spinner_dropdown_item);
		 * sCPUspinner.setAdapter(internaladapter); // set the option based on
		 * the sharedprefs sCPUspinner.setSelection(CpuGovernorPrefValue, true);
		 * sCPUspinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		 * public void onItemSelected(AdapterView<?> parent, View view, int
		 * position, long id) { SharedPreferences.Editor editor =
		 * sharedPreferences.edit(); editor.putInt("CpuGovernorPref", position);
		 * editor.commit(); Log.d(TAG, "CpuGovernorPref: position=" + position +
		 * " id=" + id);
		 * 
		 * // Try catch block for if it may go wrong try { // calls RootProcess
		 * RootProcess process = new RootProcess(); if (!process.init()) {
		 * return; } // Writing the selected value to file switch (position) {
		 * case 0: process.write(
		 * "echo msm-dcvs > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd msm-dcvs to  CPU Governor"); break; case 1:
		 * process.write(
		 * "echo intellidemand > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd intellidemand to  CPU Governor"); break; case
		 * 2: process.write(
		 * "echo interactive > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd interactive to  CPU Governor"); break; case 3:
		 * process.write(
		 * "echo conservative > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd conservative to  CPU Governor"); break; case 4:
		 * process.write(
		 * "echo ondemand > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd ondemand to  CPU Governor"); break; case 5:
		 * process.write(
		 * "echo wheatley > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd wheatley to  CPU Governor"); break; case 6:
		 * process.write(
		 * "echo smartmax > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd smartmax to  CPU Governor"); break; case 7:
		 * process.write(
		 * "echo userspace > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd userspace to  CPU Governor"); break; case 8:
		 * process.write(
		 * "echo powersave > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd powersave to  CPU Governor"); break; case 9:
		 * process.write(
		 * "echo performance > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd performance to  CPU Governor"); break; case 10:
		 * process.write(
		 * "echo adaptive > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd adaptive to  CPU Governor"); break; case 11:
		 * process.write(
		 * "echo asswax > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd asswax to  CPU Governor"); break; case 12:
		 * process.write(
		 * "echo badass > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd badass to  CPU Governor"); break; case 13:
		 * process.write(
		 * "echo dancedance > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd dancedance to  CPU Governor"); break; case 14:
		 * process.write(
		 * "echo smartassH3 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n"
		 * ); Log.d(TAG, "echo'd smartassH3 to  CPU Governor"); break; default:
		 * break; }
		 * 
		 * process.term(); ValueReader(); } catch (Exception e) { Log.e(TAG,
		 * "Error crashed " + e); Log.d(TAG, "Error " + e); } }
		 * 
		 * public void onNothingSelected(AdapterView<?> parent) { Log.d(TAG,
		 * "cpuprefadapter: Nothing selected"); } });
		 */

		// Dropdown menu for scaling_min_freq
		/*
		 * sCPUminFREQspinner = (Spinner) findViewById(R.id.cpuminfreqspinner);
		 * 
		 * ArrayAdapter<CharSequence> cpuminfreqadapter = ArrayAdapter
		 * .createFromResource(this, R.array.CPUminfreqArray,
		 * android.R.layout.simple_spinner_item); cpuminfreqadapter
		 * .setDropDownViewResource
		 * (android.R.layout.simple_spinner_dropdown_item);
		 * sCPUminFREQspinner.setAdapter(cpuminfreqadapter); // set the option
		 * based on the sharedprefs
		 * sCPUminFREQspinner.setSelection(CpuMinFREQPrefValue, true);
		 * sCPUminFREQspinner .setOnItemSelectedListener(new
		 * OnItemSelectedListener() { public void onItemSelected(AdapterView<?>
		 * parent, View view, int position, long id) { SharedPreferences.Editor
		 * editor = sharedPreferences .edit(); editor.putInt("CpuMinFREQPref",
		 * position); editor.commit(); Log.d(TAG, "CpuMinFREQPref: position=" +
		 * position + " id=" + id);
		 * 
		 * // Try catch block for if it may go wrong try { // calls RootProcess
		 * RootProcess process = new RootProcess(); if (!process.init()) {
		 * return; } // Writing the selected value to file switch (position) {
		 * case 0: process.write(
		 * "echo 162000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n"
		 * ); Log.d(TAG, "echo'd 162000 to Cpu Min FREQ"); break; case 1:
		 * process.write(
		 * "echo 216000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n"
		 * ); Log.d(TAG, "echo'd 216000 to Cpu Min FREQ"); break; case 2:
		 * process.write(
		 * "echo 270000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n"
		 * ); Log.d(TAG, "echo'd 270000 to Cpu Min FREQ"); break; case 3:
		 * process.write(
		 * "echo 324000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n"
		 * ); Log.d(TAG, "echo'd 324000 to Cpu Min FREQ"); break; case 4:
		 * process.write(
		 * "echo 378000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n"
		 * ); Log.d(TAG, "echo'd 378000 to Cpu Min FREQ"); break; case 5:
		 * process.write(
		 * "echo 384000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n"
		 * ); Log.d(TAG, "echo'd 384000 to Cpu Min FREQ"); break; case 6:
		 * process.write(
		 * "echo 486000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n"
		 * ); Log.d(TAG, "echo'd 486000 to Cpu Min FREQ"); break; case 7:
		 * process.write(
		 * "echo 594000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n"
		 * ); Log.d(TAG, "echo'd 594000 to Cpu Min FREQ"); break; default:
		 * break; }
		 * 
		 * process.term(); ValueReader(); } catch (Exception e) { Log.e(TAG,
		 * "Error crashed " + e); Log.d(TAG, "Error " + e); } }
		 * 
		 * public void onNothingSelected(AdapterView<?> parent) { Log.d(TAG,
		 * "CpuMinFREQPref: Nothing selected"); } });
		 */

		// Dropdown menu for scaling_max_freq
		/*
		 * sCPUmaxFREQspinner = (Spinner) findViewById(R.id.cpumaxfreqspinner);
		 * 
		 * ArrayAdapter<CharSequence> cpumaxfreqadapter = ArrayAdapter
		 * .createFromResource(this, R.array.CPUmaxfreqArray,
		 * android.R.layout.simple_spinner_item); cpumaxfreqadapter
		 * .setDropDownViewResource
		 * (android.R.layout.simple_spinner_dropdown_item);
		 * sCPUmaxFREQspinner.setAdapter(cpumaxfreqadapter); // set the option
		 * based on the sharedprefs
		 * sCPUmaxFREQspinner.setSelection(CpuMaxFREQPrefValue, true);
		 * sCPUmaxFREQspinner .setOnItemSelectedListener(new
		 * OnItemSelectedListener() { public void onItemSelected(AdapterView<?>
		 * parent, View view, int position, long id) { SharedPreferences.Editor
		 * editor = sharedPreferences .edit(); editor.putInt("CpuMaxFREQPref",
		 * position); editor.commit(); Log.d(TAG, "CpuMaxFREQPref: position=" +
		 * position + " id=" + id);
		 * 
		 * // Try catch block for if it may go wrong try { // calls RootProcess
		 * RootProcess process = new RootProcess(); if (!process.init()) {
		 * return; } // Writing the selected value to file switch (position) {
		 * case 0: process.write(
		 * "echo 1566000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n"
		 * ); Log.d(TAG, "echo'd 1566000 to Cpu Max FREQ"); break; case 1:
		 * process.write(
		 * "echo 1674000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n"
		 * ); Log.d(TAG, "echo'd 1674000 to Cpu Max FREQ"); break; case 2:
		 * process.write(
		 * "echo 1782000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n"
		 * ); Log.d(TAG, "echo'd 1782000 to Cpu Max FREQ"); break; case 3:
		 * process.write(
		 * "echo 1890000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n"
		 * ); Log.d(TAG, "echo'd 1890000 to Cpu Max FREQ"); break; case 4:
		 * process.write(
		 * "echo 1944000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n"
		 * ); Log.d(TAG, "echo'd 1944000 to Cpu Max FREQ"); break; case 5:
		 * process.write(
		 * "echo 1998000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n"
		 * ); Log.d(TAG, "echo'd 1998000 to Cpu Max FREQ"); break; case 6:
		 * process.write(
		 * "echo 2052000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n"
		 * ); Log.d(TAG, "echo'd 2052000 to Cpu Max FREQ"); break; case 7:
		 * process.write(
		 * "echo 2106000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n"
		 * ); Log.d(TAG, "echo'd 2106000 to Cpu Max FREQ"); break; default:
		 * break; }
		 * 
		 * process.term(); ValueReader(); } catch (Exception e) { Log.e(TAG,
		 * "Error crashed " + e); Log.d(TAG, "error " + e); } }
		 * 
		 * public void onNothingSelected(AdapterView<?> parent) { Log.d(TAG,
		 * "CpuMaxFREQPref: Nothing selected"); } });
		 */

		// Filechecking part
		cpu_hide_dialog = sharedPreferences.getInt("cpu_hide_dialog", 0);
		Log.d(TAG, "onCreate cpu_hide_dialog = " + cpu_hide_dialog);

		// Options Compatible Check
		FileCheck.CheckCPUOptions(CpuTweaks.this);
		// Hide Options if it isn't compatible
		OptionsHider.CpuTweaksHider(CpuTweaks.this);

		if (FileCheck.incompatible == true) {
			if (cpu_hide_dialog == 1) {
				Log.d(TAG, "hide the dialog");
			} else {
				Log.d(TAG, "show dialog");
				Intent intent = new Intent(CpuTweaks.this, DialogActivity.class);
				startActivityForResult(intent, GET_CODE);
			}
			Log.d(TAG, "incompatible = " + FileCheck.incompatible);
		} else {
			Log.d(TAG, "incompatible = " + FileCheck.incompatible);
		}
	}

	public void onCPUGOVERNOR(View View) {
		Log.d(TAG, "Cpu Governor value clicked");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// final FrameLayout frameView = new FrameLayout(this);
		// builder.setView(frameView);
		builder.setSingleChoiceItems(R.array.CPUgovernorArray, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						/* User clicked on a radio button do some stuff */
						Log.d(TAG, "User clicked on radio button "
								+ whichButton);
						dialog_temp_cpu_gov = whichButton;

					}
				});
		final AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.cpu_governor);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Button OK Clicked
						Log.d(TAG, "Button ok clicked");
						Log.d(TAG, "Store Selected = " + dialog_temp_cpu_gov);
						SharedPreferences sharedPreferences = getSharedPreferences(
								"MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putInt("CpuGovernorPref", dialog_temp_cpu_gov);
						editor.commit();

						CPUGovernorDialogSaver();
						ValueReader();
					}
				});

		/*
		 * LayoutInflater inflater = alertDialog.getLayoutInflater();
		 * 
		 * @SuppressWarnings("unused") View dialoglayout =
		 * inflater.inflate(R.layout.dialog_alert, frameView);
		 */
		alertDialog.show();
	}

	private void CPUGovernorDialogSaver() {
		Log.d(TAG, "CPUGovernorDialogSaver value = " + dialog_temp_cpu_gov);
		// calls RootProcess
		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}

		// Write Values to the filesystem
		switch (dialog_temp_cpu_gov) {
		case 0:
			process.write("echo msm-dcvs > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd msm-dcvs to  CPU Governor");
			break;
		case 1:
			process.write("echo intellidemand > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd intellidemand to  CPU Governor");
			break;
		case 2:
			process.write("echo interactive > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd interactive to  CPU Governor");
			break;
		case 3:
			process.write("echo conservative > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd conservative to  CPU Governor");
			break;
		case 4:
			process.write("echo ondemand > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd ondemand to  CPU Governor");
			break;
		case 5:
			process.write("echo wheatley > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd wheatley to  CPU Governor");
			break;
		case 6:
			process.write("echo smartmax > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd smartmax to  CPU Governor");
			break;
		case 7:
			process.write("echo userspace > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd userspace to  CPU Governor");
			break;
		case 8:
			process.write("echo powersave > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd powersave to  CPU Governor");
			break;
		case 9:
			process.write("echo performance > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd performance to  CPU Governor");
			break;
		case 10:
			process.write("echo adaptive > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd adaptive to  CPU Governor");
			break;
		case 11:
			process.write("echo asswax > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd asswax to  CPU Governor");
			break;
		case 12:
			process.write("echo badass > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd badass to  CPU Governor");
			break;
		case 13:
			process.write("echo dancedance > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd dancedance to  CPU Governor");
			break;
		case 14:
			process.write("echo smartassH3 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
			Log.d(TAG, "echo'd smartassH3 to  CPU Governor");
			break;
		default:
			break;
		}
		process.term();
	}

	public void onMINFREQSCALING(View View) {
		Log.d(TAG, "onMINFREQSCALING value clicked");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// final FrameLayout frameView = new FrameLayout(this);
		// builder.setView(frameView);
		builder.setSingleChoiceItems(R.array.CPUminfreqArray, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						/* User clicked on a radio button do some stuff */
						Log.d(TAG, "User clicked on radio button "
								+ whichButton);
						dialog_temp_min_scheduler = whichButton;

					}
				});
		final AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.min_frequency_scaling);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Button OK Clicked
						Log.d(TAG, "Button ok clicked");
						Log.d(TAG, "Store Selected = "
								+ dialog_temp_min_scheduler);
						SharedPreferences sharedPreferences = getSharedPreferences(
								"MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putInt("CpuMinFREQPref",
								dialog_temp_min_scheduler);
						editor.commit();

						MIN_FREQ_DialogSaver();
						ValueReader();
					}
				});

		/*
		 * LayoutInflater inflater = alertDialog.getLayoutInflater();
		 * 
		 * @SuppressWarnings("unused") View dialoglayout =
		 * inflater.inflate(R.layout.dialog_alert, frameView);
		 */
		alertDialog.show();
	}

	private void MIN_FREQ_DialogSaver() {
		Log.d(TAG, "DialogSaver intervalue = " + dialog_temp_min_scheduler);
		// calls RootProcess
		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}

		// Write Values to the filesystem
		switch (dialog_temp_min_scheduler) {
		case 0:
			process.write("echo 162000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
			Log.d(TAG, "echo'd 162000 to Cpu Min FREQ");
			break;
		case 1:
			process.write("echo 216000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
			Log.d(TAG, "echo'd 216000 to Cpu Min FREQ");
			break;
		case 2:
			process.write("echo 270000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
			Log.d(TAG, "echo'd 270000 to Cpu Min FREQ");
			break;
		case 3:
			process.write("echo 324000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
			Log.d(TAG, "echo'd 324000 to Cpu Min FREQ");
			break;
		case 4:
			process.write("echo 378000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
			Log.d(TAG, "echo'd 378000 to Cpu Min FREQ");
			break;
		case 5:
			process.write("echo 384000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
			Log.d(TAG, "echo'd 384000 to Cpu Min FREQ");
			break;
		case 6:
			process.write("echo 486000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
			Log.d(TAG, "echo'd 486000 to Cpu Min FREQ");
			break;
		case 7:
			process.write("echo 594000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
			Log.d(TAG, "echo'd 594000 to Cpu Min FREQ");
			break;
		default:
			break;
		}
		process.term();
	}

	public void onMAXFREQSCALING(View View) {
		Log.d(TAG, "onMAXFREQSCALING value clicked");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// final FrameLayout frameView = new FrameLayout(this);
		// builder.setView(frameView);
		builder.setSingleChoiceItems(R.array.CPUmaxfreqArray, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						/* User clicked on a radio button do some stuff */
						Log.d(TAG, "User clicked on radio button "
								+ whichButton);
						dialog_temp_max_scheduler = whichButton;

					}
				});
		final AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.max_fequency_scaling);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Button OK Clicked
						Log.d(TAG, "Button ok clicked");
						Log.d(TAG, "Store Selected = "
								+ dialog_temp_max_scheduler);
						SharedPreferences sharedPreferences = getSharedPreferences(
								"MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putInt("CpuMaxFREQPref",
								dialog_temp_max_scheduler);
						editor.commit();

						MAX_FREQ_DialogSaver();
						ValueReader();
					}
				});

		/*
		 * LayoutInflater inflater = alertDialog.getLayoutInflater();
		 * 
		 * @SuppressWarnings("unused") View dialoglayout =
		 * inflater.inflate(R.layout.dialog_alert, frameView);
		 */
		alertDialog.show();
	}

	private void MAX_FREQ_DialogSaver() {
		Log.d(TAG, "MAX_FREQ_DialogSaver intervalue = "
				+ dialog_temp_max_scheduler);
		// calls RootProcess
		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}

		// Write Values to the filesystem
		switch (dialog_temp_max_scheduler) {
		case 0:
			process.write("echo 1566000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
			Log.d(TAG, "echo'd 1566000 to Cpu Max FREQ");
			break;
		case 1:
			process.write("echo 1674000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
			Log.d(TAG, "echo'd 1674000 to Cpu Max FREQ");
			break;
		case 2:
			process.write("echo 1782000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
			Log.d(TAG, "echo'd 1782000 to Cpu Max FREQ");
			break;
		case 3:
			process.write("echo 1890000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
			Log.d(TAG, "echo'd 1890000 to Cpu Max FREQ");
			break;
		case 4:
			process.write("echo 1944000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
			Log.d(TAG, "echo'd 1944000 to Cpu Max FREQ");
			break;
		case 5:
			process.write("echo 1998000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
			Log.d(TAG, "echo'd 1998000 to Cpu Max FREQ");
			break;
		case 6:
			process.write("echo 2052000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
			Log.d(TAG, "echo'd 2052000 to Cpu Max FREQ");
			break;
		case 7:
			process.write("echo 2106000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
			Log.d(TAG, "echo'd 2106000 to Cpu Max FREQ");
			break;
		default:
			break;
		}
		process.term();
	}

	void showToast(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void ValueReader() {
		// Read in the Values from files
		RootProcess rootProcess = new RootProcess();
		Log.d(TAG, "CPU Tweaks, Root init s");
		rootProcess.init();
		Log.d(TAG, "CPU Tweaks, Root init e");

		if (vCheck_CPU_GOVERNOR.exists()) {
			file_CPU_GOVERNOR_temp = vCheck_CPU_GOVERNOR.read(rootProcess);
			file_CPU_GOVERNOR = file_CPU_GOVERNOR_temp;
		} else {
			file_CPU_GOVERNOR = "File Not Found";
		}

		if (vCheck_CPU_CpuMinFREQ.exists()) {
			file_CPU_MinFREQ_temp = Integer.parseInt(vCheck_CPU_CpuMinFREQ
					.read(rootProcess));
			file_CPU_MinFREQ = file_CPU_MinFREQ_temp;
		} else {
		}

		if (vCheck_CPU_CpuMaxFREQ.exists()) {
			file_CPU_MaxFREQ_temp = Integer.parseInt(vCheck_CPU_CpuMaxFREQ
					.read(rootProcess));
			file_CPU_MaxFREQ = file_CPU_MaxFREQ_temp;
		} else {
		}

		rootProcess.term();
		rootProcess = null;
		// //

		// Set current value views
		CpuCurrentValue.setText("" + file_CPU_GOVERNOR);
		CpuMinFREQValue.setText("" + file_CPU_MinFREQ);
		CpuMaxFREQValue.setText("" + file_CPU_MaxFREQ);

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
					Log.d(TAG, "RESULT_DATA = " + data.getAction());
					SharedPreferences sharedPreferences = getSharedPreferences(
							"MY_SHARED_PREF", 0);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putInt("cpu_hide_dialog",
							Integer.parseInt(data.getAction()));
					editor.commit();
				}
			}
		}
	}

	// Definition of the one requestCode we use for receiving resuls.
	static final private int GET_CODE = 0;
}
