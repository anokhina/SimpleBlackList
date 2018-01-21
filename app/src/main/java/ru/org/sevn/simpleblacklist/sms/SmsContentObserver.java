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

package ru.org.sevn.simpleblacklist.sms;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;

import ru.org.sevn.simpleblacklist.AppUtil;

/**
 * Created by avn on 09.06.2016.
 */
public class SmsContentObserver extends ContentObserver {

    public SmsContentObserver(Handler handler) {
        super(handler);
    }

    private SMSListener mSMSListener;

    private void readFromOutgoingSMS() {
        if (mSMSListener != null && !mSMSListener.isStopped()) {
            Cursor cursor = mSMSListener.getContext().getContentResolver().query(
                    Uri.parse("content://sms"), null, null, null, null);
            try {
                int sms_type;
                if (cursor.moveToNext()) {
                    String sms_protocol = cursor.getString(cursor.getColumnIndex("protocol"));
                    sms_type = cursor.getInt(cursor.getColumnIndex("type"));
                    if (sms_protocol != null || sms_type != Telephony.TextBasedSmsColumns.MESSAGE_TYPE_QUEUED) {
                    } else if (sms_type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_QUEUED) {
                        SMS sms = new SMS(cursor.getLong(cursor.getColumnIndex("_id")), "",
                                AppUtil.toNum(cursor.getString(cursor.getColumnIndex("address"))),
                                cursor.getString(cursor.getColumnIndex("body")),
                                cursor.getLong(cursor.getColumnIndex("date")));
                        mSMSListener.smsOut(sms);
                    }
                }
            } finally {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public void onChange(boolean selfChange) {
        readFromOutgoingSMS();
    }
    public void start(final SMSListener smsListener) {
        this.mSMSListener = smsListener;
        smsListener.getContext().getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, this);
    }
    public void stop() {
        if (mSMSListener != null) {
            mSMSListener.getContext().getContentResolver().unregisterContentObserver(this);
            mSMSListener = null;
        }
    }

}
