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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by avn on 09.06.2016.
 */
public abstract class PhoneActionProcessor {
    protected abstract String disableAction(final AppHelper appHelper);
    protected void disable(final String incomingNumber, final Object extra) {
        //abortBroadcast();
        String methodEndCall = disableAction(getAppHelper());
        getAppHelper().userNotify("BLOCKING: "+methodEndCall+" " +incomingNumber);
        getAppHelper().logToFile(getDisabledLogMessage(incomingNumber, extra));
        logCall(incomingNumber);
    }

    protected void logCall(final String incomingNumber) {
        AppDAO.insertCall(getAppHelper().getSqLiteOpenHelper(), incomingNumber);
    }

    protected String getDisabled() {
        return " - ";
    }

    protected String getAllowed() {
        return " + ";
    }

    protected String getAllowedExtra(final Object extra) {
        return getExtra(extra);
    }

    protected String getDisabledLogMessage(final String incomingNumber, final Object extra) {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + getDisabled() + incomingNumber + "\n" + getExtra(extra);
    }

    protected String getAllowedLogMessage(final String incomingNumber, final Object extra) {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + getAllowed() + incomingNumber + "\n" + getAllowedExtra(extra);
    }

    protected void allow(final String incomingNumber, final Object extra) {
        getAppHelper().logToFile(getAllowedLogMessage(incomingNumber, extra));
    }

    protected abstract AppHelper getAppHelper();

    protected String processNumber(final int mode, final PhoneMode pmode, final String incomingNumber, final Object extra) {
        String msg = "";
        AppSettings appSettings = getAppHelper().getAppSettings();
        if (mode == AppSettings.radioAllowAll) {
            allow(incomingNumber, extra);
        } else if (mode == AppSettings.radioAllowWList) {
            if (pmode != null && pmode.isMode()) {
                allow(incomingNumber, extra);
            } else {
                disable(incomingNumber, extra);
            }
        } else if (mode == AppSettings.radioDisableBList) {
            if (pmode != null && !pmode.isMode()) {
                disable(incomingNumber, extra);
            } else {
                allow(incomingNumber, extra);
            }
        }

        msg += "\n" + appSettings.getModeName(mode);
        if (pmode != null) {
            msg += "\nWHITE List = " + pmode.isMode();
        }
        return msg;
    }

    public String filterAction(final String phoneNumber, final Object extra, final int mode) {
        if (phoneNumber!= null) try {
            PhoneMode pmode = AppDAO.selectPhone(getAppHelper().getSqLiteOpenHelper(), phoneNumber);
            return processNumber(mode, pmode, phoneNumber, extra);
        } catch (Exception e) {
            getAppHelper().showError("CALL BLOCKING ERROR for" + phoneNumber, e);
        }
        return "";
    }

    protected String getExtra(final Object extra) {
        return "";
    }
}
