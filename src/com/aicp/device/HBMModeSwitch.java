/*
* Copyright (C) 2016 The OmniROM Project
* Copyright (C) 2021 The Android Ice Cold Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package com.aicp.device;

import android.content.Context;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import java.lang.Integer;

public class HBMModeSwitch implements OnPreferenceChangeListener {

    public static final String SETTINGS_KEY = DeviceSettings.KEY_SETTINGS_PREFIX + DeviceSettings.KEY_HBM_SWITCH;

    private Context mContext;
    private String mHBMOffState;
    private String mHBMOnState;

    public HBMModeSwitch(Context context) {
        mContext = context;
        mHBMOffState = context.getResources().getString(R.string.hbmOFF);
        mHBMOnState = context.getResources().getString(R.string.hbmON);
    }

    public static String getFile(Context context) {
        String fileName = context.getResources().getString(R.string.pathHBMModeToggle);
        if (fileName != null && !fileName.isEmpty() && Utils.fileWritable(fileName)) {
            return fileName;
        }
        return null;
    }

    public static boolean isSupported(Context context) {
        return getFile(context) != null;
    }

    public static boolean isCurrentlyEnabled(Context context) {
        return Utils.getFileValueAsBooleanHBM(getFile(context), false);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        Settings.System.putInt(mContext.getContentResolver(), SETTINGS_KEY, enabled ? Integer.parseInt(mHBMOnState) : Integer.parseInt(mHBMOffState));
        Utils.writeValue(getFile(mContext), enabled ? mHBMOnState : mHBMOffState);
        return true;
    }
}
