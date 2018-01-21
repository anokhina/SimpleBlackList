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

import android.telephony.SmsMessage;

/**
 * Created by avn on 09.06.2016.
 */
public class SMS {
    private long id;
    private long timestamp;
    private String sender;
    private String messageBody;
    private String receiver;

    public SMS(SmsMessage smsMessage) {
        sender = smsMessage.getOriginatingAddress();
        timestamp = smsMessage.getTimestampMillis();
        messageBody = smsMessage.getMessageBody();
    }

    public SMS(SMS sms) {
        this.id = sms.id;
        this.timestamp = sms.timestamp;
        this.sender = sms.sender;
        this.messageBody = sms.messageBody;
        this.receiver = sms.receiver;
    }
    public SMS(long id, String from, String to, String msg, long date) {
        this.id = id;
        this.sender = from;
        this.receiver = to;
        this.messageBody = msg;
        this.timestamp = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void appendMessageBody(String s) {
        messageBody += s;
    }
}
