/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mutablestate.readonline.domain.textprocessor.ocrinternals;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mutablestate.readonline.R;

/** Utility class to retrieve shared preferences. */
public class PreferenceUtils {

  public static boolean shouldHideDetectionInfo(Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String prefKey = context.getString(R.string.pref_key_info_hide);
    return sharedPreferences.getBoolean(prefKey, false);
  }

  public static boolean shouldGroupRecognizedTextInBlocks(Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String prefKey = context.getString(R.string.pref_key_group_recognized_text_in_blocks);
    return sharedPreferences.getBoolean(prefKey, false);
  }

  public static boolean showLanguageTag(Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String prefKey = context.getString(R.string.pref_key_show_language_tag);
    return sharedPreferences.getBoolean(prefKey, false);
  }

  public static boolean isCameraLiveViewportEnabled(Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String prefKey = context.getString(R.string.pref_key_camera_live_viewport);
    return sharedPreferences.getBoolean(prefKey, false);
  }

  private PreferenceUtils() {}
}
