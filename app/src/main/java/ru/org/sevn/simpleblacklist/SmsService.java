/*
 * Copyright [2016] Veronika Anokhina
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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import ru.org.sevn.simpleblacklist.sms.SMS;
import ru.org.sevn.simpleblacklist.sms.SMSListener;
import ru.org.sevn.simpleblacklist.sms.SmsContentObserver;

/**
 * Created by avn on 09.06.2016.
 */
public class SmsService extends Service implements SMSListener {
    private AppHelper appHelper;
    private SmsContentObserver mSMSObserver;
    private int myID;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myID = startId;
        appHelper = new AppHelper(getApplicationContext());
        mSMSObserver = new SmsContentObserver(null);
        mSMSObserver.start(this);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (isDestroyable()) {
            super.onDestroy();
            Log.e("OnDestroy", "Stopping Service");
            mSMSObserver.stop();
            try {
                stopSelf(myID);
                Log.e("Stop self", "Stopped Service");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isDestroyable() {
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void smsIn(SMS sms) {
    }

    public void smsOut(final SMS sms) {
        AppUtil.anyToast(getContext(), "SMS->"+sms.getReceiver());
        if (!isStopped()) {

            PhoneActionProcessor processor = new PhoneActionProcessor() {
                @Override
                protected void logCall(final String incomingNumber) {}

                @Override
                protected String disableAction(AppHelper appHelper) {
                    //appHelper.logToFile(getDisabledLogMessage(sms.getReceiver(), sms));
                    if (AppUtil.deleteSms(getContext(), sms.getId()) > 0) {
                        return "outgoing sms deleted";
                    }
                    return "";
                }

                @Override
                protected AppHelper getAppHelper() {
                    return appHelper;
                }
                protected String getAllowedExtra(final Object extra) {
                    return "";
                }

                protected String getExtra(final Object extra) {
                    if (extra != null && extra instanceof SMS) {
                        SMS sms = (SMS)extra;
                        return "" + sms.getTimestamp() + " " + sms.getMessageBody() + "\n";
                    }
                    return "";
                }

                protected String getDisabled() {
                    return " -smsout- ";
                }
                protected String getAllowed() {
                    return " +smsout+ ";
                }
            };
            processor.filterAction(sms.getReceiver(), sms, appHelper.getAppSettings().getModeSmsOut());
        }
    }


    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public boolean isStopped() {
        return false;//MainActivity.stopped
    }

}