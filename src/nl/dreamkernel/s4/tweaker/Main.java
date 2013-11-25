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

package nl.dreamkernel.s4.tweaker;

import com.google.analytics.tracking.android.EasyTracker;

import nl.dreamkernel.s4.tweaker.R;
import nl.dreamkernel.s4.tweaker.bugs.BugsReporter;
import nl.dreamkernel.s4.tweaker.cpu.CpuTweaks;
import nl.dreamkernel.s4.tweaker.misc.Misc;
import nl.dreamkernel.s4.tweaker.soundtweaks.SoundTweaks;
import nl.dreamkernel.s4.tweaker.systeminfo.SysInfo;
import nl.dreamkernel.s4.tweaker.util.FileCheck;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class Main extends Activity {
	// static final String TAG = "S4Tweaker";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getting versionCode declared in the manifest
		// must be called before accessing any preferenes.
		int version = 1;
		try {
			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (version == 26) {
			// This Build Version requires Clean CPU Freq config
			// Else weird things can happen :-)
			updatePreferences();
		}

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new MyPreferenceFragment())
				.commit();
		// setContentView(R.layout.main);
		// getActionBar().hide();

		// Usage counter
		SharedPreferences sharedPreferences = getSharedPreferences(
				"MY_SHARED_PREF", 0);
		int usage_counter = sharedPreferences.getInt("usage_counter", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("usage_counter", usage_counter + 1);
		editor.commit();
		if (usage_counter == 100) {
			startActivity(new Intent(this,
					nl.dreamkernel.s4.tweaker.about.About.class));
		}
		if (usage_counter == 500) {
			startActivity(new Intent(this,
					nl.dreamkernel.s4.tweaker.about.About.class));
		}
		if (usage_counter == 1000) {
			startActivity(new Intent(this,
					nl.dreamkernel.s4.tweaker.about.About.class));
		}
		// Log.d(TAG, "App started " + usage_counter + " times");
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.main, menu); return true; }
	 */

	public static class MyPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.main_pref);
		}
	}

	void updatePreferences() {
		SharedPreferences prefs = getSharedPreferences("MY_SHARED_PREF", 0);
		if (prefs.getBoolean("update_required", true)) {
			SharedPreferences.Editor editor = prefs.edit();

			// editor.clear();

			/* ....make the updates.... */

			Toast.makeText(Main.this, "For System Safety Reasons",
					Toast.LENGTH_LONG).show();
			Toast.makeText(Main.this, "Update has cleared the config",
					Toast.LENGTH_LONG).show();

			editor.remove("CpuMinFREQPref");
			editor.remove("cpu_min_freq_array");
			editor.remove("scaling_min_freq_pref");
			editor.remove("CpuMaxFREQPref");
			editor.remove("cpu_max_freq_array");
			editor.remove("scaling_max_freq_pref");
			editor.remove("onBootCpuTweaks_pref");

			// TODO
			// FIXME uncomment THIS !!
			editor.putBoolean("update_required", false);
			editor.commit();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// Shows a notice if bug report was succesfull
		if (BugsReporter.bugrecieved == true) {
			Toast.makeText(Main.this, "Bug Report Successful",
					Toast.LENGTH_LONG).show();
			Toast.makeText(Main.this, "Thank You For Your Support  :)",
					Toast.LENGTH_LONG).show();

			BugsReporter.bugrecieved = false;
		}
		if (CpuTweaks.noRoot == true | Misc.noRoot == true
				| SoundTweaks.noRoot == true | SysInfo.noRoot == true) {
			// Log.d(TAG,
			// "FileCheck.isRootEnabled() = " + FileCheck.isRootEnabled());
			// Show Root required alert
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final FrameLayout frameView = new FrameLayout(this);
			builder.setView(frameView);
			final AlertDialog norootDialog = builder.create();
			norootDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Button OK Clicked
							// Exit App
						}
					});
			LayoutInflater inflater = norootDialog.getLayoutInflater();
			@SuppressWarnings("unused")
			View dialoglayout = inflater.inflate(R.layout.no_root_alert,
					frameView);
			norootDialog.show();
			CpuTweaks.noRoot = false;
			Misc.noRoot = false;
			SoundTweaks.noRoot = false;
			SysInfo.noRoot = false;
		} else {
			// Log.d(TAG, "Got root access");
		}
		// Log.d(TAG, "onResume() " + FileCheck.isRootEnabled());
		if (!FileCheck.isRootEnabled() == true) {
			// Log.d(TAG,
			// "FileCheck.isRootEnabled() = " + FileCheck.isRootEnabled());
			// Show Root required alert
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final FrameLayout frameView = new FrameLayout(this);
			builder.setView(frameView);
			final AlertDialog norootDialog = builder.create();
			norootDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Button OK Clicked
							// Exit App
							finish();
						}
					});
			LayoutInflater inflater = norootDialog.getLayoutInflater();
			@SuppressWarnings("unused")
			View dialoglayout = inflater.inflate(R.layout.no_root_alert,
					frameView);
			norootDialog.show();
		} else {
			// Log.d(TAG, "Got root access");
		}
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
