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

package nl.dreamkernel.s4.tweaker.bugs;

import java.net.URLEncoder;

import nl.dreamkernel.s4.tweaker.util.HttpRequest;
import nl.dreamkernel.s4.tweaker.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class BugsReporter extends Activity {
	static final String TAG = "S4Tweaker";

	public static EditText editName;
	public static EditText editEmail;
	public static EditText editMsg;

	public static String outputName;
	public static String outputEmail;
	public static String outputMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bug_report);
		setTitle(R.string.menu_report_bug);
		getActionBar().hide();

		editName = (EditText) findViewById(R.id.editName);
		editEmail = (EditText) findViewById(R.id.editEmail);
		editMsg = (EditText) findViewById(R.id.editMsg);
	}

	public void onSubmit(View view) {

		outputName = editName.getText().toString();
		outputEmail = editEmail.getText().toString();
		outputMsg = editMsg.getText().toString();

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				postData();

			}
		});
		t.start();
		
		
	}

	public void postData() {

		try {
			String fullUrl = "https://docs.google.com/forms/d/16CXKEV2XOZK8xLNcW8SERhh9eN2aQyUYmDIlk7lW5ik/formResponse";
			HttpRequest mReq = new HttpRequest();
			String data1 = outputName;
			String data2 = outputEmail;
			String data3 = outputMsg;

			@SuppressWarnings("deprecation")
			String data = "entry_1845673598=" + URLEncoder.encode(data1) + "&"
					+ "entry_855344491=" + URLEncoder.encode(data2) + "&"
					+ "entry_1481820931=" + URLEncoder.encode(data3);
			String response = mReq.sendPost(fullUrl, data);
			Log.i(TAG, response);
			Toast.makeText(BugsReporter.this, "BugReport Sended", Toast.LENGTH_LONG)
			.show();
			finish();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "" +e);
			Toast.makeText(BugsReporter.this, "BugReport Failed", Toast.LENGTH_LONG)
			.show();
			finish();
			//e.printStackTrace();
		}
	}

}
