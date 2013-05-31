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

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import nl.dreamkernel.s4.tweaker.R;
import nl.dreamkernel.s4.tweaker.cpu.CpuTweaks;
import nl.dreamkernel.s4.tweaker.misc.Misc;
import nl.dreamkernel.s4.tweaker.soundtweaks.SoundTweaks;
import nl.dreamkernel.s4.tweaker.util.SysFs;


public class FileCheck {
	
	// Set File Constants
	public static final SysFs vCheck_BUSYBOX = new SysFs("/system/xbin/busybox");
	
	
	// Initialize Variables for SoundTweaks
	public static int gpl_spk_hide;
	public static int gpl_mic_hide;
	public static int gpl_cam_mic_hide;
	public static int gpl_headphone_hide;
	public static int gpl_hdmi_spkr_hide;
	public static int gpl_headset_mic_hide;
	// Initialize Variables for CpuTweaks
	public static int cpuGovernor_hide;
	public static int cpuMinFreq_hide;
	public static int cpuMaxFreq_hide;
	// Initialize Variables for MiscTweaks
	public static int internal_scheduler_hide;
	public static int external_scheduler_hide;
	public static int vibrator_intensity_hide;
	public static int Usb_Fast_charge_hide;
	// Initialize Boolean AlertDialog
	static boolean incompatible;
	//////
	// FileChecking Methods
	/////

	// SoundTweaks FileChecking Method
	public static void CheckSoundOptions(Context context) {
		
		incompatible = false;
		gpl_spk_hide = 0;
		gpl_mic_hide = 0;
		gpl_cam_mic_hide = 0;
		gpl_headphone_hide = 0;
		gpl_hdmi_spkr_hide = 0;
		gpl_headset_mic_hide = 0;		
		
		if (!SoundTweaks.vCheck_gpl_speaker_gain.exists()) {
			gpl_spk_hide = 1;
			incompatible = true;
	    } 
		if (!SoundTweaks.vCheck_gpl_mic_gain.exists()) {
	    	gpl_mic_hide = 1;
			incompatible = true;	    	
	    }
		if (!SoundTweaks.vCheck_gpl_cam_mic_gain.exists()) {
			gpl_cam_mic_hide = 1;
			incompatible = true;	    	
	    }
		if (!SoundTweaks.vCheck_gpl_headphone_gain.exists()) {
			gpl_headphone_hide = 1;
			incompatible = true;	    	
	    }
		if (!SoundTweaks.vCheck_gpl_hdmi_speaker_gain.exists()) {
			gpl_hdmi_spkr_hide = 1;
			incompatible = true;	    	
	    }
		if (!SoundTweaks.vCheck_gpl_headset_mic_gain.exists()) {
			gpl_headset_mic_hide = 1;
			incompatible = true;	    	
	    }
		AlertDialog(context);		
	}
	
	// CpuTweaks FileChecking Method
	public static void CheckCPUOptions(Context context) {
		
		incompatible = false;
		cpuGovernor_hide = 0;
		cpuMinFreq_hide = 0;
		cpuMaxFreq_hide = 0;
		
		if (!CpuTweaks.vCheck_CPU_GOVERNOR.exists()) {
			cpuGovernor_hide = 1;
			incompatible = true;
			}
		if (!CpuTweaks.vCheck_CPU_CpuMinFREQ.exists()) {
			cpuMinFreq_hide = 1;
			incompatible = true;
			}
		if (!CpuTweaks.vCheck_CPU_CpuMaxFREQ.exists()) {
			cpuMaxFreq_hide = 1;
			incompatible = true;
			}
		AlertDialog(context);
		}

	// MiscTweaks FileChecking Method
	public static void CheckMiscOptions(Context context) {
		
		incompatible = false;
		internal_scheduler_hide = 0;
		external_scheduler_hide = 0;
		vibrator_intensity_hide = 0;
		Usb_Fast_charge_hide = 0;

		if (!Misc.vCheck_internalscheduler.exists()) {
			internal_scheduler_hide = 1;
			incompatible = true;
			}
		/*if (!Misc.vCheck_externalscheduler.exists()) {  // <--- TEMP DISABLED
			external_scheduler_hide = 1;
			incompatible = true;
			}*/
		if (!Misc.vCheck_vibrator_intensity.exists()) {
			vibrator_intensity_hide = 1;
			incompatible = true;
			}
		if (!Misc.vCheck_Usb_Fast_charge.exists()) {
			Usb_Fast_charge_hide = 1;
			incompatible = true;
			}
		AlertDialog(context);
	}
	
	// Root acces check
	public static boolean isRootEnabled() {
        File file = new File("/system/bin/su");
        if (file.exists()) {
            return true;
        }
        File file2 = new File("/system/xbin/su");
        if (file2.exists()) {
            return true;
        }
        return false;
    }

	// Show Incompatible Alert if things arent right
	public static void AlertDialog(Context context) {
		if (incompatible == true) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			final FrameLayout frameView = new FrameLayout(context);
			builder.setView(frameView);
			final AlertDialog alertDialog = builder.create();
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
					"OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Button OK Clicked
					}
				});
			LayoutInflater inflater = alertDialog.getLayoutInflater();
			@SuppressWarnings("unused")
			View dialoglayout = inflater.inflate(R.layout.dialog_alert, frameView);
			alertDialog.show();
		}
	}
	
}
