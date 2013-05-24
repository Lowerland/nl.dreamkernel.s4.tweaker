package nl.dreamkernel.s4.tweaker.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.dreamkernel.s4.tweaker.util.SysFs;
import nl.dreamkernel.s4.tweaker.util.RootProcess;
import nl.dreamkernel.s4.tweaker.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Misc extends Activity {

	// variables for the Textviews
	private TextView InternalValue;
	private TextView ExternalValue;

	// Variables for file paths
	//private static final SysFs vCheck_internalscheduler = new SysFs("/sys/devices/platform/msm_sdcc.1/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler");
	//private static final SysFs vCheck_externalscheduler = new SysFs("/sys/devices/platform/msm_sdcc.2/mmc_host/mmc2/mmc2:0002/block/mmcblk1/queue/scheduler");
	private static final SysFs vCheck_internalscheduler = new SysFs("/data/data/nl.dreamkernel.s4.tweaker/files/internalscheduler");
	private static final SysFs vCheck_externalscheduler = new SysFs("/data/data/nl.dreamkernel.s4.tweaker/files/externalscheduler");
	
	
	private String file_value_internal;
	private String file_value_temp;

	private String file_value_external;
	private String file_value_temp2;
	
	// Variables

	// variables to store the shared pref in
	private int InternalPrefValue;
	private int ExternalPrefValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.misctweaks);
		setTitle(R.string.menu_misc_tweaks);
		getActionBar().hide();
		

		


		// Read in the Values from files
		RootProcess rootProcess = new RootProcess();
		Log.d("MiscTweaks", "Misc Tweaks, Root init s");
		rootProcess.init();
		Log.d("MiscTweaks", "Misc Tweaks, Root init e");

		if (vCheck_internalscheduler.exists()) {
			file_value_temp = vCheck_internalscheduler.read(rootProcess);
		
			Pattern p = Pattern.compile("\\[((.*?)\\])");
			Matcher m = p.matcher(file_value_temp);
			if (m.find()){
				file_value_internal = m.toMatchResult().group(0);
				file_value_internal = file_value_internal.replace("[", "").replace("]", "");
			}
			
		} else {
			
		}
		
		if (vCheck_externalscheduler.exists()) {
			file_value_temp2 = vCheck_externalscheduler.read(rootProcess);
			
			Pattern a = Pattern.compile("\\[((.*?)\\])");
			Matcher b = a.matcher(file_value_temp2);
			if (b.find()){
				file_value_external = b.toMatchResult().group(0);
				file_value_external = file_value_external.replace("[", "").replace("]", "");
			}
		} else {
			//gpl_speaker_gain = value_default;
		}

		rootProcess.term();
		rootProcess = null;
		////
		
		// Find current value views
		InternalValue = (TextView)findViewById(R.id.InternalValue);
		ExternalValue = (TextView)findViewById(R.id.ExternalValue);
		
		// Set current value views
		InternalValue.setText(""+file_value_internal);
		ExternalValue.setText(""+file_value_external);

		//get the Shared Prefs
		final SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
		InternalPrefValue = sharedPreferences.getInt("InternalPref", 0);
		ExternalPrefValue = sharedPreferences.getInt("ExternalPref", 0);
		
		
		
		// Dropdown menu for I/O Scheduler Internal
		Spinner sInternal = (Spinner) findViewById(R.id.internalspinner);

		ArrayAdapter<CharSequence> internaladapter = ArrayAdapter
				.createFromResource(this, R.array.ioInternal,
						android.R.layout.simple_spinner_item);
		internaladapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sInternal.setAdapter(internaladapter);
		//set the option based on the sharedprefs
		sInternal.setSelection(InternalPrefValue, true);
		sInternal.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
		    	SharedPreferences.Editor editor = sharedPreferences.edit();
      			editor.putInt("InternalPref", position);
      			editor.commit(); 
				Log.d("internaladapter","internaladapter: position=" + position + " id=" + id);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				Log.d("internaladapter","internaladapter: Nothing selected");
			}
		});
		
		

		
		// Dropdown menu for I/O Scheduler External
		Spinner sExternal = (Spinner) findViewById(R.id.externalspinner);
		
		ArrayAdapter<CharSequence> externaladapter = ArrayAdapter
				.createFromResource(this, R.array.ioExternal,
						android.R.layout.simple_spinner_item);
		externaladapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sExternal.setAdapter(externaladapter);
		//set the option based on the sharedprefs
		sExternal.setSelection(ExternalPrefValue, true);
		sExternal.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
								
		    	SharedPreferences.Editor editor = sharedPreferences.edit();
      			editor.putInt("ExternalPref", position);
      			editor.commit(); 
				Log.d("externaladapter","externaladapter: position=" + position + " id=" + id);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				Log.d("externaladapter","externaladapter: Nothing selected");
			}
		});

	}

}
