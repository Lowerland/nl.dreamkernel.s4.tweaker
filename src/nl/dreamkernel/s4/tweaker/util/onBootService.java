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

package nl.dreamkernel.s4.tweaker.util;

import nl.dreamkernel.s4.tweaker.cpu.CpuTweaks;
import nl.dreamkernel.s4.tweaker.misc.Misc;
import nl.dreamkernel.s4.tweaker.soundtweaks.SoundTweaks;
import nl.dreamkernel.s4.tweaker.util.RootProcess;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class onBootService extends Service {

	static final String TAG = "S4Tweaker";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "*************** onBootService onBind ****************");
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.d(TAG, "*************** Service Created ****************");
		super.onCreate();

		final SharedPreferences sharedPreferences = getSharedPreferences(
				"MY_SHARED_PREF", 0);

		boolean onBootCpuTweaks_pref = sharedPreferences.getBoolean(
				"onBootCpuTweaks_pref", false);
		String cpu_scaling_governor_pref = sharedPreferences.getString(
				"cpu_scaling_governor_pref", "");
		String scaling_min_freq_pref = sharedPreferences.getString(
				"scaling_min_freq_pref", "");
		String scaling_max_freq_pref = sharedPreferences.getString(
				"scaling_max_freq_pref", "");

		boolean onBootMiscTweaks_pref = sharedPreferences.getBoolean(
				"onBootMiscTweaks_pref", false);
		String internalscheduler_pref = sharedPreferences.getString(
				"internalscheduler_pref", "");
		String externalscheduler_pref = sharedPreferences.getString(
				"externalscheduler_pref", "");
		int value_vibrator = sharedPreferences.getInt("value_vibrator", 0);
		int usb_fast_charge_pref = sharedPreferences.getInt(
				"usb_fast_charge_pref", 0);

		boolean onBootSoundTweaks_pref = sharedPreferences.getBoolean(
				"onBootSoundTweaks_pref", false);
		int gpl_speaker_gain_pref = sharedPreferences.getInt(
				"gpl_speaker_gain_pref", 0);
		int gpl_mic_gain_pref = sharedPreferences
				.getInt("gpl_mic_gain_pref", 0);
		int gpl_cam_mic_gain_pref = sharedPreferences.getInt(
				"gpl_cam_mic_gain_pref", 0);
		int gpl_headphone_gain_pref = sharedPreferences.getInt(
				"gpl_headphone_gain_pref", 0);
		int gpl_hdmi_spkr_gain_pref = sharedPreferences.getInt(
				"gpl_hdmi_spkr_gain_pref", 0);
		int gpl_headset_mic_gain_pref = sharedPreferences.getInt(
				"gpl_headset_mic_gain_pref", 0);

		if (onBootCpuTweaks_pref == true) {
			Log.d(TAG, "*************** onBootCpuTweaks ***************");
			Toast.makeText(this, "Applying Cpu Tweaks", Toast.LENGTH_SHORT)
					.show();
			CpuTweaks.CpuCurrentState();
			sleep(500);
			RootProcess cpuprocess = new RootProcess();
			if (!cpuprocess.init()) {
				return;
			}
			Log.d(TAG, "**** Set Cpu Tweaks ****");
			if (CpuTweaks.vCheck_CPU_GOVERNOR.exists()) {

				if (sharedPreferences.contains("cpu_scaling_governor_pref") == true) {
					Log.d(TAG, "set cpu_scaling_governor_pref == "
							+ cpu_scaling_governor_pref);
					cpuprocess
							.write("echo "
									+ cpu_scaling_governor_pref
									+ " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
				}
			}
			if (CpuTweaks.vCheck_CPU_CpuMinFREQ.exists()) {

				if (sharedPreferences.contains("scaling_min_freq_pref") == true) {

					Log.d(TAG, "set scaling_min_freq_pref Cpu 0 == "
							+ scaling_min_freq_pref);
					cpuprocess
							.write("echo "
									+ scaling_min_freq_pref
									+ " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
				}
			}
			if (CpuTweaks.vCheck_CPU_CpuMaxFREQ.exists()) {

				if (sharedPreferences.contains("scaling_max_freq_pref") == true) {

					Log.d(TAG, "set scaling_max_freq_pref Cpu 0 == "
							+ scaling_max_freq_pref);
					cpuprocess
							.write("echo "
									+ scaling_max_freq_pref
									+ " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
				}
			}
			if (CpuTweaks.vCheck_CPU1_ONLINE.exists()) {

				if (sharedPreferences.contains("scaling_min_freq_pref") == true) {

					Log.d(TAG, "set scaling_min_freq_pref Cpu 1 == "
							+ scaling_min_freq_pref);
					cpuprocess
							.write("echo "
									+ scaling_min_freq_pref
									+ " > /sys/devices/system/cpu/cpu1/cpufreq/scaling_min_freq\n");
				}
				if (sharedPreferences.contains("scaling_max_freq_pref") == true) {
					Log.d(TAG, "set scaling_max_freq_pref Cpu 1 == "
							+ scaling_max_freq_pref);
					cpuprocess
							.write("echo "
									+ scaling_max_freq_pref
									+ " > /sys/devices/system/cpu/cpu1/cpufreq/scaling_max_freq\n");
				}
			}
			if (CpuTweaks.vCheck_CPU2_ONLINE.exists()) {

				if (sharedPreferences.contains("scaling_min_freq_pref") == true) {

					Log.d(TAG, "set scaling_min_freq_pref Cpu 2 == "
							+ scaling_min_freq_pref);
					cpuprocess
							.write("echo "
									+ scaling_min_freq_pref
									+ " > /sys/devices/system/cpu/cpu2/cpufreq/scaling_min_freq\n");
				}
				if (sharedPreferences.contains("scaling_max_freq_pref") == true) {
					Log.d(TAG, "set scaling_max_freq_pref Cpu 2 == "
							+ scaling_max_freq_pref);
					cpuprocess
							.write("echo "
									+ scaling_max_freq_pref
									+ " > /sys/devices/system/cpu/cpu2/cpufreq/scaling_max_freq\n");
				}
			}
			if (CpuTweaks.vCheck_CPU3_ONLINE.exists()) {

				if (sharedPreferences.contains("scaling_min_freq_pref") == true) {

					Log.d(TAG, "set scaling_min_freq_pref Cpu 3 == "
							+ scaling_min_freq_pref);
					cpuprocess
							.write("echo "
									+ scaling_min_freq_pref
									+ " > /sys/devices/system/cpu/cpu3/cpufreq/scaling_min_freq\n");
				}
				if (sharedPreferences.contains("scaling_max_freq_pref") == true) {
					Log.d(TAG, "set scaling_max_freq_pref Cpu 3 == "
							+ scaling_max_freq_pref);
					cpuprocess
							.write("echo "
									+ scaling_max_freq_pref
									+ " > /sys/devices/system/cpu/cpu3/cpufreq/scaling_max_freq\n");
				}
			}
			CpuTweaks.ReturnCpuState();
			sleep(100);
			cpuprocess.term();
		}
		if (onBootMiscTweaks_pref == true) {
			Log.d(TAG, "*************** onBootMiscTweaks ***************");
			Toast.makeText(this, "Applying Misc Tweaks", Toast.LENGTH_SHORT)
					.show();
			RootProcess miscprocess = new RootProcess();
			if (!miscprocess.init()) {
				return;
			}
			if (Misc.vCheck_internalscheduler.exists()) {

				if (sharedPreferences.contains("internalscheduler_pref") == true) {
					Log.d(TAG, "set internalscheduler_pref == "
							+ internalscheduler_pref);
					miscprocess.write("echo " + internalscheduler_pref
							+ " > /sys/block/mmcblk0/queue/scheduler\n");
				}
			}
			if (Misc.vCheck_externalscheduler.exists()) {

				if (sharedPreferences.contains("externalscheduler_pref") == true) {

					Log.d(TAG, "set externalscheduler_pref == "
							+ externalscheduler_pref);
					miscprocess.write("echo " + externalscheduler_pref
							+ " > /sys/block/mmcblk1/queue/scheduler\n");
				}
			}
			if (Misc.vCheck_vibrator_intensity.exists()) {

				if (sharedPreferences.contains("value_vibrator") == true) {

					Log.d(TAG, "set value_vibrator == " + value_vibrator);
					miscprocess.write("echo " + value_vibrator
							+ " > /sys/vibrator/pwm_val\n");
				}
			}
			if (Misc.vCheck_Usb_Fast_charge.exists()) {

				if (sharedPreferences.contains("usb_fast_charge_pref") == true) {

					Log.d(TAG, "set usb_fast_charge_pref == "
							+ usb_fast_charge_pref);
					miscprocess.write("echo " + usb_fast_charge_pref
							+ " > /sys/kernel/fast_charge/force_fast_charge\n");
				}
			}
			miscprocess.term();
		}
		if (onBootSoundTweaks_pref == true) {
			Log.d(TAG, "*************** onBootSoundTweaks ***************");
			Toast.makeText(this, "Applying Sound Tweaks", Toast.LENGTH_SHORT)
					.show();
			RootProcess soundprocess = new RootProcess();
			if (!soundprocess.init()) {
				return;
			}
			if (SoundTweaks.vCheck_gpl_speaker_gain.exists()) {

				if (sharedPreferences.contains("gpl_speaker_gain_pref") == true) {
					Log.d(TAG, "set gpl_speaker_gain_pref == "
							+ gpl_speaker_gain_pref);
					soundprocess
							.write("echo "
									+ gpl_speaker_gain_pref
									+ " > /sys/kernel/sound_control/gpl_speaker_gain\n");
				}
			}
			if (SoundTweaks.vCheck_gpl_mic_gain.exists()) {

				if (sharedPreferences.contains("gpl_mic_gain_pref") == true) {

					Log.d(TAG, "set gpl_mic_gain_pref == " + gpl_mic_gain_pref);
					soundprocess.write("echo " + gpl_mic_gain_pref
							+ " > /sys/kernel/sound_control/gpl_mic_gain\n");
				}
			}
			if (SoundTweaks.vCheck_gpl_cam_mic_gain.exists()) {

				if (sharedPreferences.contains("gpl_cam_mic_gain_pref") == true) {

					Log.d(TAG, "set gpl_cam_mic_gain_pref == "
							+ gpl_cam_mic_gain_pref);
					soundprocess
							.write("echo "
									+ gpl_cam_mic_gain_pref
									+ " > /sys/kernel/sound_control/gpl_cam_mic_gain\n");
				}
			}
			if (SoundTweaks.vCheck_gpl_headphone_gain.exists()) {

				if (sharedPreferences.contains("gpl_headphone_gain_pref") == true) {

					Log.d(TAG, "set gpl_headphone_gain_pref == "
							+ gpl_headphone_gain_pref);
					soundprocess
							.write("echo "
									+ gpl_headphone_gain_pref
									+ " "
									+ gpl_headphone_gain_pref
									+ " > /sys/kernel/sound_control/gpl_headphone_gain\n");
				}
			}
			if (SoundTweaks.vCheck_gpl_hdmi_speaker_gain.exists()) {

				if (sharedPreferences.contains("gpl_hdmi_spkr_gain_pref") == true) {

					Log.d(TAG, "set gpl_hdmi_spkr_gain_pref == "
							+ gpl_hdmi_spkr_gain_pref);
					soundprocess
							.write("echo "
									+ gpl_hdmi_spkr_gain_pref
									+ " > /sys/kernel/sound_control/gpl_hdmi_spkr_gain\n");
				}
			}
			if (SoundTweaks.vCheck_gpl_headset_mic_gain.exists()) {

				if (sharedPreferences.contains("gpl_headset_mic_gain_pref") == true) {

					Log.d(TAG, "set gpl_headset_mic_gain_pref == "
							+ gpl_headset_mic_gain_pref);
					soundprocess
							.write("echo "
									+ gpl_headset_mic_gain_pref
									+ " > /sys/kernel/sound_control/gpl_headset_mic_gain\n");
				}
			}
			soundprocess.term();
		}
		sleep(1000);
		stopService(new Intent(onBootService.this, onBootService.class));
	}

	public static void sleep(long msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		// Tell the user we stopped.
		Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();

		Log.d(TAG, "*************** Service Destroyed ****************");
	}

}
