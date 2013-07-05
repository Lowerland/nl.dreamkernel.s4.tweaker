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

package nl.dreamkernel.s4.tweaker.systeminfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.dreamkernel.s4.tweaker.R;
import nl.dreamkernel.s4.tweaker.cpu.CpuTweaks;
import nl.dreamkernel.s4.tweaker.util.RootProcess;
import nl.dreamkernel.s4.tweaker.util.SysCmds;
import nl.dreamkernel.s4.tweaker.util.SysFs;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class SysInfo extends Activity {
	static final String TAG = "S4Tweaker";

	public static TextView Kernel_Version;

	// Cpu Info
	public static TextView Processor;
	public static TextView BogoMIPS;
	public static TextView Features;
	public static TextView CPU_implementer;
	public static TextView CPU_architecture;
	public static TextView CPU_variant;
	public static TextView CPU_part;
	public static TextView CPU_revision;
	public static TextView Hardware;
	public static TextView Revision;
	public static TextView Serial;

	public static TextView cpu0_status;
	public static TextView cpu1_status;
	public static TextView cpu2_status;
	public static TextView cpu3_status;

	public static TextView cpu0_frequency;
	public static TextView cpu1_frequency;
	public static TextView cpu2_frequency;
	public static TextView cpu3_frequency;

	public static TextView kernel_cmdline;

	public static final SysFs vCheck_Kernel_CMDLine = new SysFs("/proc/cmdline");
	// public static final SysFs vCheck_Kernel_CMDLine = new
	// SysFs("/mnt/sdcard/testfiles/cmdline");

	public static String Kernel_CMDLine_temp;
	public static String Kernel_CMDLine;
	public static final String NoInfo = "No Info";

	public static int i;

	// Intent service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.systeminfo);
		setTitle(R.string.menu_system_info);
		getActionBar().hide();

		// Kernel Version
		Kernel_Version = (TextView) findViewById(R.id.kernel_version);

		// Cpu Info
		Processor = (TextView) findViewById(R.id.Processor);
		BogoMIPS = (TextView) findViewById(R.id.BogoMIPS);
		Features = (TextView) findViewById(R.id.Features);
		CPU_implementer = (TextView) findViewById(R.id.CPU_implementer);
		CPU_architecture = (TextView) findViewById(R.id.CPU_architecture);
		CPU_variant = (TextView) findViewById(R.id.CPU_variant);
		CPU_part = (TextView) findViewById(R.id.CPU_part);
		CPU_revision = (TextView) findViewById(R.id.CPU_revision);
		Hardware = (TextView) findViewById(R.id.Hardware);
		Revision = (TextView) findViewById(R.id.Revision);
		Serial = (TextView) findViewById(R.id.Serial);

		cpu0_status = (TextView) findViewById(R.id.cpu0_status);
		cpu1_status = (TextView) findViewById(R.id.cpu1_status);
		cpu2_status = (TextView) findViewById(R.id.cpu2_status);
		cpu3_status = (TextView) findViewById(R.id.cpu3_status);

		cpu0_frequency = (TextView) findViewById(R.id.cpu0_frequency);
		cpu1_frequency = (TextView) findViewById(R.id.cpu1_frequency);
		cpu2_frequency = (TextView) findViewById(R.id.cpu2_frequency);
		cpu3_frequency = (TextView) findViewById(R.id.cpu3_frequency);

		// Kernel Commandline
		kernel_cmdline = (TextView) findViewById(R.id.kernel_cmdline);

		Processor.setText(SysCmds.CPUinfo("Processor"));
		BogoMIPS.setText(SysCmds.CPUinfo("BogoMIPS"));
		Features.setText(SysCmds.CPUinfo("Features"));
		CPU_implementer.setText(SysCmds.CPUinfo("CPU implementer"));
		CPU_architecture.setText(SysCmds.CPUinfo("CPU architecture"));
		CPU_variant.setText(SysCmds.CPUinfo("CPU variant"));
		CPU_part.setText(SysCmds.CPUinfo("CPU part"));
		CPU_revision.setText(SysCmds.CPUinfo("CPU revision"));
		Hardware.setText(SysCmds.CPUinfo("Hardware"));
		Revision.setText(SysCmds.CPUinfo("Revision"));
		Serial.setText(SysCmds.CPUinfo("Serial"));

		Kernel_Version.setText(getKernelVersion());
		kernel_cmdline.setText(getKernelcmdline() + "\n");

	}

	public static String kv;

	public static String getKernelVersion() {
		String ret = SysCmds.kernelversion("");
		if (SysInfo.isNullOfEmpty(ret)) {
			return NoInfo;
		} else {
			if (ret.indexOf(' ') > -1) {
				Pattern p = Pattern.compile("^((.*))");
				Matcher m = p.matcher(ret);
				if (m.find()) {
					kv = m.toMatchResult().group(0);
					kv = kv.replace(") )", " ").replace(") (", "\n")
							.replace("(", "").replace(" #1 ", "\n#1 ");
					// .replace(") (", "\n").replace("GCC", "");
					return kv;
				}
			} else {
				return NoInfo;
			}
		}
		return ret;
	}

	public static String kc;
	public static String ret2;

	public static String getKernelcmdline() {

		if (vCheck_Kernel_CMDLine.exists()) {
			RootProcess rootProcess = new RootProcess();
			rootProcess.init();
			ret2 = vCheck_Kernel_CMDLine.read(rootProcess);
			rootProcess.term();
			if (SysInfo.isNullOfEmpty(ret2)) {
				return NoInfo;
			} else {
				if (ret2.indexOf(" ") > -1) {
					Pattern o = Pattern.compile("^((.*))");
					Matcher h = o.matcher(ret2);
					if (h.find()) {
						kc = h.toMatchResult().group(0);
						kc = kc.replace(" ", "\n");
						return kc;
					}
				}
			}
		} else {
			return NoInfo;
		}
		return ret2;
	}

	public static boolean isNullOfEmpty(String value) {
		return (value == null || "".equals(value));
	}

	private static int file_CPU0_ONLINE;
	private static int file_CPU1_ONLINE;
	private static int file_CPU2_ONLINE;
	private static int file_CPU3_ONLINE;

	public static void Cpu_Status() {
		// calls RootProcess

		RootProcess process = new RootProcess();
		if (!process.init()) {
			return;
		}
		if (CpuTweaks.vCheck_CPU0_ONLINE.exists()) {
			int file_CPU0_ONLINE_temp = Integer
					.parseInt(CpuTweaks.vCheck_CPU0_ONLINE.read(process));
			file_CPU0_ONLINE = file_CPU0_ONLINE_temp;
			Log.d(TAG, "Read Cpu 0 State ");
		} else {
			cpu0_status.setText("Offline");
			cpu0_status.setTextColor(Color.parseColor("#f27474"));
			cpu0_frequency.setText("");
		}
		if (CpuTweaks.vCheck_CPU1_ONLINE.exists()) {
			int file_CPU1_ONLINE_temp = Integer
					.parseInt(CpuTweaks.vCheck_CPU1_ONLINE.read(process));
			file_CPU1_ONLINE = file_CPU1_ONLINE_temp;
			Log.d(TAG, "Read Cpu 1 State ");
		} else {
			cpu1_status.setText("Offline");
			cpu1_status.setTextColor(Color.parseColor("#f27474"));
			cpu1_frequency.setText("");
		}
		if (CpuTweaks.vCheck_CPU2_ONLINE.exists()) {
			int file_CPU2_ONLINE_temp = Integer
					.parseInt(CpuTweaks.vCheck_CPU2_ONLINE.read(process));
			file_CPU2_ONLINE = file_CPU2_ONLINE_temp;
			Log.d(TAG, "Read Cpu 2 State ");
		} else {
			cpu2_status.setText("Offline");
			cpu2_status.setTextColor(Color.parseColor("#f27474"));
			cpu2_frequency.setText("");
		}
		if (CpuTweaks.vCheck_CPU3_ONLINE.exists()) {
			int file_CPU3_ONLINE_temp = Integer
					.parseInt(CpuTweaks.vCheck_CPU3_ONLINE.read(process));
			file_CPU3_ONLINE = file_CPU3_ONLINE_temp;
			Log.d(TAG, "Read Cpu 3 State ");
		} else {
			cpu3_status.setText("Offline");
			cpu3_status.setTextColor(Color.parseColor("#f27474"));
			cpu3_frequency.setText("");
		}
		if (file_CPU0_ONLINE == 0) {
			cpu0_status.setText("Offline");
			cpu0_status.setTextColor(Color.parseColor("#f27474"));
			cpu0_frequency.setText("");
			Log.d(TAG, "Set text cpu 0 offline");
		} else {
			cpu0_status.setText("Online");
			Log.d(TAG, "CPU 0is ONLINE ");
			if (CpuTweaks.vCheck_CPU0_CUR_FREQ.exists()) {
				int file_CPU0_CUR_FREQ = Integer
						.parseInt(CpuTweaks.vCheck_CPU0_CUR_FREQ.read(process));
				cpu0_frequency.setText("" + file_CPU0_CUR_FREQ);
			}
		}
		if (file_CPU1_ONLINE == 0) {
			cpu1_status.setText("Offline");
			cpu1_status.setTextColor(Color.parseColor("#f27474"));
			cpu1_frequency.setText("");
			Log.d(TAG, "Set text cpu 1 offline");
		} else {
			cpu1_status.setText("Online");
			Log.d(TAG, "CPU 1 is ONLINE ");
			if (CpuTweaks.vCheck_CPU1_CUR_FREQ.exists()) {
				int file_CPU1_CUR_FREQ = Integer
						.parseInt(CpuTweaks.vCheck_CPU1_CUR_FREQ.read(process));
				cpu1_frequency.setText("" + file_CPU1_CUR_FREQ);
			}
		}
		if (file_CPU2_ONLINE == 0) {
			cpu2_status.setText("Offline");
			cpu2_status.setTextColor(Color.parseColor("#f27474"));
			cpu2_frequency.setText("");
			Log.d(TAG, "Set text cpu 2 offline");
		} else {
			cpu2_status.setText("Online");
			Log.d(TAG, "CPU 2 is ONLINE ");
			if (CpuTweaks.vCheck_CPU2_CUR_FREQ.exists()) {
				int file_CPU2_CUR_FREQ = Integer
						.parseInt(CpuTweaks.vCheck_CPU2_CUR_FREQ.read(process));
				cpu2_frequency.setText("" + file_CPU2_CUR_FREQ);
			}
		}
		if (file_CPU3_ONLINE == 0) {
			cpu3_status.setText("Offline");
			cpu3_status.setTextColor(Color.parseColor("#f27474"));
			cpu3_frequency.setText("");
			Log.d(TAG, "Set text cpu 3 offline");
		} else {
			cpu3_status.setText("Online");
			Log.d(TAG, "CPU 3 is ONLINE ");
			if (CpuTweaks.vCheck_CPU3_CUR_FREQ.exists()) {
				int file_CPU3_CUR_FREQ = Integer
						.parseInt(CpuTweaks.vCheck_CPU3_CUR_FREQ.read(process));
				cpu3_frequency.setText("" + file_CPU3_CUR_FREQ);
			}
		}

		process.term();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// inside onCreate:
		// If you need a timed UI update, create a Runnable with code inside of
		// run(),
		// instantiate a Handler as a member variable of your Activity,
		// and call post(), postDelayed(), or postAtTime() on the Handler
		// object,
		// passing the runnable as a parameter value.

		final Handler handler = new Handler();
		final Runnable updater = new Runnable() {
			public void run() {
				Log.d(TAG,
						"new Handler() final Runnable updater = new Runnable() ");
				Cpu_Status();
			}
		};
		Thread x = new Thread() {
			public void run() {
				i = 0;
				while (i < 5) {

					try {

						handler.postDelayed(updater, 1000);

						Log.d(TAG, "handler.postDelayed(updater) i= " + i);
						Thread.sleep(5000); // sleep 5 seconds
						handler.removeCallbacksAndMessages(updater);
						// handler.removeCallbacks(updater);

					} catch (Exception e) {
						Log.e(TAG, "local Thread error", e);
					}
					i--;

				}
			}
		};
		x.start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// set int for ending the cpu stats loop
		i = 10;

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// set int for ending the cpu stats loop
		// i=10;

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// set int for ending the cpu stats loop
		i = 10;
	}

	public static void sleep(long msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			Log.e(TAG, "Exception", e);
			e.printStackTrace();
		}
	}

}
