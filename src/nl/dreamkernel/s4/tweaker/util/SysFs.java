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

import nl.dreamkernel.s4.tweaker.util.RootProcess;
import nl.dreamkernel.s4.tweaker.util.RuntimeExec;

public class SysFs {
	static final String TAG = "S4Tweaker";

	private File mFile;
	private String mPermission;

	public SysFs(String path) {

		this(path, null);
	}

	public SysFs(String path, String permission) {
		mFile = new File(path);
		mPermission = permission;
	}

	public boolean exists() {
		return mFile.exists();
	}

	public String read(RootProcess rootProcess) {
		// Log.d(TAG, "*********** 48: String Read");
		String[] values = readMuitiLine(rootProcess);
		if (values != null && values.length > 0) {
			// Log.d(TAG, "*********** 51: if  != null && values.length > 0");
			return values[0];
		}
		// Log.d(TAG, "*********** 54: return null");
		return null;
	}

	public String[] readMuitiLine(RootProcess rootProcess) {
		if (!mFile.exists()) {
			// Log.d(TAG, "*********** 60: if !mFile.exists()");
			return null;
		}
		String command = "cat " + mFile.getPath() + "\n";
		// Log.d(TAG, "*********** 64: String command= "+command);
		if (!mFile.canRead()) {
			// Log.d(TAG, "*********** 66: if !mFile.canRead()");
			RootProcess process;
			if (rootProcess == null) {
				// Log.d(TAG, "*********** 69: if (rootProcess == null)");
				process = new RootProcess();
				process.init();
			} else {
				// Log.d(TAG,
				// "*********** 73: } ELSE { process = rootProcess;");
				process = rootProcess;
			}

			// TODO Doesnt work like it should needs fixing
			if (mPermission != null) {
				// Log.d(TAG,
				// "*********** 78: if (mPermission != null) "+mPermission);
				process.write("chmod " + mPermission + " " + mFile.getPath()
						+ "\n");
			}
			// Log.d(TAG, "*********** 81: process.write(command)");
			process.write(command);
			String[] ret = process.read();
			// Log.d(TAG, "*********** 84: ret = process.read()"+ret);
			if (rootProcess == null) {
				// Log.d(TAG, "*********** 86: process.term()");
				process.term();
			}
			// Log.d(TAG, "*********** 89: return ret;");
			return ret;
		} else {
			// Log.d(TAG,
			// "*********** 92: return RuntimeExec.execute(command, true);");
			return RuntimeExec.execute(command, true);
		}
	}

	public void write(String data, RootProcess rootProcess) {
		if (!mFile.exists()) {
			return;
		}
		String command = "echo " + data + " > " + mFile.getPath() + "\n";
		if (!mFile.canWrite()) {
			RootProcess process;
			if (rootProcess == null) {
				process = new RootProcess();
				process.init();
			} else {
				process = rootProcess;
			}

			if (mPermission != null) {
				process.write("chmod " + mPermission + " " + mFile.getPath()
						+ "\n");
			}
			process.write(command);

			if (rootProcess == null) {
				process.term();
			}
		} else {
			RuntimeExec.execute(command, false);
		}
	}

	public String getPath() {
		return mFile.getPath();
	}
}
