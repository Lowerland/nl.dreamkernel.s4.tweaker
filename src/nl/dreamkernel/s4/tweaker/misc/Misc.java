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

import com.google.analytics.tracking.android.EasyTracker;

import nl.dreamkernel.s4.tweaker.util.DialogActivity;
import nl.dreamkernel.s4.tweaker.util.FileCheck;
import nl.dreamkernel.s4.tweaker.util.OptionsHider;
import nl.dreamkernel.s4.tweaker.util.RootCheck;
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
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class Misc extends Activity {
	static final String TAG = "S4Tweaker";

	// variables for the Textviews
	public static TextView InternalValue;
	public static TextView ExternalValue;
	public static TextView textuncompatibel;
	public static TextView textuncompatibel2;
	public static TextView textuncompatibel3;
	public static TextView textuncompatibel4;
	public static TextView textuncompatibel5;
	public static TextView textuncompatibel6;

	// variables for touch blocks
	public static View Touch_block_int_scheduler;
	public static View Touch_block_ext_scheduler;

	// Variables for file paths

	public static final SysFs vCheck_internalscheduler = new SysFs(
			"/sys/block/mmcblk0/queue/scheduler");
	public static final SysFs vCheck_externalscheduler = new SysFs(
			"/sys/block/mmcblk1/queue/scheduler");
	public static final SysFs vCheck_vibrator_intensity = new SysFs(
			"/sys/vibrator/pwm_val");
	public static final SysFs vCheck_Usb_Fast_charge = new SysFs(
			"/sys/kernel/fast_charge/force_fast_charge");
	public static final SysFs vCheck_Dyn_File_Sys_Sync = new SysFs(
			"/sys/kernel/dyn_fsync/Dyn_fsync_active");
	public static final SysFs vCheck_Display_Power_Reduce = new SysFs(
			"/sys/devices/platform/mipi_samsung_full_hd.2305/lcd/panel/power_reduce");

	// public static final SysFs vCheck_internalscheduler = new SysFs(
	// "/mnt/sdcard/testfiles/internalscheduler");
	// public static final SysFs vCheck_externalscheduler = new SysFs(
	// "/mnt/sdcard/testfiles/externalscheduler");
	// public static final SysFs vCheck_vibrator_intensity = new SysFs(
	// "/mnt/sdcard/testfiles/pwm_val");
	// public static final SysFs vCheck_Usb_Fast_charge = new
	// SysFs("/mnt/sdcard/testfiles/force_fast_charge");
	// public static final SysFs vCheck_Dyn_File_Sys_Sync = new
	// SysFs("/mnt/sdcard/testfiles/Dyn_fsync_active");
	// public static final SysFs vCheck_Display_Power_Reduce = new SysFs(
	// "/mnt/sdcard/testfiles/power_reduce");

	// variables storing the real file values
	private String file_value_internal;
	private String file_value_external;
	private String file_value_temp;
	private String file_value_temp2;
	private int value_vibrator;
	private int value_vibrator_temp;
	private boolean usb_switch_value;
	private int usb_switch_value_temp;
	private boolean dynamic_file_sys_switch_value;
	private int dynamic_file_sys_switch_value_temp;
	private boolean display_power_reduce_switch_value;
	private int display_power_reduce_switch_value_temp;

	// the seek bar variable
	public static SeekBar seekbar_vibrator;
	// declare text label objects
	public static TextView vibratorProgress;
	// declare Switch objects
	public static Switch usbfastchargeswitch;
	public static Switch dynamicfilesyssyncswitch;
	public static Switch displaypowerreduceswitch;

	// variables to store the shared pref in
	// private int InternalPrefValue;
	// private int ExternalPrefValue;
	public static int misc_hide_dialog;

	// TEMP Int used by dialogs
	private static int dialog_temp_internal;
	private static int dialog_temp_external;

	// Variables for setOnBoot
	public Switch onBootSwitch_MiscTweaks;
	public boolean onBootMiscTweaks_pref;

	public static boolean noRoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.misctweaks);
		setTitle(R.string.menu_misc_tweaks);
		getActionBar().hide();

		noRoot = false;

		// TODO
		RootCheck rootcheck = new RootCheck();
		if (!rootcheck.init()) {
			// Log.d(TAG, "YOU NOOOOB");
			noRoot = true;
			finish();
			return;
		} else {
			// Log.d(TAG, "Root ACCESSS BITCH");
		}

		final SharedPreferences sharedPreferences = getSharedPreferences(
				"MY_SHARED_PREF", 0);

		// Find current value views
		InternalValue = (TextView) findViewById(R.id.InternalValue);
		ExternalValue = (TextView) findViewById(R.id.ExternalValue);
		vibratorProgress = (TextView) findViewById(R.id.value_vibrator_intensity);
		// Find Views
		textuncompatibel = (TextView) findViewById(R.id.internal_scheduler_alert);
		textuncompatibel2 = (TextView) findViewById(R.id.external_scheduler_alert);

		textuncompatibel3 = (TextView) findViewById(R.id.vibrator_intensity_alert);
		textuncompatibel4 = (TextView) findViewById(R.id.usb_fast_charge_alert);
		textuncompatibel5 = (TextView) findViewById(R.id.dyn_file_sys_sync_alert);
		textuncompatibel6 = (TextView) findViewById(R.id.display_power_reduce_alert);

		// get the seek bar
		seekbar_vibrator = (SeekBar) findViewById(R.id.sb_vibrator_intensity);

		// Find boot switch
		onBootSwitch_MiscTweaks = (Switch) findViewById(R.id.onBootSwitch_MiscTweaks);

		usbfastchargeswitch = (Switch) findViewById(R.id.usb_fast_charge_switch);

		dynamicfilesyssyncswitch = (Switch) findViewById(R.id.dyn_file_sys_sync_switch);

		displaypowerreduceswitch = (Switch) findViewById(R.id.display_power_reduce_switch);

		// Find Thouch Blocks so we can could disable them
		Touch_block_int_scheduler = (View) findViewById(R.id.internaltouchblock);
		Touch_block_ext_scheduler = (View) findViewById(R.id.externaltouchblock);

		// get the Shared Prefs
		// InternalPrefValue = sharedPreferences.getInt("InternalPref", 0);
		// ExternalPrefValue = sharedPreferences.getInt("ExternalPref", 0);
		// get onBoot Pref
		onBootMiscTweaks_pref = sharedPreferences.getBoolean(
				"onBootMiscTweaks_pref", false);

		// Set on boot switch
		onBootSwitch_MiscTweaks.setChecked(onBootMiscTweaks_pref);

		// read the files value
		ValueReader();

		// Setup the Switch Listeners

		onBootSwitch_MiscTweaks
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// The toggle is enabled
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putBoolean("onBootMiscTweaks_pref", true);
							editor.commit();
							Log.d(TAG, "onBoot Enabled for MiscTweaks");
						} else {
							// The toggle is disabled
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putBoolean("onBootMiscTweaks_pref", false);
							editor.commit();
							Log.d(TAG, "onBoot Disabled for MiscTweaks");
						}
					}
				});

		usbfastchargeswitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// The toggle is enabled
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putInt("usb_fast_charge_pref", 1);
							editor.commit();
							fast_charge_on();
							Log.d(TAG, "USB FAST CHARGE Enabled");
						} else {
							// The toggle is disabled
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putInt("usb_fast_charge_pref", 0);
							editor.commit();
							fast_charge_off();
							Log.d(TAG, "USB FAST CHARGE Disabled");
						}
					}
				});

		dynamicfilesyssyncswitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// The toggle is enabled
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putInt("dyn_file_sys_sync_pref", 1);
							editor.commit();
							DYN_FILE_SYS_SYNC_on();
							Log.d(TAG, "DYN_FILE_SYS_SYNC Enabled");
						} else {
							// The toggle is disabled
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putInt("dyn_file_sys_sync_pref", 0);
							editor.commit();
							DYN_FILE_SYS_SYNC_off();
							Log.d(TAG, "DYN_FILE_SYS_SYNC Disabled");
						}
					}
				});

		displaypowerreduceswitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// The toggle is enabled
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putInt("display_power_reduce_pref", 1);
							editor.commit();
							DISPLAY_POWER_REDUCE_on();
							Log.d(TAG, "display_power_reduce_pref Enabled");
						} else {
							// The toggle is disabled
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putInt("display_power_reduce_pref", 0);
							editor.commit();
							DISPLAY_POWER_REDUCE_off();
							Log.d(TAG, "display_power_reduce_pref Disabled");
						}
					}
				});

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
						// set the preferences for onBoot Usage
						SharedPreferences sharedPreferences = getSharedPreferences(
								"MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putInt("value_vibrator", value_vibrator);
						editor.commit();

						// Try catch block for if it may go wrong
						try {
							// Log.d(TAG, "vibrator: " + value_vibrator);
							// calls RootProcess
							RootProcess process = new RootProcess();
							if (!process.init()) {
								return;
							}
							process.write("chmod 664 /sys/vibrator/pwm_val\n");
							// Writing the values to the files
							process.write("echo " + value_vibrator
									+ " > /sys/vibrator/pwm_val\n");
							// Log.d(TAG, "echo value_vibrator : "
							// + value_vibrator);
							process.term();
						} catch (Exception e) {
							// Log.e(TAG, "Error crashed " + e);
							// Log.d(TAG, "error " + e);
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
						// Log.d(TAG, "Progressbar: " + siz);
						// Log.d(TAG, "Progressbar: " + value_vibrator);

						// change progress text label with current seekbar value
						vibratorProgress.setText("" + value_vibrator);
					}
				});

		// Filechecking part
		misc_hide_dialog = sharedPreferences.getInt("misc_hide_dialog", 0);
		// Log.d(TAG, "onCreate misc_hide_dialog = " + misc_hide_dialog);

		// Options Compatible Check
		FileCheck.CheckMiscOptions(Misc.this);
		// Hide Options if it isn't compatible
		OptionsHider.MiscTweaksHider(Misc.this);

		if (FileCheck.incompatible == true) {
			if (misc_hide_dialog == 1) {
				// Log.d(TAG, "hide the dialog");
			} else {
				// Log.d(TAG, "show dialog");
				Intent intent = new Intent(Misc.this, DialogActivity.class);
				startActivityForResult(intent, GET_CODE);
			}
			// Log.d(TAG, "incompatible = " + FileCheck.incompatible);
		} else {
			// Log.d(TAG, "incompatible = " + FileCheck.incompatible);
		}

	}

	void showToast(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void ValueReader() {
		// Read in the Values from files
		// Log.d(TAG, "Read in the Values from files");
		RootProcess rootProcess = new RootProcess();
		// Log.d(TAG, "Misc Tweaks, Root init s");
		rootProcess.init();
		// Log.d(TAG, "Misc Tweaks, Root init e");

		if (vCheck_internalscheduler.exists()) {
			rootProcess.write("chmod 664 /sys/block/mmcblk0/queue/scheduler\n");
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

		if (vCheck_externalscheduler.exists()) {
			rootProcess.write("chmod 664 /sys/block/mmcblk1/queue/scheduler\n");
			file_value_temp2 = vCheck_externalscheduler.read(rootProcess);

			Pattern a = Pattern.compile("\\[((.*?)\\])");
			Matcher b = a.matcher(file_value_temp2);
			if (b.find()) {
				file_value_external = b.toMatchResult().group(0);
				file_value_external = file_value_external.replace("[", "")
						.replace("]", "");
			}
		} else {
			file_value_external = "File Not Found";
		}

		if (vCheck_vibrator_intensity.exists()) {
			rootProcess.write("chmod 664 /sys/vibrator/pwm_val\n");
			try {
				value_vibrator_temp = Integer
						.parseInt(vCheck_vibrator_intensity.read(rootProcess));
				value_vibrator = value_vibrator_temp;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		} else {
			value_vibrator = 0;
		}

		if (vCheck_Usb_Fast_charge.exists()) {
			rootProcess
					.write("chmod 664 /sys/kernel/fast_charge/force_fast_charge\n");
			try {
				usb_switch_value_temp = Integer.parseInt(vCheck_Usb_Fast_charge
						.read(rootProcess));
				if (usb_switch_value_temp == 1) {
					usb_switch_value = true;
				}
				if (usb_switch_value_temp == 0) {
					usb_switch_value = false;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			// Log.d(TAG, "Boolean usb_switch_value_temp = "
			// + usb_switch_value_temp);
			// Log.d(TAG, "Boolean usb_switch_value = " + usb_switch_value);
		} else {
			usb_switch_value = false;
		}

		if (vCheck_Dyn_File_Sys_Sync.exists()) {
			rootProcess
					.write("chmod 664 /sys/kernel/dyn_fsync/Dyn_fsync_active\n");
			try {
				dynamic_file_sys_switch_value_temp = Integer
						.parseInt(vCheck_Dyn_File_Sys_Sync.read(rootProcess));
				if (dynamic_file_sys_switch_value_temp == 1) {
					dynamic_file_sys_switch_value = true;
					dynamicfilesyssyncswitch.setChecked(true);
				}
				if (dynamic_file_sys_switch_value_temp == 0) {
					dynamic_file_sys_switch_value = false;
					dynamicfilesyssyncswitch.setChecked(false);
				}
				Log.d(TAG, "Boolean dynamic_file_sys_switch_value_temp = "
						+ dynamic_file_sys_switch_value_temp);
				Log.d(TAG, "Boolean dynamic_file_sys_switch_value = "
						+ dynamic_file_sys_switch_value);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		} else {
			dynamic_file_sys_switch_value = false;
			dynamicfilesyssyncswitch.setChecked(false);
		}

		if (vCheck_Display_Power_Reduce.exists()) {
			rootProcess
					.write("chmod 664 /sys/devices/platform/mipi_samsung_full_hd.2305/lcd/panel/power_reduce\n");
			try {
				display_power_reduce_switch_value_temp = Integer
						.parseInt(vCheck_Display_Power_Reduce.read(rootProcess));
				if (display_power_reduce_switch_value_temp == 1) {
					display_power_reduce_switch_value = true;
				}
				if (display_power_reduce_switch_value_temp == 0) {
					display_power_reduce_switch_value = false;
				}
				Log.d(TAG, "Boolean display_power_reduce_switch_value_temp = "
						+ display_power_reduce_switch_value_temp);
				Log.d(TAG, "Boolean display_power_reduce_switch_value = "
						+ display_power_reduce_switch_value);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		} else {
			display_power_reduce_switch_value = false;
		}

		rootProcess.term();
		rootProcess = null;

		// Set current value views
		InternalValue.setText("" + file_value_internal);
		ExternalValue.setText("" + file_value_external);
		usbfastchargeswitch.setChecked(usb_switch_value);
		dynamicfilesyssyncswitch.setChecked(dynamic_file_sys_switch_value);
		displaypowerreduceswitch.setChecked(display_power_reduce_switch_value);
	}

	// // Start on boot switch
	// public void onUSBFASTSWITCH(View view) {
	// // set the preferences for onBoot Usage
	// SharedPreferences sharedPreferences = getSharedPreferences(
	// "MY_SHARED_PREF", 0);
	// SharedPreferences.Editor editor = sharedPreferences.edit();
	//
	// boolean on = ((Switch) view).isChecked();
	// if (on) {
	// editor.putInt("usb_fast_charge_pref", 1);
	// // Log.d(TAG, "on USB FAST SWITCH Enabled");
	// fast_charge_on();
	// editor.commit();
	// usbfastchargeswitch.toggle();
	// } else {
	// editor.putInt("usb_fast_charge_pref", 0);
	// // Log.d(TAG, "on USB FAST SWITCH Disabled");
	// fast_charge_off();
	// editor.commit();
	// usbfastchargeswitch.toggle();
	// }
	// }

	public void fast_charge_on() {
		// calls RootProcess
		// Log.d(TAG, "on USB FAST SWITCH Enabled");

		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}
		process.write("chmod 664 /sys/kernel/fast_charge/force_fast_charge\n");
		process.write("echo 1 > /sys/kernel/fast_charge/force_fast_charge\n");
		// process.write("echo 1 > /mnt/sdcard/testfiles/force_fast_charge\n");
		ValueReader();
		process.term();

	}

	public void fast_charge_off() {
		// calls RootProcess
		// Log.d(TAG, "on USB FAST SWITCH Disabled");

		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}
		process.write("chmod 664 /sys/kernel/fast_charge/force_fast_charge\n");
		process.write("echo 0 > /sys/kernel/fast_charge/force_fast_charge\n");
		// process.write("echo 0 > /mnt/sdcard/testfiles/force_fast_charge\n");
		ValueReader();
		process.term();
	}

	// Start on dynamic file system sync switch
	// TODO
	// public void onDYN_FILE_SYS_SYNC_SWITCH(View view) {
	// // set the preferences for onBoot Usage
	// SharedPreferences sharedPreferences = getSharedPreferences(
	// "MY_SHARED_PREF", 0);
	// SharedPreferences.Editor editor = sharedPreferences.edit();
	//
	// boolean on = ((Switch) view).isChecked();
	// if (on) {
	// editor.putInt("dyn_file_sys_sync_pref", 1);
	// Log.d(TAG, "on dyn_file_sys_sync SWITCH Enabled");
	// DYN_FILE_SYS_SYNC_on();
	// editor.commit();
	// dynamicfilesyssyncswitch.toggle();
	// } else {
	// editor.putInt("dyn_file_sys_sync_pref", 0);
	// Log.d(TAG, "on dyn_file_sys_sync SWITCH Disabled");
	// DYN_FILE_SYS_SYNC_off();
	// editor.commit();
	// dynamicfilesyssyncswitch.toggle();
	// }
	// }

	public void DYN_FILE_SYS_SYNC_on() {
		// calls RootProcess
		// TODO
		Log.d(TAG, "on dyn_file_sys_sync SWITCH Enabled");

		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}
		process.write("chmod 664 /sys/kernel/dyn_fsync/Dyn_fsync_active\n");
		process.write("echo 1 > /sys/kernel/dyn_fsync/Dyn_fsync_active\n");
		// process.write("echo 1 > /mnt/sdcard/testfiles/Dyn_fsync_active\n");
		ValueReader();
		process.term();
	}

	public void DYN_FILE_SYS_SYNC_off() {
		// calls RootProcess
		// TODO
		Log.d(TAG, "on dyn_file_sys_sync Disabled");

		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}
		process.write("chmod 664 /sys/kernel/dyn_fsync/Dyn_fsync_active\n");
		process.write("echo 0 > /sys/kernel/dyn_fsync/Dyn_fsync_active\n");
		// process.write("echo 0 > /mnt/sdcard/testfiles/Dyn_fsync_active\n");
		ValueReader();
		process.term();
	}

	// Start onDisplay_Power_Reduce_SWITCH
	// TODO
	// public void onDisplay_Power_Reduce_SWITCH(View view) {
	// // set the preferences for onBoot Usage
	// SharedPreferences sharedPreferences = getSharedPreferences(
	// "MY_SHARED_PREF", 0);
	// SharedPreferences.Editor editor = sharedPreferences.edit();
	//
	// boolean on = ((Switch) view).isChecked();
	// if (on) {
	// editor.putInt("display_power_reduce_pref", 1);
	// Log.d(TAG, "on display_power_reduce_pref SWITCH Enabled");
	// DISPLAY_POWER_REDUCE_on();
	// editor.commit();
	// displaypowerreduceswitch.toggle();
	// } else {
	// editor.putInt("display_power_reduce_pref", 0);
	// Log.d(TAG, "on display_power_reduce_pref SWITCH Disabled");
	// DISPLAY_POWER_REDUCE_off();
	// editor.commit();
	// displaypowerreduceswitch.toggle();
	// }
	//
	// }

	public void DISPLAY_POWER_REDUCE_on() {
		// calls RootProcess
		// TODO
		Log.d(TAG, "on display_power_reduce SWITCH Enabled");

		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}
		process.write("chmod 664 /sys/devices/platform/mipi_samsung_full_hd.2305/lcd/panel/power_reduce\n");
		process.write("echo 1 > /sys/devices/platform/mipi_samsung_full_hd.2305/lcd/panel/power_reduce\n");
		// process.write("echo 1 > /mnt/sdcard/testfiles/power_reduce\n");
		ValueReader();
		process.term();
	}

	public void DISPLAY_POWER_REDUCE_off() {
		// calls RootProcess
		// TODO
		Log.d(TAG, "on display_power_reduce Disabled");

		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}
		process.write("chmod 664 /sys/devices/platform/mipi_samsung_full_hd.2305/lcd/panel/power_reduce\n");
		process.write("echo 0 > /sys/devices/platform/mipi_samsung_full_hd.2305/lcd/panel/power_reduce\n");
		// process.write("echo 0 > /mnt/sdcard/testfiles/power_reduce\n");
		ValueReader();
		process.term();
	}

	public void onINTERNAL(View View) {
		// Log.d(TAG, "Internal value clicked");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(R.array.ioInternal, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						/* User clicked on a radio button do some stuff */
						// Log.d(TAG, "User clicked on radio button "
						// + whichButton);
						dialog_temp_internal = whichButton;

					}
				});
		final AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.text_Internal_Scheduler);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Button OK Clicked
						// Log.d(TAG, "Button ok clicked");
						// Log.d(TAG, "Store Selected = " +
						// dialog_temp_internal);
						SharedPreferences sharedPreferences = getSharedPreferences(
								"MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putInt("InternalPref", dialog_temp_internal);
						editor.commit();

						InternalDialogSaver();
						ValueReader();
					}
				});
		alertDialog.show();
	}

	private void InternalDialogSaver() {
		// Log.d(TAG, "DialogSaver intervalue = " + dialog_temp_internal);
		// calls RootProcess
		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}
		SharedPreferences sharedPreferences = getSharedPreferences(
				"MY_SHARED_PREF", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		process.write("chmod 664 /sys/block/mmcblk0/queue/scheduler\n");
		// Write Values to the filesystem
		switch (dialog_temp_internal) {
		case 0:
			editor.putString("internalscheduler_pref", "noop");
			// Log.d(TAG, "echo'd noop to internalscheduler");
			process.write("echo noop > /sys/block/mmcblk0/queue/scheduler\n");
			// Log.d(TAG, "echo'd noop to internalscheduler");
			break;
		case 1:
			editor.putString("internalscheduler_pref", "deadline");
			process.write("echo deadline > /sys/block/mmcblk0/queue/scheduler\n");
			// Log.d(TAG, "echo'd deadline to internalscheduler");
			break;
		case 2:
			process.write("echo cfq > /sys/block/mmcblk0/queue/scheduler\n");
			editor.putString("internalscheduler_pref", "cfq");
			// Log.d(TAG, "echo'd cfq to internalscheduler");
			break;
		case 3:
			process.write("echo bfq > /sys/block/mmcblk0/queue/scheduler\n");
			editor.putString("internalscheduler_pref", "bfq");

			// Log.d(TAG, "echo'd bfq to internalscheduler");
			break;
		case 4:
			process.write("echo fiops > /sys/block/mmcblk0/queue/scheduler\n");
			editor.putString("internalscheduler_pref", "fiops");

			// Log.d(TAG, "echo'd fiops to internalscheduler");
			break;
		case 5:
			process.write("echo sio > /sys/block/mmcblk0/queue/scheduler\n");
			editor.putString("internalscheduler_pref", "sio");

			// Log.d(TAG, "echo'd sio to internalscheduler");
			break;
		case 6:
			process.write("echo vr > /sys/block/mmcblk0/queue/scheduler\n");
			editor.putString("internalscheduler_pref", "vr");

			// Log.d(TAG, "echo'd vr to internalscheduler");
			break;
		case 7:
			process.write("echo zen > /sys/block/mmcblk0/queue/scheduler\n");
			editor.putString("internalscheduler_pref", "zen");

			// Log.d(TAG, "echo'd zen to internalscheduler");
			break;
		default:
			break;
		}
		editor.commit();
		process.term();
	}

	public void onEXTERNAL(View View) {
		// Log.d(TAG, "External value clicked");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(R.array.ioExternal, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						/* User clicked on a radio button do some stuff */
						// Log.d(TAG, "User clicked on radio button "
						// + whichButton);
						dialog_temp_external = whichButton;

					}
				});
		final AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.text_External_Scheduler);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Button OK Clicked
						// Log.d(TAG, "Button ok clicked");
						// Log.d(TAG, "Store Selected = " +
						// dialog_temp_external);
						SharedPreferences sharedPreferences = getSharedPreferences(
								"MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putInt("ExternalPref", dialog_temp_external);
						editor.commit();

						ExternalDialogSaver();
						ValueReader();
					}
				});

		alertDialog.show();
	}

	private void ExternalDialogSaver() {
		// Log.d(TAG, "DialogSaver extervalue = " + dialog_temp_external);
		// calls RootProcess
		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}
		SharedPreferences sharedPreferences = getSharedPreferences(
				"MY_SHARED_PREF", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		process.write("chmod 664 /sys/block/mmcblk1/queue/scheduler\n");

		// Write Values to the filesystem
		switch (dialog_temp_external) {
		case 0:
			process.write("echo noop > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "noop");

			// Log.d("process", "echo'd noop to externalscheduler");
			break;
		case 1:
			process.write("echo deadline > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "deadline");

			// Log.d("process", "echo'd deadline to externalscheduler");
			break;
		case 2:
			process.write("echo cfq > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "cfq");

			// Log.d("process", "echo'd cfq to externalscheduler");
			break;
		case 3:
			process.write("echo bfq > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "bfq");

			// Log.d("process", "echo'd bfq to externalscheduler");
			break;
		case 4:
			process.write("echo fiops > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "fiops");

			// Log.d("process", "echo'd fiops to externalscheduler");
			break;
		case 5:
			process.write("echo sio > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "sio");

			// Log.d("process", "echo'd sio to externalscheduler");
			break;
		case 6:
			process.write("echo vr > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "vr");

			// Log.d("process", "echo'd vr to externalscheduler");
			break;
		case 7:
			process.write("echo zen > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "zen");

			// Log.d("process", "echo'd zen to externalscheduler");
			break;
		default:
			break;
		}
		editor.commit();
		process.term();
	}

	// on boot switch
	// TODO
	// public void onBootMiscTweaks(View view) {
	// SharedPreferences sharedPreferences = getSharedPreferences(
	// "MY_SHARED_PREF", 0);
	// SharedPreferences.Editor editor = sharedPreferences.edit();
	//
	// boolean on = ((Switch) view).isChecked();
	// if (on) {
	// editor.putBoolean("onBootMiscTweaks_pref", true);
	// editor.commit();
	// // Log.d(TAG, "onBoot Enabled for MiscTweaks");
	// } else {
	// editor.putBoolean("onBootMiscTweaks_pref", false);
	// editor.commit();
	// // Log.d(TAG, "onBoot Disabled for MiscTweaks");
	// }
	// }

	// Method Used for retreiving data from the AlertDialog
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GET_CODE) {
			if (resultCode == RESULT_CANCELED) {
			} else {
				@SuppressWarnings("unused")
				String resultlog = Integer.toString(resultCode);
				if (data != null) {
					// Log.d(TAG, "RESULT_DATA = " + data.getAction());
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

	@Override
	protected void onResume() {
		super.onResume();

	}

}