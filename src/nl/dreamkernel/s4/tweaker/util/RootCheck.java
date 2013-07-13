/*
 *  Copyright 2013-2014 Talustus & Jeroen Gorter <Lowerland@hotmail.com>
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

import java.io.DataInputStream;
import java.io.DataOutputStream;

import android.util.Log;

public class RootCheck {

	private static final String TAG = "S4Tweaker";
	private Process mProcess = null;
	private DataOutputStream mOutputStream = null;
	private DataInputStream mInputStream = null;
	public static boolean exitSu;
	public static boolean retval;

	public boolean init() {
		try {
			mProcess = Runtime.getRuntime().exec("su");
			mOutputStream = new DataOutputStream(mProcess.getOutputStream());
			mInputStream = new DataInputStream(mProcess.getInputStream());
			retval = false;
			exitSu = false;
			
			if (null != mOutputStream && null != mInputStream) {
				// Getting the id of the current user to check if this is root
				mOutputStream.writeBytes("id\n");
				mOutputStream.flush();

				String Uid = mInputStream.readLine();

				if (null == Uid) {
					retval = false;
					exitSu = false;
					Log.d(TAG, "ROOT, Can't get root access or denied by user");
				} else if (true == Uid.contains("uid=0")) {
					retval = true;
					exitSu = true;
					Log.d(TAG, "ROOT, Root access granted for: " + Uid);
				} else {
					retval = false;
					exitSu = true;
					Log.d(TAG, "ROOT Root access rejected: " + Uid);
				}

				if (exitSu) {
					mOutputStream.writeBytes("exit\n");
					mOutputStream.flush();
				}
			}
		} catch (Exception e) {
			// no root ... !
			// Probably broken pipe exception on trying to write to
			// mOutputStream
			// after su failed, meaning that the device is not rooted
			retval = false;
			Log.d(TAG, "ROOT Root access rejected [" + e.getClass().getName()
					+ "] : " + e.getMessage());
		}
		return retval;
	}
}
