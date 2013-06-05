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
import nl.dreamkernel.s4.tweaker.cpu.CpuTweaks;
import android.content.Context;
import android.util.Log;
import android.view.View;

public class OptionsHider {
	private static final String TAG = "S4Tweaker";


	// Methods used for hiding uncompatible options

	// Hiding method for Cpu Tweaks
	public static void CpuTweaksHider(Context context) {
		Log.d(TAG, "OptionsHider() cpuGovernor_hide = "
				+ FileCheck.cpuGovernor_hide);
		if (FileCheck.cpuGovernor_hide == 1) {
			CpuTweaks.Touch_block_governor.setVisibility(View.GONE);
			CpuTweaks.CpuCurrentValue.setVisibility(View.GONE);
			CpuTweaks.textuncompatibel.setText(R.string.disabled_option_text);
		}
		Log.d(TAG, "OptionsHider() cpuMinFreq_hide = "
				+ FileCheck.cpuMinFreq_hide);
		if (FileCheck.cpuMinFreq_hide == 1) {
			CpuTweaks.Touch_block_min_freq.setVisibility(View.GONE);
			CpuTweaks.CpuMinFREQValue.setVisibility(View.GONE);
			CpuTweaks.textuncompatibel2.setText(R.string.disabled_option_text);
		}
		Log.d(TAG, "OptionsHider() cpuMaxFreq_hide = "
				+ FileCheck.cpuMaxFreq_hide);
		if (FileCheck.cpuMaxFreq_hide == 1) {
			CpuTweaks.Touch_block_max_freq.setVisibility(View.GONE);
			CpuTweaks.CpuMaxFREQValue.setVisibility(View.GONE);
			CpuTweaks.textuncompatibel3.setText(R.string.disabled_option_text);
		}
	}

	

}
