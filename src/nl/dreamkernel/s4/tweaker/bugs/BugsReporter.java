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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import nl.dreamkernel.s4.tweaker.util.HttpRequest;
import nl.dreamkernel.s4.tweaker.R;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

	public static boolean http_Connectivity;
	public static boolean bugrecieved;

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

	public boolean isConnectable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		Log.d(TAG, "cm.getActiveNetworkInfo() " + cm.getActiveNetworkInfo());
		if (netInfo != null && netInfo.isConnected()) {
			Log.d(TAG, "netInfo.isConnected() " + netInfo.isConnected());
			try {
				URL url = new URL("http://www.google.com");
				HttpURLConnection urlc = (HttpURLConnection) url
						.openConnection();
				urlc.setConnectTimeout(3000);
				urlc.connect();
				if (urlc.getResponseCode() == 200) {
					Log.d(TAG,
							"urlc.getResponseCode() = "
									+ urlc.getResponseCode());
					http_Connectivity = true;
					return true;
				}
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		http_Connectivity = false;
		return false;
	}

	public void onSubmit(View view) {

		outputName = editName.getText().toString();
		outputEmail = editEmail.getText().toString();
		outputMsg = editMsg.getText().toString();
		http_Connectivity = false;
		Thread e = new Thread(new Runnable() {
			@Override
			public void run() {
				isConnectable();
				Log.d(TAG, "isConnectable = " + isConnectable());
			}
		});
		e.start();

		int whilecount = 0;
		while (whilecount < 10) {
			whilecount = whilecount + 1;
			if (whilecount == 10 && http_Connectivity == true) {
				Toast.makeText(BugsReporter.this, "Sending Bugreport",
						Toast.LENGTH_LONG).show();
				Log.d(TAG, "whilecount == 10 && http_Connectivity == true");
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						postData();

					}
				});
				t.start();
			}
			if (whilecount == 9 && http_Connectivity == false) {
				Toast.makeText(BugsReporter.this, "Failed to send Bug Report",
						Toast.LENGTH_LONG).show();
				Toast.makeText(BugsReporter.this,
						"No response from: google.com", Toast.LENGTH_LONG)
						.show();
				Toast.makeText(BugsReporter.this,
						"Make sure you are connected\n and try again",
						Toast.LENGTH_LONG).show();
				Log.d(TAG, "else http_Connectivity === " + http_Connectivity);
			}
			if (whilecount < 9 && http_Connectivity == true) {
				whilecount = 9;
			}
			Log.d(TAG, "http_Connectivity === " + http_Connectivity);
			Log.d(TAG, "http_Connectivity whilecount === " + whilecount);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void postData() {

		try {
			if (http_Connectivity == true) {

				String fullUrl = "https://docs.google.com/forms/d/16CXKEV2XOZK8xLNcW8SERhh9eN2aQyUYmDIlk7lW5ik/formResponse";
				HttpRequest mReq = new HttpRequest();
				String data1 = outputName;
				String data2 = outputEmail;
				String data3 = outputMsg;

				@SuppressWarnings("deprecation")
				String data = "entry_1845673598=" + URLEncoder.encode(data1)
						+ "&" + "entry_855344491=" + URLEncoder.encode(data2)
						+ "&" + "entry_1481820931=" + URLEncoder.encode(data3);
				String response = mReq.sendPost(fullUrl, data);
				bugrecieved = true;
				Log.i(TAG, data);
				finish();
			} else {
				Log.d(TAG, "+++++ Can't Connect +++++ ");
			}
		} catch (Exception e) {
			Log.i(TAG, "" + e);
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		bugrecieved = false;
	}

}