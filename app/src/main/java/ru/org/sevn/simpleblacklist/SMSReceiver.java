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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import ru.org.sevn.simpleblacklist.sms.SMS;

/**
 * Created by avn on 08.06.2016.
 */
public class SMSReceiver extends BroadcastReceiver {

    protected String disableActionLocal(final AppHelper appHelper) {
        String methodEndCall = "false";
        try {
            abortBroadcast();
            methodEndCall = "abortBroadcast";
        } catch (Exception ex) {
            appHelper.showError(null, ex);
        }
        return methodEndCall;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final AppHelper appHelper = new AppHelper(context);
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");
        appHelper.userNotify("SMS>"+pdus.length);

        if (pdus.length == 0) {
            return;
        }

        //abortBroadcast
        SMS sms = fromPdus(pdus);
        PhoneActionProcessor processor = new PhoneActionProcessor() {
            @Override
            protected String disableAction(AppHelper appHelper) {
                return disableActionLocal(appHelper);
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
                return " -sms- ";
            }
            protected String getAllowed() {
                return " +sms+ ";
            }
        };
        processor.filterAction(sms.getSender(), sms, appHelper.getAppSettings().getModeSms());
    }

    public static SMS fromPdus(final Object[] pdus) {
        SMS result = new SMS(SmsMessage.createFromPdu((byte[]) pdus[0]));
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);
            result.appendMessageBody(sms.getMessageBody());
        }

        return result;
    }
}
