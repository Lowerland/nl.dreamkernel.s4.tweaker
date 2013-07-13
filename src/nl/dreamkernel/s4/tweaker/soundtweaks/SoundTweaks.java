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
import nl.dreamkernel.s4.tweaker.util.OptionsHider;
import nl.dreamkernel.s4.tweaker.util.RootCheck;
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

	// UI objects
	// the seek bar variable
	public static SeekBar seekbar_gpl;
	public static SeekBar seekbar_mic_gain;
	public static SeekBar seekbar_cam_mic_gain;
	public static SeekBar seekbar_headphone_gain;
	public static SeekBar seekbar_hdmi_spkr_gain;
	public static SeekBar seekbar_headset_mic_gain;

	// declare text label objects
	public static TextView textProgress;
	public static TextView textgplmicProgress;
	public static TextView textgplcammicProgress;
	public static TextView textgplheadphoneProcess;
	public static TextView textgplhdmispkrgainProgress;
	public static TextView textgplheadsetmicgainProgress;
	public static TextView textuncompatibel;
	public static TextView textuncompatibel2;
	public static TextView textuncompatibel3;
	public static TextView textuncompatibel4;
	public static TextView textuncompatibel5;
	public static TextView textuncompatibel6;

	public Switch sound_tweaks_set_on_boot;
	public boolean onBootSoundTweaks_pref;

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

	// vars needed for the + - assigning
	public static String gpl_speaker_temp_sign;
	public static String gpl_mic_temp_sign;
	public static String gpl_cam_mic_temp_sign;
	public static String gpl_headphone_temp_sign;
	public static String gpl_hdmi_spkr_temp_sign;
	public static String gpl_headset_mic_temp_sign;

	// Static Constants for file value checking

	public static final SysFs vCheck_gpl_speaker_gain = new SysFs(
			"/sys/kernel/sound_control/gpl_speaker_gain");
	public static final SysFs vCheck_gpl_mic_gain = new SysFs(
			"/sys/kernel/sound_control/gpl_mic_gain");
	public static final SysFs vCheck_gpl_cam_mic_gain = new SysFs(
			"/sys/kernel/sound_control/gpl_cam_mic_gain");
	public static final SysFs vCheck_gpl_headphone_gain = new SysFs(
			"/sys/kernel/sound_control/gpl_headphone_gain");
	public static final SysFs vCheck_gpl_hdmi_speaker_gain = new SysFs(
			"/sys/kernel/sound_control/gpl_hdmi_spkr_gain");
	public static final SysFs vCheck_gpl_headset_mic_gain = new SysFs(
			"/sys/kernel/sound_control/gpl_headset_mic_gain");

	// public static final SysFs vCheck_gpl_speaker_gain = new
	// SysFs("/mnt/sdcard/testfiles/gpl_speaker_gain");
	// public static final SysFs vCheck_gpl_mic_gain = new
	// SysFs("/mnt/sdcard/testfiles/gpl_mic_gain");
	// public static final SysFs vCheck_gpl_cam_mic_gain = new
	// SysFs("/mnt/sdcard/testfiles/gpl_cam_mic_gain");
	// public static final SysFs vCheck_gpl_headphone_gain = new
	// SysFs("/mnt/sdcard/testfiles/gpl_headphone_gain");
	// public static final SysFs vCheck_gpl_hdmi_speaker_gain = new
	// SysFs("/mnt/sdcard/testfiles/gpl_hdmi_spkr_gain");
	// public static final SysFs vCheck_gpl_headset_mic_gain = new
	// SysFs("/mnt/sdcard/testfiles/gpl_headset_mic_gain");

	private int Value_gpl_speaker_gain;
	private int Value_gpl_mic_gain;
	private int Value_gpl_cam_mic_gain;
	// private int Value_gpl_headphone_gain;
	private int Value_gpl_hdmi_speaker_gain;
	private int Value_gpl_headset_mic_gain;

	public static boolean noRoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.soundtweaks);
		setTitle(R.string.menu_soundtweaks);
		getActionBar().hide();

		noRoot = false;

		RootCheck rootcheck = new RootCheck();
		if (!rootcheck.init()) {
			Log.d(TAG, "YOU NOOOOB");
			noRoot = true;
			finish();
			return;
		} else {
			Log.d(TAG, "Root ACCESSS BITCH");
		}

		final SharedPreferences sharedPreferences = getSharedPreferences(
				"MY_SHARED_PREF", 0);

		// Root init s/e
		RootProcess rootProcess = new RootProcess();
		// Log.d(TAG, "Root init s");
		rootProcess.init();
		// Log.d(TAG, "Root init e");
		// Read in the Values from files
		if (vCheck_gpl_speaker_gain.exists()) {
			Value_gpl_speaker_gain = Integer.parseInt(vCheck_gpl_speaker_gain
					.read(rootProcess));
			gpl_speaker_gain = Value_gpl_speaker_gain;
		} else {
			gpl_speaker_gain = value_default;
		}

		if (vCheck_gpl_mic_gain.exists()) {
			Value_gpl_mic_gain = Integer.parseInt(vCheck_gpl_mic_gain
					.read(rootProcess));
			gpl_mic_gain = Value_gpl_mic_gain;
		} else {
			gpl_mic_gain = value_default;
		}

		if (vCheck_gpl_cam_mic_gain.exists()) {
			Value_gpl_cam_mic_gain = Integer.parseInt(vCheck_gpl_cam_mic_gain
					.read(rootProcess));
			gpl_cam_mic_gain = Value_gpl_cam_mic_gain;
		} else {
			gpl_cam_mic_gain = value_default;
		}

		if (vCheck_gpl_headphone_gain.exists()) {
			// This File hold 2 values example: 20 20
			// so it needs an different aproach

			headphonevalueconvert = vCheck_gpl_headphone_gain.read(rootProcess);
			int nStartIndex = 0;
			int nEndIndex = 2;
			headphonesubstring = headphonevalueconvert.substring(nStartIndex,
					nEndIndex);
			gpl_headphone_gain = Integer.parseInt(headphonesubstring);

		} else {
			gpl_headphone_gain = value_default;
		}

		if (vCheck_gpl_hdmi_speaker_gain.exists()) {
			Value_gpl_hdmi_speaker_gain = Integer
					.parseInt(vCheck_gpl_hdmi_speaker_gain.read(rootProcess));
			gpl_hdmi_spkr_gain = Value_gpl_hdmi_speaker_gain;
		} else {
			gpl_hdmi_spkr_gain = value_default;
		}

		if (vCheck_gpl_headset_mic_gain.exists()) {
			Value_gpl_headset_mic_gain = Integer
					.parseInt(vCheck_gpl_headset_mic_gain.read(rootProcess));
			gpl_headset_mic_gain = Value_gpl_headset_mic_gain;
		} else {
			gpl_headset_mic_gain = value_default;
		}

		rootProcess.term();
		rootProcess = null;

		// Start on boot switch
		sound_tweaks_set_on_boot = (Switch) findViewById(R.id.onBootSwitch_SoundTweaks);

		// make text label for progress value
		textProgress = (TextView) findViewById(R.id.textViewProgress);
		textgplmicProgress = (TextView) findViewById(R.id.gplmicgainprogress);
		textgplcammicProgress = (TextView) findViewById(R.id.gplcammicgainprogress);
		textgplheadphoneProcess = (TextView) findViewById(R.id.gplheadphonegainprogress);
		textgplhdmispkrgainProgress = (TextView) findViewById(R.id.gplhdmispkrgainprogress);
		textgplheadsetmicgainProgress = (TextView) findViewById(R.id.gplheadsetmicgainprogress);

		// get the seek bar from main.xml file
		seekbar_gpl = (SeekBar) findViewById(R.id.sb_gpl_speaker_gain);
		seekbar_mic_gain = (SeekBar) findViewById(R.id.sb_gpl_mic_gain);
		seekbar_cam_mic_gain = (SeekBar) findViewById(R.id.sb_gpl_cam_mic_gain);
		seekbar_headphone_gain = (SeekBar) findViewById(R.id.sb_gpl_headphone_gain);
		seekbar_hdmi_spkr_gain = (SeekBar) findViewById(R.id.sb_gpl_hdmi_spkr_gain);
		seekbar_headset_mic_gain = (SeekBar) findViewById(R.id.sb_gpl_headset_mic_gain);

		// TODO Maybe need this function in the future
		// get the current settings
		onBootSoundTweaks_pref = sharedPreferences.getBoolean(
				"onBootSoundTweaks_pref", false);
		// SharedPreferences sharedPreferences =
		// getSharedPreferences("MY_SHARED_PREF", 0);
		// gpl_speaker_gain = sharedPreferences.getInt("gpl_speaker_gain", 0);
		// gpl_mic_gain = sharedPreferences.getInt("gpl_mic_gain", 0);
		// gpl_cam_mic_gain = sharedPreferences.getInt("gpl_cam_mic_gain", 0);
		// gpl_headphone_gain = sharedPreferences.getInt("gpl_headphone_gain",
		// 0);
		// gpl_hdmi_spkr_gain = sharedPreferences.getInt("gpl_hdmi_spkr_gain",
		// 0);
		// gpl_headset_mic_gain =
		// sharedPreferences.getInt("gpl_headset_mic_gain", 0);

		// set progress text + signs
		int gpl_speaker_gain_sign_temp = gpl_speaker_gain - 40;
		gpl_speaker_temp_sign = "";
		if (gpl_speaker_gain_sign_temp > 0) {
			gpl_speaker_temp_sign = "+";
		}
		String gpl_speaker_gain_sign = gpl_speaker_temp_sign
				+ gpl_speaker_gain_sign_temp;

		int gpl_mic_gain_sign_temp = gpl_mic_gain - 40;
		gpl_mic_temp_sign = "";
		if (gpl_mic_gain_sign_temp > 0) {
			gpl_mic_temp_sign = "+";
		}
		String gpl_mic_gain_sign = gpl_mic_temp_sign + gpl_mic_gain_sign_temp;

		int gpl_cam_mic_gain_sign_temp = gpl_cam_mic_gain - 40;
		gpl_cam_mic_temp_sign = "";
		if (gpl_cam_mic_gain_sign_temp > 0) {
			gpl_cam_mic_temp_sign = "+";
		}
		String gpl_cam_mic_gain_sign = gpl_cam_mic_temp_sign
				+ gpl_cam_mic_gain_sign_temp;

		int gpl_headphone_gain_sign_temp = gpl_headphone_gain - 40;
		gpl_headphone_temp_sign = "";
		if (gpl_headphone_gain_sign_temp > 0) {
			gpl_headphone_temp_sign = "+";
		}
		String gpl_headphone_gain_sign = gpl_headphone_temp_sign
				+ gpl_headphone_gain_sign_temp;

		int gpl_hdmi_spkr_gain_sign_temp = gpl_hdmi_spkr_gain - 40;
		gpl_hdmi_spkr_temp_sign = "";
		if (gpl_hdmi_spkr_gain_sign_temp > 0) {
			gpl_hdmi_spkr_temp_sign = "+";
		}
		String gpl_hdmi_spkr_gain_sign = gpl_hdmi_spkr_temp_sign
				+ gpl_hdmi_spkr_gain_sign_temp;

		int gpl_headset_mic_gain_sign_temp = gpl_headset_mic_gain - 40;
		gpl_headset_mic_temp_sign = "";
		if (gpl_headset_mic_gain_sign_temp > 0) {
			gpl_headset_mic_temp_sign = "+";
		}
		String gpl_headset_mic_gain_sign = gpl_headset_mic_temp_sign
				+ gpl_headset_mic_gain_sign_temp;

		// Set on boot switch
		sound_tweaks_set_on_boot.setChecked(onBootSoundTweaks_pref);

		// set progress text
		textProgress.setText("" + gpl_speaker_gain_sign);
		textgplmicProgress.setText("" + gpl_mic_gain_sign);
		textgplcammicProgress.setText("" + gpl_cam_mic_gain_sign);
		textgplheadphoneProcess.setText("" + gpl_headphone_gain_sign);
		textgplhdmispkrgainProgress.setText("" + gpl_hdmi_spkr_gain_sign);
		textgplheadsetmicgainProgress.setText("" + gpl_headset_mic_gain_sign);

		// Find Views
		textuncompatibel = (TextView) findViewById(R.id.gpl_spkr_alert);
		textuncompatibel2 = (TextView) findViewById(R.id.gpl_spkr_alert2);
		textuncompatibel3 = (TextView) findViewById(R.id.gpl_spkr_alert3);
		textuncompatibel4 = (TextView) findViewById(R.id.gpl_spkr_alert4);
		textuncompatibel5 = (TextView) findViewById(R.id.gpl_spkr_alert5);
		textuncompatibel6 = (TextView) findViewById(R.id.gpl_spkr_alert6);

		// seek bar settings
		// set the seek bar progress based on the sharedprefs
		// needs the -20 at the end
		seekbar_gpl.setKeyProgressIncrement(gpl_speaker_gain - 20);
		seekbar_mic_gain.setKeyProgressIncrement(gpl_mic_gain - 20);
		seekbar_cam_mic_gain.setKeyProgressIncrement(gpl_cam_mic_gain - 20);
		seekbar_headphone_gain.setKeyProgressIncrement(gpl_headphone_gain - 20);
		seekbar_hdmi_spkr_gain.setKeyProgressIncrement(gpl_hdmi_spkr_gain - 20);
		seekbar_headset_mic_gain
				.setKeyProgressIncrement(gpl_headset_mic_gain - 20);

		// sets the progress of the seek bar based on the sharedprefs
		// needs the -20 at the end
		seekbar_gpl.setProgress(gpl_speaker_gain - 20);
		seekbar_mic_gain.setProgress(gpl_mic_gain - 20);
		seekbar_cam_mic_gain.setProgress(gpl_cam_mic_gain - 20);
		seekbar_headphone_gain.setProgress(gpl_headphone_gain - 20);
		seekbar_hdmi_spkr_gain.setProgress(gpl_hdmi_spkr_gain - 20);
		seekbar_headset_mic_gain.setProgress(gpl_headset_mic_gain - 20);

		// Log.d(TAG, "SetProgress gpl_speaker_gain: " + gpl_speaker_gain);
		// Log.d(TAG, "SetProgress gpl_mic_gain: " + gpl_mic_gain);
		// Log.d(TAG, "SetProgress gpl_cam_mic_gain: " + gpl_cam_mic_gain);
		// Log.d(TAG, "SetProgress gpl_headphone_gain: " + gpl_headphone_gain);
		// Log.d(TAG, "SetProgress gpl_hdmi_spkr_gain: " + gpl_hdmi_spkr_gain);
		// Log.d(TAG, "SetProgress gpl_headset_mic_gain: " +
		// gpl_headset_mic_gain);

		// register OnSeekBarChangeListener, so it can actually change values
		seekbar_gpl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// set the preferences using the seekbar_gpl variable value

				SharedPreferences sharedPreferences = getSharedPreferences(
						"MY_SHARED_PREF", 0);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putInt("gpl_speaker_gain_pref", gpl_speaker_gain);
				editor.commit();

				// Try catch block for if it may go wrong
				try {
					// Log.d(TAG, "echo gpl speaker gain: " + gpl_speaker_gain);
					// calls RootProcess
					RootProcess process = new RootProcess();
					if (!process.init()) {
						return;
					}
					// Writing the values to the files
					process.write("echo " + gpl_speaker_gain
							+ " > /sys/kernel/sound_control/gpl_speaker_gain\n");
					// Log.d(TAG, "echo gpl speaker gain: " + gpl_speaker_gain);
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
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// sets the minimal level
				int siz = progress + 20;
				gpl_speaker_gain = siz;
				// Log.d(TAG, "Progressbar: " + siz);
				// Log.d(TAG, "Progressbar: " + gpl_speaker_gain);

				// change progress text label with current seekbar value
				int gpl_speaker_gain_sign_temp = gpl_speaker_gain - 40;
				gpl_speaker_temp_sign = "";
				if (gpl_speaker_gain_sign_temp > 0) {
					gpl_speaker_temp_sign = "+";
				}
				String gpl_speaker_gain_sign = gpl_speaker_temp_sign
						+ gpl_speaker_gain_sign_temp;
				textProgress.setText("" + gpl_speaker_gain_sign);
			}
		});

		// register OnSeekBarChangeListener, so it can actually change values
		seekbar_mic_gain
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Maybe no function for this in the future
						SharedPreferences sharedPreferences = getSharedPreferences(
								"MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putInt("gpl_mic_gain_pref", gpl_mic_gain);
						editor.commit();

						// Try catch block for if it may go wrong
						try {
							// Log.d(TAG, "echo gpl mic gain: " + gpl_mic_gain);
							// calls RootProcess
							RootProcess process = new RootProcess();
							if (!process.init()) {
								return;
							}
							// Writing the values to the files
							process.write("echo "
									+ gpl_mic_gain
									+ " > /sys/kernel/sound_control/gpl_mic_gain\n");
							// Log.d(TAG, "echo gpl mic gain: " + gpl_mic_gain);
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
						int siz = progress + 20;
						gpl_mic_gain = siz;
						// Log.d(TAG, "Progressbar: " + siz);
						// Log.d(TAG, "Progressbar: " + gpl_mic_gain);

						// change progress text label with current seekbar value
						int gpl_mic_gain_sign_temp = gpl_mic_gain - 40;
						gpl_mic_temp_sign = "";
						if (gpl_mic_gain_sign_temp > 0) {
							gpl_mic_temp_sign = "+";
						}
						String gpl_mic_gain_sign = gpl_mic_temp_sign
								+ gpl_mic_gain_sign_temp;
						textgplmicProgress.setText("" + gpl_mic_gain_sign);
					}
				});

		// register OnSeekBarChangeListener, so it can actually change values
		seekbar_cam_mic_gain
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Maybe no function for this in the future
						SharedPreferences sharedPreferences = getSharedPreferences(
								"MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putInt("gpl_cam_mic_gain_pref", gpl_cam_mic_gain);
						editor.commit();

						// Try catch block for if it may go wrong
						try {
							// Log.d(TAG, "echo gpl cam mic gain: "
							// + gpl_cam_mic_gain);
							// calls RootProcess
							RootProcess process = new RootProcess();
							if (!process.init()) {
								return;
							}
							// Writing the values to the files
							process.write("echo "
									+ gpl_cam_mic_gain
									+ " > /sys/kernel/sound_control/gpl_cam_mic_gain\n");
							// Log.d(TAG, "echo gpl cam mic gain: "
							// + gpl_cam_mic_gain);
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
						int siz = progress + 20;
						gpl_cam_mic_gain = siz;
						// Log.d(TAG, "Progressbar: " + siz);
						// Log.d(TAG, "Progressbar: " + gpl_cam_mic_gain);

						// change progress text label with current seekbar value
						int gpl_cam_mic_gain_sign_temp = gpl_cam_mic_gain - 40;
						gpl_cam_mic_temp_sign = "";
						if (gpl_cam_mic_gain_sign_temp > 0) {
							gpl_cam_mic_temp_sign = "+";
						}
						String gpl_cam_mic_gain_sign = gpl_cam_mic_temp_sign
								+ gpl_cam_mic_gain_sign_temp;
						textgplcammicProgress.setText(""
								+ gpl_cam_mic_gain_sign);
					}
				});

		// register OnSeekBarChangeListener, so it can actually change values
		seekbar_headphone_gain
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Maybe no function for this in the future
						SharedPreferences sharedPreferences = getSharedPreferences(
								"MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putInt("gpl_headphone_gain_pref",
								gpl_headphone_gain);
						editor.commit();

						// Try catch block for if it may go wrong
						try {
							// Log.d(TAG, "echo gpl_headphone_gain: "
							// + gpl_headphone_gain);
							// calls RootProcess
							RootProcess process = new RootProcess();
							if (!process.init()) {
								return;
							}
							// Writing the values to the files
							process.write("echo "
									+ gpl_headphone_gain
									+ " "
									+ gpl_headphone_gain
									+ " > /sys/kernel/sound_control/gpl_headphone_gain\n");
							// Log.d(TAG, "echo gpl_headphone_gain: "
							// + gpl_headphone_gain);
							// Value_gpl_headphone_gain =
							// Integer.parseInt(vCheck_gpl_headphone_gain.read(rootProcess));
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
						int siz = progress + 20;
						gpl_headphone_gain = siz;
						// Log.d(TAG, "Progressbar: " + siz);
						// Log.d(TAG, "Progressbar: " + gpl_headphone_gain);

						// change progress text label with current seekbar value
						int gpl_headphone_gain_sign_temp = gpl_headphone_gain - 40;
						gpl_headphone_temp_sign = "";
						if (gpl_headphone_gain_sign_temp > 0) {
							gpl_headphone_temp_sign = "+";
						}
						String gpl_headphone_gain_sign = gpl_headphone_temp_sign
								+ gpl_headphone_gain_sign_temp;
						textgplheadphoneProcess.setText(""
								+ gpl_headphone_gain_sign);
					}
				});

		// register OnSeekBarChangeListener, so it can actually change values
		seekbar_hdmi_spkr_gain
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Maybe no function for this in the future
						SharedPreferences sharedPreferences = getSharedPreferences(
								"MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putInt("gpl_hdmi_spkr_gain_pref",
								gpl_hdmi_spkr_gain);
						editor.commit();

						// Try catch block for if it may go wrong
						try {
							// Log.d(TAG, "echo gpl hdmi spkr gain: "
							// + gpl_hdmi_spkr_gain);
							// calls RootProcess
							RootProcess process = new RootProcess();
							if (!process.init()) {
								return;
							}
							// Writing the values to the files
							process.write("echo "
									+ gpl_hdmi_spkr_gain
									+ " > /sys/kernel/sound_control/gpl_hdmi_spkr_gain\n");
							// Log.d(TAG, "echo gpl hdmi spkr gain: "
							// + gpl_hdmi_spkr_gain);
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
						int siz = progress + 20;
						gpl_hdmi_spkr_gain = siz;
						// Log.d(TAG, "Progressbar: " + siz);
						// Log.d(TAG, "Progressbar: " + gpl_hdmi_spkr_gain);

						// change progress text label with current seekbar value
						int gpl_hdmi_spkr_gain_sign_temp = gpl_hdmi_spkr_gain - 40;
						gpl_hdmi_spkr_temp_sign = "";
						if (gpl_hdmi_spkr_gain_sign_temp > 0) {
							gpl_hdmi_spkr_temp_sign = "+";
						}
						String gpl_hdmi_spkr_gain_sign = gpl_hdmi_spkr_temp_sign
								+ gpl_hdmi_spkr_gain_sign_temp;
						textgplhdmispkrgainProgress.setText(""
								+ gpl_hdmi_spkr_gain_sign);
					}
				});

		// register OnSeekBarChangeListener, so it can actually change values
		seekbar_headset_mic_gain
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Maybe no function for this in the future
						SharedPreferences sharedPreferences = getSharedPreferences(
								"MY_SHARED_PREF", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putInt("gpl_headset_mic_gain_pref",
								gpl_headset_mic_gain);
						editor.commit();

						// Try catch block for if it may go wrong
						try {
							// Log.d(TAG, "echo gpl_headset_mic_gain: "
							// + gpl_headset_mic_gain);
							// calls RootProcess
							RootProcess process = new RootProcess();
							if (!process.init()) {
								return;
							}
							// Writing the values to the files
							process.write("echo "
									+ gpl_headset_mic_gain
									+ " > /sys/kernel/sound_control/gpl_headset_mic_gain\n");
							// Log.d(TAG, "echo gpl_headset_mic_gain: "
							// + gpl_headset_mic_gain);
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
						int siz = progress + 20;
						gpl_headset_mic_gain = siz;
						// Log.d(TAG, "Progressbar: " + siz);
						// Log.d(TAG, "Progressbar: " + gpl_headset_mic_gain);

						// change progress text label with current seekbar value
						int gpl_headset_mic_gain_sign_temp = gpl_headset_mic_gain - 40;
						gpl_headset_mic_temp_sign = "";
						if (gpl_headset_mic_gain_sign_temp > 0) {
							gpl_headset_mic_temp_sign = "+";
						}
						String gpl_headset_mic_gain_sign = gpl_headset_mic_temp_sign
								+ gpl_headset_mic_gain_sign_temp;
						textgplheadsetmicgainProgress.setText(""
								+ gpl_headset_mic_gain_sign);
					}
				});

		// Filechecking part
		soundtweaks_hide_dialog = sharedPreferences.getInt(
				"soundtweaks_hide_dialog", 0);
		// Log.d(TAG, "onCreate cpu_hide_dialog = " + soundtweaks_hide_dialog);

		// Options Compatible Check
		FileCheck.CheckSoundOptions(SoundTweaks.this);
		// Hide Options if it isn't compatible
		OptionsHider.SoundTweaksHider(SoundTweaks.this);

		if (FileCheck.incompatible == true) {
			if (soundtweaks_hide_dialog == 1) {
				// Log.d(TAG, "hide the dialog");
			} else {
				// Log.d(TAG, "show dialog");
				Intent intent = new Intent(SoundTweaks.this,
						DialogActivity.class);
				startActivityForResult(intent, GET_CODE);
			}
			// Log.d(TAG, "incompatible = " + FileCheck.incompatible);
		} else {
			// Log.d(TAG, "incompatible = " + FileCheck.incompatible);
		}
	}

	// on boot switch
	// TODO
	public void onBootSoundTweaks(View view) {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"MY_SHARED_PREF", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		boolean on = ((Switch) view).isChecked();
		if (on) {
			editor.putBoolean("onBootSoundTweaks_pref", true);
			editor.commit();
			// Log.d(TAG, "onBoot Enabled for SoundTweaks");
		} else {
			editor.putBoolean("onBootSoundTweaks_pref", false);
			editor.commit();
			// Log.d(TAG, "onBoot Disabled for SoundTweaks");
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
					// Log.d(TAG, "RESULT_DATA = " + data.getAction());
					SharedPreferences sharedPreferences = getSharedPreferences(
							"MY_SHARED_PREF", 0);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putInt("soundtweaks_hide_dialog",
							Integer.parseInt(data.getAction()));
					editor.commit();
				}
			}
		}
	}

	// Definition of the one requestCode we use for receiving resuls.
	static final private int GET_CODE = 0;
}
