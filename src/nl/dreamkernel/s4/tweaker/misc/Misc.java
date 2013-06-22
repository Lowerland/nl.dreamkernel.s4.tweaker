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

	// public static final SysFs vCheck_internalscheduler = new SysFs(
	// "/mnt/sdcard/testfiles/internalscheduler");
	// public static final SysFs vCheck_externalscheduler = new SysFs(
	// "/mnt/sdcard/testfiles/externalscheduler");
	// public static final SysFs vCheck_vibrator_intensity = new SysFs(
	// "/mnt/sdcard/testfiles/pwm_val");
	// public static final SysFs vCheck_Usb_Fast_charge = new SysFs(
	// "/mnt/sdcard/testfiles/force_fast_charge");

	// variables storing the real file values
	private String file_value_internal;
	private String file_value_external;
	private String file_value_temp;
	private String file_value_temp2;
	private int value_vibrator;
	private int value_vibrator_temp;
	private boolean usb_switch_value;
	private int usb_switch_value_temp;

	// the seek bar variable
	public static SeekBar seekbar_vibrator;
	// declare text label objects
	public static TextView vibratorProgress;
	// declare Switch objects
	public static Switch usbfastchargeswitch;

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
		ExternalValue = (TextView) findViewById(R.id.ExternalValue);
		vibratorProgress = (TextView) findViewById(R.id.value_vibrator_intensity);
		// Find Views
		textuncompatibel = (TextView) findViewById(R.id.internal_scheduler_alert);
		textuncompatibel2 = (TextView) findViewById(R.id.external_scheduler_alert);

		textuncompatibel3 = (TextView) findViewById(R.id.vibrator_intensity_alert);
		textuncompatibel4 = (TextView) findViewById(R.id.usb_fast_charge_alert);

		// get the seek bar
		seekbar_vibrator = (SeekBar) findViewById(R.id.sb_vibrator_intensity);

		// Find boot switch
		onBootSwitch_MiscTweaks = (Switch) findViewById(R.id.onBootSwitch_MiscTweaks);

		usbfastchargeswitch = (Switch) findViewById(R.id.usb_fast_charge_switch);

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

		// Filechecking part
		misc_hide_dialog = sharedPreferences.getInt("misc_hide_dialog", 0);
		Log.d(TAG, "onCreate misc_hide_dialog = " + misc_hide_dialog);

		// Options Compatible Check
		FileCheck.CheckMiscOptions(Misc.this);
		// Hide Options if it isn't compatible
		OptionsHider.MiscTweaksHider(Misc.this);

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
		Log.d(TAG, "Read in the Values from files");
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

		if (vCheck_externalscheduler.exists()) {
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
			Log.d(TAG, "Boolean usb_switch_value_temp = "
					+ usb_switch_value_temp);
			Log.d(TAG, "Boolean usb_switch_value = " + usb_switch_value);
		} else {
			usb_switch_value = false;
		}

		rootProcess.term();
		rootProcess = null;

		// Set current value views
		InternalValue.setText("" + file_value_internal);
		ExternalValue.setText("" + file_value_external);
		usbfastchargeswitch.setChecked(usb_switch_value);
	}

	// Start on boot switch
	public void onUSBFASTSWITCH(View view) {
		// set the preferences for onBoot Usage
		SharedPreferences sharedPreferences = getSharedPreferences(
				"MY_SHARED_PREF", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		boolean on = ((Switch) view).isChecked();
		if (on) {
			editor.putInt("usb_fast_charge_pref", 1);
			Log.d(TAG, "on USB FAST SWITCH Enabled");
			fast_charge_on();
		} else {
			editor.putInt("usb_fast_charge_pref", 0);
			Log.d(TAG, "on USB FAST SWITCH Disabled");
			fast_charge_off();
		}
		editor.commit();
	}

	public void fast_charge_on() {
		// calls RootProcess
		Log.d(TAG, "on USB FAST SWITCH Enabled");
		ValueReader();
		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}
		process.write("echo 1 > /sys/kernel/fast_charge/force_fast_charge\n");
		process.term();
	}

	public void fast_charge_off() {
		// calls RootProcess
		Log.d(TAG, "on USB FAST SWITCH Disabled");
		ValueReader();
		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}
		process.write("echo 0 > /sys/kernel/fast_charge/force_fast_charge\n");
		process.term();
	}

	public void onINTERNAL(View View) {
		Log.d(TAG, "Internal value clicked");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

						InternalDialogSaver();
						ValueReader();
					}
				});
		alertDialog.show();
	}

	private void InternalDialogSaver() {
		Log.d(TAG, "DialogSaver intervalue = " + dialog_temp_internal);
		// calls RootProcess
		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}
		SharedPreferences sharedPreferences = getSharedPreferences(
				"MY_SHARED_PREF", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		// Write Values to the filesystem
		switch (dialog_temp_internal) {
		case 0:
			editor.putString("internalscheduler_pref", "noop");
			Log.d(TAG, "echo'd noop to internalscheduler");
			process.write("echo noop > /sys/block/mmcblk0/queue/scheduler\n");
			Log.d(TAG, "echo'd noop to internalscheduler");
			break;
		case 1:
			editor.putString("internalscheduler_pref", "deadline");
			process.write("echo deadline > /sys/block/mmcblk0/queue/scheduler\n");
			Log.d(TAG, "echo'd deadline to internalscheduler");
			break;
		case 2:
			process.write("echo cfq > /sys/block/mmcblk0/queue/scheduler\n");
			editor.putString("internalscheduler_pref", "cfq");
			Log.d(TAG, "echo'd cfq to internalscheduler");
			break;
		case 3:
			process.write("echo bfq > /sys/block/mmcblk0/queue/scheduler\n");
			editor.putString("internalscheduler_pref", "bfq");

			Log.d(TAG, "echo'd bfq to internalscheduler");
			break;
		case 4:
			process.write("echo fiops > /sys/block/mmcblk0/queue/scheduler\n");
			editor.putString("internalscheduler_pref", "fiops");

			Log.d(TAG, "echo'd fiops to internalscheduler");
			break;
		case 5:
			process.write("echo sio > /sys/block/mmcblk0/queue/scheduler\n");
			editor.putString("internalscheduler_pref", "sio");

			Log.d(TAG, "echo'd sio to internalscheduler");
			break;
		case 6:
			process.write("echo vr > /sys/block/mmcblk0/queue/scheduler\n");
			editor.putString("internalscheduler_pref", "vr");

			Log.d(TAG, "echo'd vr to internalscheduler");
			break;
		case 7:
			process.write("echo zen > /sys/block/mmcblk0/queue/scheduler\n");
			editor.putString("internalscheduler_pref", "zen");

			Log.d(TAG, "echo'd zen to internalscheduler");
			break;
		default:
			break;
		}
		editor.commit();
		process.term();
	}

	public void onEXTERNAL(View View) {
		Log.d(TAG, "External value clicked");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(R.array.ioExternal, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						/* User clicked on a radio button do some stuff */
						Log.d(TAG, "User clicked on radio button "
								+ whichButton);
						dialog_temp_external = whichButton;

					}
				});
		final AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.text_External_Scheduler);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Button OK Clicked
						Log.d(TAG, "Button ok clicked");
						Log.d(TAG, "Store Selected = " + dialog_temp_external);
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
		Log.d(TAG, "DialogSaver extervalue = " + dialog_temp_external);
		// calls RootProcess
		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}
		SharedPreferences sharedPreferences = getSharedPreferences(
				"MY_SHARED_PREF", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		// Write Values to the filesystem
		switch (dialog_temp_external) {
		case 0:
			process.write("echo noop > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "noop");

			Log.d("process", "echo'd noop to externalscheduler");
			break;
		case 1:
			process.write("echo deadline > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "deadline");

			Log.d("process", "echo'd deadline to externalscheduler");
			break;
		case 2:
			process.write("echo cfq > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "cfq");

			Log.d("process", "echo'd cfq to externalscheduler");
			break;
		case 3:
			process.write("echo bfq > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "bfq");

			Log.d("process", "echo'd bfq to externalscheduler");
			break;
		case 4:
			process.write("echo fiops > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "fiops");

			Log.d("process", "echo'd fiops to externalscheduler");
			break;
		case 5:
			process.write("echo sio > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "sio");

			Log.d("process", "echo'd sio to externalscheduler");
			break;
		case 6:
			process.write("echo vr > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "vr");

			Log.d("process", "echo'd vr to externalscheduler");
			break;
		case 7:
			process.write("echo zen > /sys/block/mmcblk1/queue/scheduler\n");
			editor.putString("externalscheduler_pref", "zen");

			Log.d("process", "echo'd zen to externalscheduler");
			break;
		default:
			break;
		}
		editor.commit();
		process.term();
	}

	// on boot switch
	// TODO
	public void onBootMiscTweaks(View view) {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"MY_SHARED_PREF", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		boolean on = ((Switch) view).isChecked();
		if (on) {
			editor.putBoolean("onBootMiscTweaks_pref", true);
			editor.commit();
			Log.d(TAG, "onBoot Enabled for MiscTweaks");
		} else {
			editor.putBoolean("onBootMiscTweaks_pref", false);
			editor.commit();
			Log.d(TAG, "onBoot Disabled for MiscTweaks");
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