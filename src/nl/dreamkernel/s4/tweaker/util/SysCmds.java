/*
 * Copyright (C) 2011-2012 sakuramilk <c.sakuramilk@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.dreamkernel.s4.tweaker.util;

public class SysCmds {
	static final String TAG = "S4Tweaker";

	public static String kernelversion(String option) {
		String[] ret = RuntimeExec.execute("cat /proc/version\n", true);
		return ret[0];
	}

	public static String uname(String option) {
		String[] ret = RuntimeExec.execute("uname " + option + "\n", true);
		return ret[0];
	}

	public static String CPUinfo(String option) {
		String[] ret = RuntimeExec.execute("cat /proc/cpuinfo | grep \""
				+ option + "\"\n", true);
		return ret[0];
	}
}
