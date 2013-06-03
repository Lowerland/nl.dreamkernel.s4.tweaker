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

import nl.dreamkernel.s4.tweaker.util.DialogActivity;
import nl.dreamkernel.s4.tweaker.util.FileCheck;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class Misc extends Activity {
	static final String TAG = "S4Tweaker";

	// variables for the Textviews
	private static TextView InternalValue;
	// private static TextView ExternalValue;
	private static TextView textuncompatibel;
	// private static TextView textuncompatibel2;// <--- TEMP DISABLED
	private static TextView textuncompatibel3;
	private static TextView textuncompatibel4;

	// Variables for file paths
	public static final SysFs vCheck_internalscheduler = new SysFs(
			"/sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler");
	// public static final SysFs vCheck_externalscheduler = new
	// SysFs("/sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler");//
	// <--- TEMP DISABLED
	public static final SysFs vCheck_vibrator_intensity = new SysFs(
			"/sys/vibrator/pwm_val");
	public static final SysFs vCheck_Usb_Fast_charge = new SysFs(
			"/sys/kernel/fast_charge/force_fast_charge");
	/*
	 * public static final SysFs vCheck_internalscheduler = new
	 * SysFs("/mnt/sdcard/testfiles/internalscheduler"); //public static final
	 * SysFs vCheck_externalscheduler = new
	 * SysFs("/mnt/sdcard/testfiles/externalscheduler");// <--- TEMP DISABLED
	 * public static final SysFs vCheck_vibrator_intensity = new
	 * SysFs("/mnt/sdcard/testfiles/pwm_val"); public static final SysFs
	 * vCheck_Usb_Fast_charge = new
	 * SysFs("/mnt/sdcard/testfiles/force_fast_charge");
	 */
	// variables storing the real file values
	private String file_value_internal;
	// private String file_value_external; // <--- TEMP DISABLED
	private String file_value_temp;
	// private String file_value_temp2;// <--- TEMP DISABLED
	private int value_vibrator;
	private int value_vibrator_temp;
	private boolean usb_switch_value;
	private int usb_switch_value_temp;

	// the seek bar variable
	private static SeekBar seekbar_vibrator;
	// declare text label objects
	private static TextView vibratorProgress;
	// declare Switch objects
	private static Switch usbfastchargeswitch;

	// declare the spinners
	// private static Spinner sInternal;
	// private static Spinner sExternal;

	// variables to store the shared pref in
	private int InternalPrefValue;
	// private int ExternalPrefValue;// <--- TEMP DISABLED
	public static int misc_hide_dialog;

	// TEMP Int used by dialogs
	private static int dialog_temp_internal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.misctweaks);
		setTitle(R.string.menu_misc_tweaks);
		getActionBar().hide();

		final SharedPreferences sharedPreferences = getSharedPreferences(
				"MY_SHARED_PREF", 0);

		// Find current value views
		InternalValue = (TextView) findViewById(R.id.InternalValue);
		// ExternalValue = (TextView)findViewById(R.id.ExternalValue);
		vibratorProgress = (TextView) findViewById(R.id.value_vibrator_intensity);
		// Find Views
		textuncompatibel = (TextView) findViewById(R.id.internal_scheduler_alert);
		// textuncompatibel2 =
		// (TextView)findViewById(R.id.external_scheduler_alert);// <--- TEMP
		// DISABLED
		textuncompatibel3 = (TextView) findViewById(R.id.vibrator_intensity_alert);
		textuncompatibel4 = (TextView) findViewById(R.id.usb_fast_charge_alert);

		// get the seek bar
		seekbar_vibrator = (SeekBar) findViewById(R.id.sb_vibrator_intensity);

		// Start on boot switch
		usbfastchargeswitch = (Switch) findViewById(R.id.usb_fast_charge_switch);

		// get the Shared Prefs

		InternalPrefValue = sharedPreferences.getInt("InternalPref", 0);
		// ExternalPrefValue = sharedPreferences.getInt("ExternalPref", 0); //
		// <--- TEMP DISABLED

		// read the files value
		ValueReader();

		// set progress text
		vibratorProgress.setText("" + value_vibrator);

		// set the seek bar progress
		seekbar_vibrator.setKeyProgressIncrement(value_vibrator);

		// sets the progress of the seek bar
		seekbar_vibrator.setProgress(value_vibrator);

		// register OnSeekBarChangeListener, so it can actually change values
		seekbar_vibrator
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// set the preferences using the seekbar_gpl variable
						// value
						/*
						 * SharedPreferences sharedPreferences =
						 * getSharedPreferences("MY_SHARED_PREF", 0);
						 * SharedPreferences.Editor editor =
						 * sharedPreferences.edit();
						 * editor.putInt("gpl_speaker_gain", gpl_speaker_gain);
						 * editor.commit();
						 */

						// Try catch block for if it may go wrong
						try {
							Log.d(TAG, "vibrator: " + value_vibrator);
							// calls RootProcess
							RootProcess process = new RootProcess();
							if (!process.init()) {
								return;
							}
							// Writing the values to the files
							process.write("echo " + value_vibrator
									+ " > /sys/vibrator/pwm_val\n");
							Log.d(TAG, "echo value_vibrator : "
									+ value_vibrator);
							process.term();
						} catch (Exception e) {
							Log.e(TAG, "Error crashed " + e);
							Log.d(TAG, "error " + e);
						}

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						// sets the minimal level
						int siz = progress;
						value_vibrator = siz;
						Log.d(TAG, "Progressbar: " + siz);
						Log.d(TAG, "Progressbar: " + value_vibrator);

						// change progress text label with current seekbar value
						vibratorProgress.setText("" + value_vibrator);
					}
				});

		// Dropdown menu for I/O Scheduler External
		// sExternal = (Spinner) findViewById(R.id.externalspinner); // Original
		// line
		// ////////////////////////////////////////////////////////////////////////////////////////////////////////
		// TextView tempexternalhidetext = (TextView)
		// findViewById(R.id.current_ext); // DELETE ME TO ENABLE EXT SCHEDULER
		// TextView tempexternalhidetext2 = (TextView)
		// findViewById(R.id.textIOschedulerexternal); // DELETE ME TO ENABLE
		// EXT SCHEDULER
		// tempexternalhidetext.setVisibility(View.GONE); // DELETE ME TO ENABLE
		// EXT SCHEDULER
		// tempexternalhidetext2.setVisibility(View.GONE); // DELETE ME TO
		// ENABLE EXT SCHEDULER
		// sExternal.setVisibility(View.GONE); // DELETE ME TO ENABLE EXT
		// SCHEDULER
		// ExternalValue.setVisibility(View.GONE); // DELETE ME TO ENABLE EXT
		// SCHEDULER
		// ////////////////////////////////////////////////////////////////////////////////////////////////////////

		/*
		 * ArrayAdapter<CharSequence> externaladapter = ArrayAdapter
		 * .createFromResource(this, R.array.ioExternal,
		 * android.R.layout.simple_spinner_item); externaladapter
		 * .setDropDownViewResource
		 * (android.R.layout.simple_spinner_dropdown_item);
		 * sExternal.setAdapter(externaladapter); //set the option based on the
		 * sharedprefs sExternal.setSelection(ExternalPrefValue, true);
		 * sExternal.setOnItemSelectedListener(new OnItemSelectedListener() {
		 * public void onItemSelected(AdapterView<?> parent, View view, int
		 * position, long id) {
		 * 
		 * SharedPreferences.Editor editor = sharedPreferences.edit();
		 * editor.putInt("ExternalPref", position); editor.commit();
		 * Log.d("externaladapter","externaladapter: position=" + position +
		 * " id=" + id);
		 * 
		 * //calls RootProcess RootProcess process = new RootProcess(); if
		 * (!process.init()) { return; } // Writing the selected value to file
		 * switch (position){ case 0: process.write(
		 * "echo noop > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n"
		 * ); Log.d("process","echo'd noop to externalscheduler"); break; case
		 * 1: process.write(
		 * "echo deadline > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n"
		 * ); Log.d("process","echo'd deadline to externalscheduler"); break;
		 * case 2: process.write(
		 * "echo cfq > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n"
		 * ); Log.d("process","echo'd cfq to externalscheduler"); break; case 3:
		 * process.write(
		 * "echo bfq > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n"
		 * ); Log.d("process","echo'd bfq to externalscheduler"); break; case 4:
		 * process.write(
		 * "echo fiops > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n"
		 * ); Log.d("process","echo'd fiops to externalscheduler"); break; case
		 * 5: process.write(
		 * "echo sio > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n"
		 * ); Log.d("process","echo'd sio to externalscheduler"); break; case 6:
		 * process.write(
		 * "echo vr > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n"
		 * ); Log.d("process","echo'd vr to externalscheduler"); break; case 7:
		 * process.write(
		 * "echo zen > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler\n"
		 * ); Log.d("process","echo'd zen to externalscheduler"); break;
		 * default: break; }
		 * 
		 * process.term(); ValueReader();
		 * 
		 * }
		 * 
		 * public void onNothingSelected(AdapterView<?> parent) {
		 * Log.d("externaladapter","externaladapter: Nothing selected"); } });
		 */

		// Filechecking part
		misc_hide_dialog = sharedPreferences.getInt("misc_hide_dialog", 0);
		Log.d(TAG, "onCreate misc_hide_dialog = " + misc_hide_dialog);

		FileCheck.CheckMiscOptions(Misc.this);
		OptionsHider();

		if (FileCheck.incompatible == true) {
			if (misc_hide_dialog == 1) {
				Log.d(TAG, "hide the dialog");
			} else {
				Log.d(TAG, "show dialog");
				Intent intent = new Intent(Misc.this, DialogActivity.class);
				startActivityForResult(intent, GET_CODE);
			}
			Log.d(TAG, "incompatible = " + FileCheck.incompatible);
		} else {
			Log.d(TAG, "incompatible = " + FileCheck.incompatible);
		}

	}

	void showToast(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void ValueReader() {
		// Read in the Values from files
		RootProcess rootProcess = new RootProcess();
		Log.d(TAG, "Misc Tweaks, Root init s");
		rootProcess.init();
		Log.d(TAG, "Misc Tweaks, Root init e");

		if (vCheck_internalscheduler.exists()) {
			file_value_temp = vCheck_internalscheduler.read(rootProcess);

			Pattern p = Pattern.compile("\\[((.*?)\\])");
			Matcher m = p.matcher(file_value_temp);
			if (m.find()) {
				file_value_internal = m.toMatchResult().group(0);
				file_value_internal = file_value_internal.replace("[", "")
						.replace("]", "");
			}
		} else {
			file_value_internal = "File Not Found";
		}

		/*
		 * if (vCheck_externalscheduler.exists()) { file_value_temp2 =
		 * vCheck_externalscheduler.read(rootProcess);
		 * 
		 * Pattern a = Pattern.compile("\\[((.*?)\\])"); Matcher b =
		 * a.matcher(file_value_temp2); if (b.find()){ file_value_external =
		 * b.toMatchResult().group(0); file_value_external =
		 * file_value_external.replace("[", "").replace("]", ""); } } else {
		 * file_value_external = "File Not Found"; }
		 */

		if (vCheck_vibrator_intensity.exists()) {
			value_vibrator_temp = Integer.parseInt(vCheck_vibrator_intensity
					.read(rootProcess));
			value_vibrator = value_vibrator_temp;
		} else {
			value_vibrator = 0;
		}

		if (vCheck_Usb_Fast_charge.exists()) {
			usb_switch_value_temp = Integer.parseInt(vCheck_Usb_Fast_charge
					.read(rootProcess));
			if (usb_switch_value_temp == 1) {
				usb_switch_value = true;
			}
			if (usb_switch_value_temp == 0) {
				usb_switch_value = false;
			}
			// usb_switch_value_temp =
			// Boolean.parseBoolean(vCheck_Usb_Fast_charge.read(rootProcess));
			// usb_switch_value = usb_switch_value_temp;
			Log.d(TAG, "Boolean usb_switch_value_temp = "
					+ usb_switch_value_temp);
			Log.d(TAG, "Boolean usb_switch_value = " + usb_switch_value);
		} else {
			usb_switch_value = false;
		}

		rootProcess.term();
		rootProcess = null;
		// //

		// Set current value views
		InternalValue.setText("" + file_value_internal);
		// ExternalValue.setText(""+file_value_external);// <--- TEMP DISABLED
		usbfastchargeswitch.setChecked(usb_switch_value);
	}

	// Start on boot switch
	public void onUSBFASTSWITCH(View view) {
		// Is the toggle on?
		boolean on = ((Switch) view).isChecked();
		if (on) {
			// calls RootProcess
			RootProcess process = new RootProcess();
			if (!process.init()) {
				return;
			}
			process.write("echo 1 > /sys/kernel/fast_charge/force_fast_charge\n");
			// process.write("echo 1 > /data/data/nl.dreamkernel.s4.tweaker/files/force_fast_charge\n");
			Log.d(TAG, "on USB FAST SWITCH Enabled");
			process.term();
			ValueReader();

		} else {
			// calls RootProcess
			RootProcess process = new RootProcess();
			if (!process.init()) {
				return;
			}
			process.write("echo 0 > /sys/kernel/fast_charge/force_fast_charge\n");
			// process.write("echo 0 > /data/data/nl.dreamkernel.s4.tweaker/files/force_fast_charge\n");
			Log.d(TAG, "on USB FAST SWITCH Disabled");
			process.term();
			ValueReader();

		}
	}

	public void onINTERNAL(View View) {
		Log.d(TAG, "Internal value clicked");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// final FrameLayout frameView = new FrameLayout(this);
		// builder.setView(frameView);
		builder.setSingleChoiceItems(R.array.ioInternal, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						/* User clicked on a radio button do some stuff */
						Log.d(TAG, "User clicked on radio button "
								+ whichButton);
						dialog_temp_internal = whichButton;

					}
				});
		final AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.text_Internal_Scheduler);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Button OK Clicked
						Log.d(TAG, "Button ok clicked");
						Log.d(TAG, "Store Selected = " + dialog_temp_internal);
						SharedPreferences sharedPreferences = getSharedPreferences(
								"MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putInt("InternalPref", dialog_temp_internal);
						editor.commit();

						DialogSaver();
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

	private void DialogSaver() {
		Log.d(TAG, "DialogSaver intervalue = " + dialog_temp_internal);
		// calls RootProcess
		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}

		// Write Values to the filesystem
		switch (dialog_temp_internal) {
		case 0:
			Log.d(TAG, "echo'd noop to internalscheduler");
			process.write("echo noop > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
			Log.d(TAG, "echo'd noop to internalscheduler");
			break;
		case 1:
			process.write("echo deadline > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
			Log.d(TAG, "echo'd deadline to internalscheduler");
			break;
		case 2:
			process.write("echo cfq > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
			Log.d(TAG, "echo'd cfq to internalscheduler");
			break;
		case 3:
			process.write("echo bfq > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
			Log.d(TAG, "echo'd bfq to internalscheduler");
			break;
		case 4:
			process.write("echo fiops > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
			Log.d(TAG, "echo'd fiops to internalscheduler");
			break;
		case 5:
			process.write("echo sio > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
			Log.d(TAG, "echo'd sio to internalscheduler");
			break;
		case 6:
			process.write("echo vr > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
			Log.d(TAG, "echo'd vr to internalscheduler");
			break;
		case 7:
			process.write("echo zen > /sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler\n");
			Log.d(TAG, "echo'd zen to internalscheduler");
			break;
		default:
			break;
		}
		process.term();
		Log.d(TAG, "DialogSaver intervalue end= " + dialog_temp_internal);
	}

	static void OptionsHider() {

		Log.d(TAG, "OptionsHider() internal_scheduler_hide = "
				+ FileCheck.internal_scheduler_hide);
		if (FileCheck.internal_scheduler_hide == 1) {
			// sInternal.setVisibility(View.GONE);
			InternalValue.setVisibility(View.GONE);
			textuncompatibel.setText(R.string.disabled_option_text);
		}
		// if(FileCheck.external_scheduler_hide == 1) { // <--- TEMP DISABLED
		// Log.d("external_scheduler_hide","OptionsHider() external_scheduler_hide = "+FileCheck.external_scheduler_hide);//
		// <--- TEMP DISABLED
		// sExternal.setVisibility(View.GONE);// <--- TEMP DISABLED
		// ExternalValue.setVisibility(View.GONE);// <--- TEMP DISABLED
		// textuncompatibel2.setText(R.string.disabled_option_text);// <--- TEMP
		// DISABLED
		// }
		if (FileCheck.vibrator_intensity_hide == 1) {
			Log.d(TAG, "OptionsHider() vibrator_intensity_hide = "
					+ FileCheck.vibrator_intensity_hide);
			seekbar_vibrator.setVisibility(View.GONE);
			vibratorProgress.setVisibility(View.GONE);
			textuncompatibel3.setText(R.string.disabled_option_text);
		}
		if (FileCheck.Usb_Fast_charge_hide == 1) {
			Log.d(TAG, "OptionsHider() Usb_Fast_charge_hide = "
					+ FileCheck.Usb_Fast_charge_hide);
			usbfastchargeswitch.setVisibility(View.GONE);
			textuncompatibel4.setText(R.string.disabled_option_text);
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
					Log.d(TAG, "RESULT_DATA = " + data.getAction());
					SharedPreferences sharedPreferences = getSharedPreferences(
							"MY_SHARED_PREF", 0);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putInt("misc_hide_dialog",
							Integer.parseInt(data.getAction()));
					editor.commit();
				}
			}
		}
	}

	// Definition of the one requestCode we use for receiving resuls.
	static final private int GET_CODE = 0;
}