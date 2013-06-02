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
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DialogActivity extends Activity implements OnCheckedChangeListener {
	static final String TAG = "S4Tweaker";
	
	public static int hidedialog;
	private CheckBox checkBoxHIDEALERT;
	
	private Button button_ok;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_alert);
		
		hidedialog = 0;
		checkBoxHIDEALERT = (CheckBox)findViewById(R.id.checkBoxHIDEALERT);
		checkBoxHIDEALERT.setOnCheckedChangeListener(this);
		
		button_ok = (Button)findViewById(R.id.btn_dialog_ok);
		button_ok.setOnClickListener(buttonDialogListener);
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				
				Log.d(TAG,"isChecked");
				hidedialog = 1;
				//setResult(RESULT_OK, (new Intent()).setAction("checkbox Corky!"));
	            //finish();
				/*hide_dialog = 1;
				SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", 0);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putInt("hide_dialog", hide_dialog);
				editor.commit();*/ 
				
				//Log.d(TAG,"Editor "+hide_dialog);
			} else {
				
				//setResult(RESULT_OK, (new Intent()).setAction("checkbox false"));
				hidedialog = 0;
				/*hide_dialog = 0;
				Log.d(TAG,"isunChecked"+hide_dialog);*/
			}
		
	}
	
	
	/*@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
		Log.d(TAG,"onDialogOK pressed");
	}*/
    private OnClickListener buttonDialogListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            // To send a result, simply call setResult() before your
            // activity is finished.
            setResult(RESULT_OK, (new Intent()).setAction(""+hidedialog));
            finish();
        }
    };

    /*private OnClickListener mVioletListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            // To send a result, simply call setResult() before your
            // activity is finished.
            setResult(RESULT_OK, (new Intent()).setAction("Violet!"));
            finish();
        }
    };*/
}
