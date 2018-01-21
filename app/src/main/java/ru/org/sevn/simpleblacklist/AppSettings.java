/*
 * Copyright 2016 Veronica Anokhina
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
 *
 */
package ru.org.sevn.simpleblacklist;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by avn on 01.06.2016.
 */
public class AppSettings {

    public static final String MODE_PREF_NAME = "ru.org.sevn.simpleblacklist.pref.mode";
    public static final String MODE_PREF_NAME_SMS = "ru.org.sevn.simpleblacklist.pref.modesms";
    public static final String MODE_PREF_NAME_OUT = "ru.org.sevn.simpleblacklist.pref.modeout";
    public static final String MODE_PREF_NAME_SMS_OUT = "ru.org.sevn.simpleblacklist.pref.modesmsout";
    public static final String PREF_NAME = "ru.org.sevn.simpleblacklist.PREFERENCE_KEY";
    public static final int radioAllowAll = 0;
    public static final int radioAllowWList = 1;
    public static final int radioDisableBList = 2;

    private SharedPreferences sharedPref;

    private int mode = 0;
    private int modeOut = 0;
    private int modeSms = 0;
    private int modeSmsOut = 0;

    public int getMode() {
        return mode;
    }
    public int getModeOut() {
        return modeOut;
    }
    public int getModeSms() {
        return modeSms;
    }
    public int getModeSmsOut() {
        return modeSmsOut;
    }

    public String getModeName(int mode) {
        switch(mode) {
            case radioAllowAll:
                return "ALLOW ALL";
            case radioAllowWList:
                return "ALLOW White List";
            case radioDisableBList:
                return "BLOCK Black List";
        }
        return "UNKNOWN BLOCKING";
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
    public void setModeOut(int mode) {
        this.modeOut = mode;
    }
    public void setModeSms(int mode) {
        this.modeSms = mode;
    }
    public void setModeSmsOut(int mode) {
        this.modeSmsOut = mode;
    }

    public AppSettings(Context ctx) {
        sharedPref = ctx.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        //activity.getPreferences(Context.MODE_PRIVATE);
        this.setMode(sharedPref.getInt(MODE_PREF_NAME, 0));
        this.setModeSms(sharedPref.getInt(MODE_PREF_NAME_SMS, 0));
        this.setModeOut(sharedPref.getInt(MODE_PREF_NAME_OUT, 0));
        this.setModeSmsOut(sharedPref.getInt(MODE_PREF_NAME_SMS_OUT, 0));
    }

    public SharedPreferences getSharedPref() {
        return sharedPref;
    }

    public void setSharedPref(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    public void commit(Context ctx) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putInt(MODE_PREF_NAME, getMode());
        editor.putInt(MODE_PREF_NAME_SMS, getModeSms());
        editor.putInt(MODE_PREF_NAME_OUT, getModeOut());
        editor.putInt(MODE_PREF_NAME_SMS_OUT, getModeSmsOut());
        editor.commit();
    }
}
