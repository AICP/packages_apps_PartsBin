/*
* Copyright (C) 2016 The OmniROM Project
* Copyright (C) 2020 The Android Ice Cold Project
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

import static android.provider.Settings.System.MIN_REFRESH_RATE;
import static android.provider.Settings.System.PEAK_REFRESH_RATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import androidx.preference.SwitchPreference;

import com.aicp.gear.preference.SelfRemovingPreference;
import com.aicp.gear.preference.SelfRemovingPreferenceCategory;
import com.android.internal.util.aicp.PackageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeviceSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "AicpDeviceSettingsFragment";

    public static final String GESTURE_HAPTIC_SETTINGS_VARIABLE_NAME = "OFF_GESTURE_HAPTIC_ENABLE";
    public static final String GESTURE_MUSIC_PLAYBACK_SETTINGS_VARIABLE_NAME = "MUSIC_PLAYBACK_GESTURE_ENABLE";

    public static final String KEY_SYSTEM_VIBSTRENGTH = "vib_system_strength";
    public static final String KEY_CALL_VIBSTRENGTH = "vib_call_strength";
    public static final String KEY_NOTIF_VIBSTRENGTH = "vib_notif_strength";

    private static final String KEY_SLIDER_MODE_TOP = "slider_mode_top";
    private static final String KEY_SLIDER_MODE_CENTER = "slider_mode_center";
    private static final String KEY_SLIDER_MODE_BOTTOM = "slider_mode_bottom";

    private static final String KEY_AUDIO_CATEGORY = "category_audio";
    private static final String KEY_BUTTON_CATEGORY = "category_buttons";
    private static final String KEY_GRAPHICS_CATEGORY = "category_graphics";
    private static final String KEY_REFRESH_CATEGORY = "category_refresh";
    private static final String KEY_VIBRATOR_CATEGORY = "category_vibrator";
    private static final String KEY_SLIDER_CATEGORY = "category_slider";
    private static final String KEY_GESTURES_CATEGORY = "category_gestures";
    private static final String KEY_POWER_CATEGORY = "category_power";
    private static final String KEY_AUDIOGAINS_CATEGORY = "category_audiogains";

    public static final String KEY_HEADPHONE_GAIN = "headphone_gain";
    public static final String KEY_EARPIECE_GAIN = "earpiece_gain";
    public static final String KEY_MIC_GAIN = "mic_gain";
    public static final String KEY_SPEAKER_GAIN = "speaker_gain";

    public static final String KEY_SRGB_SWITCH = "srgb";
    public static final String KEY_ADOBERGB_SWITCH = "adobe_rgb";
    public static final String KEY_HBM_SWITCH = "hbm";
    public static final String KEY_PROXI_SWITCH = "proxi";
    public static final String KEY_DCD_SWITCH = "dcd";
    public static final String KEY_DCI_SWITCH = "dci";
    public static final String KEY_WIDE_SWITCH = "wide";
    public static final String KEY_NIGHT_SWITCH = "night";
    public static final String KEY_ONEPLUSMODE_SWITCH = "oneplus";

    public static final String KEY_HWK_SWITCH = "hwk";
    public static final String KEY_STAP_SWITCH = "single_tap";
    public static final String KEY_DT2W_SWITCH = "double_tap_to_wake";
    public static final String KEY_S2S_SWITCH = "sweep_to_sleep";
    public static final String KEY_S2W_SWITCH = "sweep_to_wake";
    public static final String KEY_FASTCHARGE_SWITCH = "fastcharge";
    private static final String KEY_PEAK_REFRESH_RATE = "peakrefreshrate";
    private static final String KEY_MIN_REFRESH_RATE = "minrefreshrate";
    private static final String KEY_ENABLE_DOLBY_ATMOS = "enable_dolby_atmos";
    public static final String KEY_OFFSCREEN_GESTURES = "gesture_category";
    public static final String KEY_PANEL_SETTINGS = "panel_category";
    public static final String SLIDER_DEFAULT_VALUE = "2,1,0";

    public static final String KEY_SETTINGS_PREFIX = "device_setting_";

    private VibratorSystemStrengthPreference mVibratorSystemStrength;
    private VibratorCallStrengthPreference mVibratorCallStrength;
    private VibratorNotifStrengthPreference mVibratorNotifStrength;

    private EarpieceGainPreference mEarpieceGainPref;
    private HeadphoneGainPreference mHeadphoneGainPref;
    private MicGainPreference mMicGainPref;
    private SpeakerGainPreference mSpeakerGainPref;

    private ListPreference mSliderModeTop;
    private ListPreference mSliderModeCenter;
    private ListPreference mSliderModeBottom;
    private ListPreference mPeakRefreshRatePref;
    private ListPreference mMinRefreshRatePref;
    private Preference mOffScreenGestures;
    private Preference mPanelSettings;
    private static TwoStatePreference mHBMModeSwitch;
    private static TwoStatePreference mDCDModeSwitch;
    private static TwoStatePreference mHWKSwitch;
    private static TwoStatePreference mSTapSwitch;
    private static TwoStatePreference mFastChargeSwitch;
    private static TwoStatePreference mDoubleTapToWakeSwitch;
    private static TwoStatePreference mSweepToSleepSwitch;
    private static TwoStatePreference mSweepToWakeSwitch;
    private SwitchPreference mEnableDolbyAtmos;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main, rootKey);

        final Resources res = getContext().getResources();
        boolean supportsGestures = res.getBoolean(R.bool.config_device_supports_gestures);
        boolean supportsPanels = res.getBoolean(R.bool.config_device_supports_panels);

        SelfRemovingPreferenceCategory sliderCategory = (SelfRemovingPreferenceCategory) findPreference(KEY_SLIDER_CATEGORY);
        if (sliderCategory != null) {
            mSliderModeTop = (ListPreference) findPreference(KEY_SLIDER_MODE_TOP);
            mSliderModeTop.setOnPreferenceChangeListener(this);
            int sliderModeTop = getSliderAction(0);
            int valueIndex = mSliderModeTop.findIndexOfValue(String.valueOf(sliderModeTop));
            mSliderModeTop.setValueIndex(valueIndex);
            mSliderModeTop.setSummary(mSliderModeTop.getEntries()[valueIndex]);

            mSliderModeCenter = (ListPreference) findPreference(KEY_SLIDER_MODE_CENTER);
            mSliderModeCenter.setOnPreferenceChangeListener(this);
            int sliderModeCenter = getSliderAction(1);
            valueIndex = mSliderModeCenter.findIndexOfValue(String.valueOf(sliderModeCenter));
            mSliderModeCenter.setValueIndex(valueIndex);
            mSliderModeCenter.setSummary(mSliderModeCenter.getEntries()[valueIndex]);

            mSliderModeBottom = (ListPreference) findPreference(KEY_SLIDER_MODE_BOTTOM);
            mSliderModeBottom.setOnPreferenceChangeListener(this);
            int sliderModeBottom = getSliderAction(2);
            valueIndex = mSliderModeBottom.findIndexOfValue(String.valueOf(sliderModeBottom));
            mSliderModeBottom.setValueIndex(valueIndex);
            mSliderModeBottom.setSummary(mSliderModeBottom.getEntries()[valueIndex]);
        }

        SelfRemovingPreferenceCategory soundCategory = (SelfRemovingPreferenceCategory) findPreference(KEY_AUDIO_CATEGORY);
        if (soundCategory != null) {
            mEnableDolbyAtmos = (SwitchPreference) findPreference(KEY_ENABLE_DOLBY_ATMOS);
            mEnableDolbyAtmos.setOnPreferenceChangeListener(this);
        }

        mHWKSwitch = (TwoStatePreference) findPreference(KEY_HWK_SWITCH);
        if (mHWKSwitch != null && HWKSwitch.isSupported()) {
            mHWKSwitch.setEnabled(true);
            mHWKSwitch.setChecked(HWKSwitch.isCurrentlyEnabled());
            mHWKSwitch.setOnPreferenceChangeListener(new HWKSwitch(getContext()));
        } else {
            PreferenceCategory buttonsCategory = (PreferenceCategory) findPreference(KEY_BUTTON_CATEGORY);
            buttonsCategory.getParent().removePreference(buttonsCategory);
        }

        PreferenceCategory gesturesCategory = (PreferenceCategory) findPreference(KEY_GESTURES_CATEGORY);
        int gesturesRemoved = 0;
        mSTapSwitch = (TwoStatePreference) findPreference(KEY_STAP_SWITCH);
        if (mSTapSwitch != null && SingleTapSwitch.isSupported(getContext())){
            mSTapSwitch.setEnabled(true);
            mSTapSwitch.setChecked(SingleTapSwitch.isCurrentlyEnabled(getContext()));
            mSTapSwitch.setOnPreferenceChangeListener(new SingleTapSwitch(getContext()));
        } else {
            gesturesCategory.removePreference(mSTapSwitch);
            gesturesRemoved += 1;
        }
        mDoubleTapToWakeSwitch = (TwoStatePreference) findPreference(KEY_DT2W_SWITCH);
        if (mDoubleTapToWakeSwitch != null && DoubleTapToWakeSwitch.isSupported(getContext())){
            mDoubleTapToWakeSwitch.setEnabled(true);
            mDoubleTapToWakeSwitch.setChecked(DoubleTapToWakeSwitch.isCurrentlyEnabled(getContext()));
            mDoubleTapToWakeSwitch.setOnPreferenceChangeListener(new DoubleTapToWakeSwitch(getContext()));
        } else {
            gesturesCategory.removePreference(mDoubleTapToWakeSwitch);
            gesturesRemoved += 1;
        }
        mSweepToSleepSwitch = (TwoStatePreference) findPreference(KEY_S2S_SWITCH);
        if (mSweepToSleepSwitch != null && SweepToSleepSwitch.isSupported(getContext())){
            mSweepToSleepSwitch.setEnabled(true);
            mSweepToSleepSwitch.setChecked(SweepToSleepSwitch.isCurrentlyEnabled(getContext()));
            mSweepToSleepSwitch.setOnPreferenceChangeListener(new SweepToSleepSwitch(getContext()));
        } else {
            gesturesCategory.removePreference(mSweepToSleepSwitch);
            gesturesRemoved += 1;
        }
        mSweepToWakeSwitch = (TwoStatePreference) findPreference(KEY_S2W_SWITCH);
        if (mSweepToWakeSwitch != null && SweepToWakeSwitch.isSupported(getContext())){
            mSweepToWakeSwitch.setEnabled(true);
            mSweepToWakeSwitch.setChecked(SweepToWakeSwitch.isCurrentlyEnabled(getContext()));
            mSweepToWakeSwitch.setOnPreferenceChangeListener(new SweepToWakeSwitch(getContext()));
        } else {
            gesturesCategory.removePreference(mSweepToWakeSwitch);
            gesturesRemoved += 1;
        }
        SelfRemovingPreference mOffScreenGestures = (SelfRemovingPreference) findPreference(KEY_OFFSCREEN_GESTURES);
        if (mOffScreenGestures == null) {
            gesturesRemoved += 1;
        }
        if (gesturesRemoved == 5) gesturesCategory.getParent().removePreference(gesturesCategory);

        PreferenceCategory graphicsCategory = (PreferenceCategory) findPreference(KEY_GRAPHICS_CATEGORY);
        mPanelSettings = (Preference) findPreference(KEY_PANEL_SETTINGS);
        int graphicsRemoved = 0;
        mHBMModeSwitch = (TwoStatePreference) findPreference(KEY_HBM_SWITCH);
        if (mHBMModeSwitch != null && HBMModeSwitch.isSupported(getContext())){
            mHBMModeSwitch.setEnabled(true);
            mHBMModeSwitch.setChecked(HBMModeSwitch.isCurrentlyEnabled(getContext()));
            mHBMModeSwitch.setOnPreferenceChangeListener(new HBMModeSwitch(getContext()));
        } else {
            graphicsCategory.removePreference(mHBMModeSwitch);
            graphicsRemoved += 1;
        }

        mDCDModeSwitch = (TwoStatePreference) findPreference(KEY_DCD_SWITCH);
        if (mDCDModeSwitch != null && DCDModeSwitch.isSupported(getContext())){
            mDCDModeSwitch.setEnabled(true);
            mDCDModeSwitch.setChecked(DCDModeSwitch.isCurrentlyEnabled(getContext()));
            mDCDModeSwitch.setOnPreferenceChangeListener(new DCDModeSwitch(getContext()));
        } else {
            graphicsCategory.removePreference(mDCDModeSwitch);
            graphicsRemoved += 1;
        }
        if (!supportsPanels) {
            mPanelSettings.getParent().removePreference(mPanelSettings);
            graphicsRemoved += 1;
        }
        if (graphicsRemoved == 3) graphicsCategory.getParent().removePreference(graphicsCategory);

        SelfRemovingPreferenceCategory refreshCategory = (SelfRemovingPreferenceCategory) findPreference(KEY_REFRESH_CATEGORY);
        if (refreshCategory != null) {
            mPeakRefreshRatePref = findPreference(KEY_PEAK_REFRESH_RATE);
            initRefreshRatePreference(mPeakRefreshRatePref, PEAK_REFRESH_RATE);
            mMinRefreshRatePref = findPreference(KEY_MIN_REFRESH_RATE);
            initRefreshRatePreference(mMinRefreshRatePref, MIN_REFRESH_RATE);
        }

        PreferenceCategory powerCategory = (PreferenceCategory) findPreference(KEY_POWER_CATEGORY);
        mFastChargeSwitch = (TwoStatePreference) findPreference(KEY_FASTCHARGE_SWITCH);
        if (mFastChargeSwitch != null && FastChargeSwitch.isSupported(getContext())){
            mFastChargeSwitch.setEnabled(true);
            mFastChargeSwitch.setChecked(FastChargeSwitch.isCurrentlyEnabled(getContext()));
            mFastChargeSwitch.setOnPreferenceChangeListener(new FastChargeSwitch(getContext()));
        } else {
            powerCategory.removePreference(mFastChargeSwitch);
            powerCategory.getParent().removePreference(powerCategory);
        }

        PreferenceCategory audiogainsCategory = (PreferenceCategory) findPreference(KEY_AUDIOGAINS_CATEGORY);
        int audiogainsRemoved = 0;
        mEarpieceGainPref = (EarpieceGainPreference) findPreference(KEY_EARPIECE_GAIN);
        if (mEarpieceGainPref != null && mEarpieceGainPref.isSupported()) {
            mEarpieceGainPref.setEnabled(true);
        } else {
            mEarpieceGainPref.getParent().removePreference(mEarpieceGainPref);
            audiogainsRemoved += 1;
        }
        mHeadphoneGainPref = (HeadphoneGainPreference) findPreference(KEY_HEADPHONE_GAIN);
        if (mHeadphoneGainPref != null && mHeadphoneGainPref.isSupported()) {
            mHeadphoneGainPref.setEnabled(true);
        } else {
            mHeadphoneGainPref.getParent().removePreference(mHeadphoneGainPref);
            audiogainsRemoved += 1;
        }
        mMicGainPref = (MicGainPreference) findPreference(KEY_MIC_GAIN);
        if (mMicGainPref != null && mMicGainPref.isSupported()) {
            mMicGainPref.setEnabled(true);
        } else {
            mMicGainPref.getParent().removePreference(mMicGainPref);
            audiogainsRemoved += 1;
        }
        mSpeakerGainPref = (SpeakerGainPreference) findPreference(KEY_SPEAKER_GAIN);
        if (mSpeakerGainPref != null && mSpeakerGainPref.isSupported()) {
            mSpeakerGainPref.setEnabled(true);
        } else {
            mSpeakerGainPref.getParent().removePreference(mSpeakerGainPref);
            audiogainsRemoved += 1;
        }
        if (audiogainsRemoved == 4) audiogainsCategory.getParent().removePreference(audiogainsCategory);

        SelfRemovingPreferenceCategory vibratorCategory = (SelfRemovingPreferenceCategory) findPreference(KEY_VIBRATOR_CATEGORY);
        if(vibratorCategory !=null) {
            mVibratorSystemStrength = (VibratorSystemStrengthPreference) findPreference(KEY_SYSTEM_VIBSTRENGTH);
            if (mVibratorSystemStrength != null && mVibratorSystemStrength.isSupported()) {
                mVibratorSystemStrength.setEnabled(true);
            } else {
                mVibratorSystemStrength.getParent().removePreference(mVibratorSystemStrength);
            }
            mVibratorCallStrength = (VibratorCallStrengthPreference) findPreference(KEY_CALL_VIBSTRENGTH);
            if (mVibratorCallStrength != null && mVibratorCallStrength.isSupported()) {
            mVibratorCallStrength.setEnabled(true);
            } else {
                mVibratorCallStrength.getParent().removePreference(mVibratorCallStrength);
            }
            mVibratorNotifStrength = (VibratorNotifStrengthPreference) findPreference(KEY_NOTIF_VIBSTRENGTH);
            if (mVibratorNotifStrength != null && mVibratorNotifStrength.isSupported()) {
                mVibratorNotifStrength.setEnabled(true);
            } else {
                mVibratorNotifStrength.getParent().removePreference(mVibratorNotifStrength);
            }
        }
    }

    private void initRefreshRatePreference(ListPreference preference, String key) {
        List<String> entries = new ArrayList<>(), values = new ArrayList<>();
        Display.Mode mode = getContext().getDisplay().getMode();
        Display.Mode[] modes = getContext().getDisplay().getSupportedModes();
        for (Display.Mode m : modes) {
            if (m.getPhysicalWidth() == mode.getPhysicalWidth() &&
                    m.getPhysicalHeight() == mode.getPhysicalHeight()) {
                entries.add(String.format("%.02fHz", m.getRefreshRate())
                        .replaceAll("[\\.,]00", ""));
                values.add(String.format(Locale.US, "%.02f", m.getRefreshRate()));
            }
        }
        preference.setEntries(entries.toArray(new String[entries.size()]));
        preference.setEntryValues(values.toArray(new String[values.size()]));
        updateRefreshRateState(preference, key);
    }

    private void updateRefreshRateState(ListPreference preference, String key) {
        final float defaultRefreshRate = (float) getContext().getResources().getInteger(
                        com.android.internal.R.integer.config_defaultPeakRefreshRate);
        final float currentValue = Settings.System.getFloat(getContext().getContentResolver(),
                key, defaultRefreshRate);
        int index = preference.findIndexOfValue(
                String.format(Locale.US, "%.02f", currentValue));
        if (index < 0) index = 0;
        preference.setValueIndex(index);
        preference.setSummary(preference.getEntries()[index]);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSliderModeTop) {
            String value = (String) newValue;
            int sliderMode = Integer.valueOf(value);
            setSliderAction(0, sliderMode);
            int valueIndex = mSliderModeTop.findIndexOfValue(value);
            mSliderModeTop.setSummary(mSliderModeTop.getEntries()[valueIndex]);
        } else if (preference == mSliderModeCenter) {
            String value = (String) newValue;
            int sliderMode = Integer.valueOf(value);
            setSliderAction(1, sliderMode);
            int valueIndex = mSliderModeCenter.findIndexOfValue(value);
            mSliderModeCenter.setSummary(mSliderModeCenter.getEntries()[valueIndex]);
        } else if (preference == mSliderModeBottom) {
            String value = (String) newValue;
            int sliderMode = Integer.valueOf(value);
            setSliderAction(2, sliderMode);
            int valueIndex = mSliderModeBottom.findIndexOfValue(value);
            mSliderModeBottom.setSummary(mSliderModeBottom.getEntries()[valueIndex]);
        } else if (preference == mPeakRefreshRatePref) {
            Settings.System.putFloat(getContext().getContentResolver(), PEAK_REFRESH_RATE,
                    Float.valueOf((String) newValue));
            updateRefreshRateState(mPeakRefreshRatePref, PEAK_REFRESH_RATE);
        } else if (preference == mMinRefreshRatePref) {
            Settings.System.putFloat(getContext().getContentResolver(), MIN_REFRESH_RATE,
                    Float.valueOf((String) newValue));
            updateRefreshRateState(mMinRefreshRatePref, MIN_REFRESH_RATE);
        } else if (preference == mEnableDolbyAtmos) {
          boolean enabled = (Boolean) newValue;
          Intent daxService = new Intent();
          ComponentName name = new ComponentName("com.dolby.daxservice", "com.dolby.daxservice.DaxService");
          daxService.setComponent(name);
          if (enabled) {
              // enable service component and start service
              this.getContext().getPackageManager().setComponentEnabledSetting(name,
                      PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, 0);
              this.getContext().startService(daxService);
          } else {
              // disable service component and stop service
              this.getContext().stopService(daxService);
              this.getContext().getPackageManager().setComponentEnabledSetting(name,
                      PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
          }
        }
        return true;
    }

    private int getSliderAction(int position) {
        String value = Settings.System.getString(getContext().getContentResolver(),
                    Settings.System.OMNI_BUTTON_EXTRA_KEY_MAPPING);
        final String defaultValue = SLIDER_DEFAULT_VALUE;

        if (value == null) {
            value = defaultValue;
        } else if (value.indexOf(",") == -1) {
            value = defaultValue;
        }
        try {
            String[] parts = value.split(",");
            return Integer.valueOf(parts[position]);
        } catch (Exception e) {
        }
        return 0;
    }

    private void setSliderAction(int position, int action) {
        String value = Settings.System.getString(getContext().getContentResolver(),
                    Settings.System.OMNI_BUTTON_EXTRA_KEY_MAPPING);
        final String defaultValue = SLIDER_DEFAULT_VALUE;

        if (value == null) {
            value = defaultValue;
        } else if (value.indexOf(",") == -1) {
            value = defaultValue;
        }
        try {
            String[] parts = value.split(",");
            parts[position] = String.valueOf(action);
            String newValue = TextUtils.join(",", parts);
            Settings.System.putString(getContext().getContentResolver(),
                    Settings.System.OMNI_BUTTON_EXTRA_KEY_MAPPING, newValue);
        } catch (Exception e) {
        }
    }
}
