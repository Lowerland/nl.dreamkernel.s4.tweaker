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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import nl.dreamkernel.s4.tweaker.R;
import nl.dreamkernel.s4.tweaker.soundtweaks.SoundTweaks;


public class FileCheck {
	
	
	
	
	public static String getCompatibility_Alert(Context context) {
        return context.getString(R.string.compatibility_alert_body);
    }	


	/*public boolean gpl_cam_mic_hide=false;
	public boolean gpl_headphone_hide=false;
	public boolean gpl_hdmi_spkr_hide=false;
	public boolean gpl_headset_mic_hide=false;*/
	

	
	
	public static int gpl_spk_hide;
	public static int gpl_mic_hide;
	public static int gpl_cam_mic_hide;
	public static int gpl_headphone_hide;
	public static int gpl_hdmi_spkr_hide;
	public static int gpl_headset_mic_hide;
	static boolean incompatible;
	
	// Check if options are compatibel with the filesystem
	public static void CheckOptions(Context context) {
		
		incompatible = false;
		gpl_spk_hide = 0;
		gpl_mic_hide = 0;
		gpl_cam_mic_hide = 0;
		gpl_headphone_hide = 0;
		gpl_hdmi_spkr_hide = 0;
		gpl_headset_mic_hide = 0;
		
		// SoundTweaks FileChecks
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
		// Show Incompatible Alert because things arent right here.
		if (incompatible == true) {
	    	/*AlertDialog alert = new AlertDialog.Builder(context).create();
	    	alert.setIconAttribute(android.R.attr.alertDialogIcon);
	    	alert.setTitle(R.string.compatibility_alert_title);
	    	alert.setMessage(getCompatibility_Alert(context));
	    	alert.setButton(DialogInterface.BUTTON_POSITIVE,
                    "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	// Button OK Clicked
                }
            });	    	
	    	alert.show();*/
			
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
				
            //.setTitle("My title")
            //.setMessage("Enter password");
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
			
			View dialoglayout = inflater.inflate(R.layout.dialog_alert, frameView);
			alertDialog.show();
	    }
	}	
}
