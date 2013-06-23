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

import nl.dreamkernel.s4.tweaker.R;
import nl.dreamkernel.s4.tweaker.cpu.CpuTweaks;
import nl.dreamkernel.s4.tweaker.misc.Misc;
import nl.dreamkernel.s4.tweaker.soundtweaks.SoundTweaks;
import android.content.Context;
import android.view.View;

public class OptionsHider {
	//private static final String TAG = "S4Tweaker";

	// Methods used for hiding uncompatible options

	// Hiding method for Cpu Tweaks
	public static void CpuTweaksHider(Context context) {
		//Log.d(TAG, "OptionsHider() cpuGovernor_hide = "
		//		+ FileCheck.cpuGovernor_hide);
		if (FileCheck.cpuGovernor_hide == 1) {
			CpuTweaks.Touch_block_governor.setVisibility(View.GONE);
			CpuTweaks.CpuCurrentValue.setVisibility(View.GONE);
			CpuTweaks.textuncompatibel.setText(R.string.disabled_option_text);
		}
		//Log.d(TAG, "OptionsHider() cpuMinFreq_hide = "
		//		+ FileCheck.cpuMinFreq_hide);
		if (FileCheck.cpuMinFreq_hide == 1) {
			CpuTweaks.Touch_block_min_freq.setVisibility(View.GONE);
			CpuTweaks.CpuMinFREQValue.setVisibility(View.GONE);
			CpuTweaks.textuncompatibel2.setText(R.string.disabled_option_text);
		}
		//Log.d(TAG, "OptionsHider() cpuMaxFreq_hide = "
		//		+ FileCheck.cpuMaxFreq_hide);
		if (FileCheck.cpuMaxFreq_hide == 1) {
			CpuTweaks.Touch_block_max_freq.setVisibility(View.GONE);
			CpuTweaks.CpuMaxFREQValue.setVisibility(View.GONE);
			CpuTweaks.textuncompatibel3.setText(R.string.disabled_option_text);
		}
	}

	// Hiding Method for Misc Tweaks
	public static void MiscTweaksHider(Context context) {

		//Log.d(TAG, "OptionsHider() internal_scheduler_hide = "
		//		+ FileCheck.internal_scheduler_hide);
		if (FileCheck.internal_scheduler_hide == 1) {
			Misc.Touch_block_int_scheduler.setVisibility(View.GONE);
			Misc.InternalValue.setVisibility(View.GONE);
			Misc.textuncompatibel.setText(R.string.disabled_option_text);
		}
		//Log.d("external_scheduler_hide",
		//		"OptionsHider() external_scheduler_hide = "
		//				+ FileCheck.external_scheduler_hide);

		if (FileCheck.external_scheduler_hide == 1) {
			Misc.Touch_block_ext_scheduler.setVisibility(View.GONE);
			Misc.ExternalValue.setVisibility(View.GONE);
			Misc.textuncompatibel2.setText(R.string.disabled_option_text);
		}
		//Log.d(TAG, "OptionsHider() vibrator_intensity_hide = "
		//		+ FileCheck.vibrator_intensity_hide);
		if (FileCheck.vibrator_intensity_hide == 1) {
			Misc.seekbar_vibrator.setVisibility(View.GONE);
			Misc.vibratorProgress.setVisibility(View.GONE);
			Misc.textuncompatibel3.setText(R.string.disabled_option_text);
		}
		//Log.d(TAG, "OptionsHider() Usb_Fast_charge_hide = "
		//		+ FileCheck.Usb_Fast_charge_hide);
		if (FileCheck.Usb_Fast_charge_hide == 1) {
			Misc.usbfastchargeswitch.setVisibility(View.GONE);
			Misc.textuncompatibel4.setText(R.string.disabled_option_text);
		}
	}

	// Hiding Method for Sound Tweaks
	public static void SoundTweaksHider(Context context){

	//Log.d("gpl_spk_hide", "OptionsHider() gpl_spk_hide = "
	//		+ FileCheck.gpl_spk_hide);
	if (FileCheck.gpl_spk_hide == 1) {
		SoundTweaks.seekbar_gpl.setVisibility(View.GONE);
		SoundTweaks.textProgress.setVisibility(View.GONE);
		SoundTweaks.textuncompatibel.setText(R.string.disabled_option_text);
	}

	//Log.d("gpl_mic_hide", "OptionsHider() gpl_mic_hide = "
	//		+ FileCheck.gpl_mic_hide);
	if (FileCheck.gpl_mic_hide == 1) {
		SoundTweaks.seekbar_mic_gain.setVisibility(View.GONE);
		SoundTweaks.textgplmicProgress.setVisibility(View.GONE);
		SoundTweaks.textuncompatibel2.setText(R.string.disabled_option_text);
	}

	//Log.d("gpl_mic_hide", "OptionsHider() gpl_cam_mic_hide = "
	//		+ FileCheck.gpl_cam_mic_hide);
	if (FileCheck.gpl_cam_mic_hide == 1) {
		SoundTweaks.seekbar_cam_mic_gain.setVisibility(View.GONE);
		SoundTweaks.textgplcammicProgress.setVisibility(View.GONE);
		SoundTweaks.textuncompatibel3.setText(R.string.disabled_option_text);
	}

	//Log.d("gpl_mic_hide", "OptionsHider() gpl_headphone_hide = "
	//		+ FileCheck.gpl_headphone_hide);
	if (FileCheck.gpl_headphone_hide == 1) {
		SoundTweaks.seekbar_headphone_gain.setVisibility(View.GONE);
		SoundTweaks.textgplheadphoneProcess.setVisibility(View.GONE);
		SoundTweaks.textuncompatibel4.setText(R.string.disabled_option_text);
	}

	//Log.d("gpl_mic_hide", "OptionsHider() gpl_hdmi_spkr_hide = "
	//		+ FileCheck.gpl_hdmi_spkr_hide);
	if (FileCheck.gpl_hdmi_spkr_hide == 1) {
		SoundTweaks.seekbar_hdmi_spkr_gain.setVisibility(View.GONE);
		SoundTweaks.textgplhdmispkrgainProgress.setVisibility(View.GONE);
		SoundTweaks.textuncompatibel5.setText(R.string.disabled_option_text);
	}

	//Log.d("gpl_mic_hide", "OptionsHider() gpl_headset_mic_hide = "
	//		+ FileCheck.gpl_headset_mic_hide);
	if (FileCheck.gpl_headset_mic_hide == 1) {
		SoundTweaks.seekbar_headset_mic_gain.setVisibility(View.GONE);
		SoundTweaks.textgplheadsetmicgainProgress.setVisibility(View.GONE);
		SoundTweaks.textuncompatibel6.setText(R.string.disabled_option_text);
	}
	}

}
