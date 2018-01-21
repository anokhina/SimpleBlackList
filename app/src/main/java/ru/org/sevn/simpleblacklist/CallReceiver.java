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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

/**
 * Created by avn on 30.05.2016.
 */
public class CallReceiver extends BroadcastReceiver {

    /*
    private boolean endCallVariant(Context context) {
        boolean ret = false;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Object serviceManagerObject;

            final Class telephonyClass = Class.forName("com.android.internal.telephony.ITelephony");
            final Class[] telephonyStubClasses = telephonyClass.getClasses();
            final Class serviceManagerClass = Class.forName("android.os.ServiceManager");
            final Class serviceManagerNativeClass = Class.forName("android.os.ServiceManagerNative");

            final Method telephonyEndCall = telephonyClass.getMethod("endCall");
            Method endCallForSubscriber = null;
            try {
                endCallForSubscriber = telephonyClass.getMethod("endCallForSubscriber");
            } catch (Exception e) {};
            Method getDefaultSubscription = null;
            try {
                getDefaultSubscription = tm.getClass().getMethod("getDefaultSubscription");
            } catch (Exception e) {}

            final Method getService =
                    serviceManagerClass.getMethod("getService", String.class);
            final Method listServices =
                    serviceManagerClass.getMethod("listServices");

            final Method tempInterfaceMethod = serviceManagerNativeClass.getMethod(
                    "asInterface", IBinder.class);// static

            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");

            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);//call static
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, Context.TELEPHONY_SERVICE); //"phone"

            for (Class telephonyStubClass : telephonyStubClasses) {
                Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
                Object telephonyObject = serviceMethod.invoke(null, retbinder);
                Boolean b = (Boolean)telephonyEndCall.invoke(telephonyObject);
                if (b != null) {
                    ret = b;
                }
                if (!ret && endCallForSubscriber != null) {
                    b = (Boolean)endCallForSubscriber.invoke(telephonyObject, 1);
                    ret = b;
                }
                break;
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(CallReceiver.this.getClass().getName(),
                    "FATAL ERROR: could not connect to telephony subsystem");
            Log.e(CallReceiver.this.getClass().getName(), "Exception object: " + e);
        }
        return ret;
    }*/

    private boolean endCallByTMReflection(AppHelper appHelper) {
        TelephonyManager tm = (TelephonyManager) appHelper.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            //getITelephonyMSim
            //getITelephony
            Method m = null;
            try {
                m = c.getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                Object telephonyService = m.invoke(tm);
                appHelper.logToFile(">>>>>>>>>endCall>>>>>>>>getITelephony");
                Method mcall = telephonyService.getClass().getMethod("endCall");
                return (boolean)mcall.invoke(telephonyService);
            } catch (Exception ex) {
                m = c.getDeclaredMethod("getITelephonyMSim");
                m.setAccessible(true);
                Object telephonyService = m.invoke(tm);
                appHelper.logToFile(">>>>>>>>>endCall>>>>>>>>getITelephonyMSim");
                Method mcall = telephonyService.getClass().getMethod("endCall", int.class);
                Method mgetPreferredDataSubscription = telephonyService.getClass().getMethod("getPreferredDataSubscription");
                int csub = (int)mgetPreferredDataSubscription.invoke(telephonyService);
                appHelper.logToFile(">>>>>>>>>endCall>>>>>>>>getPreferredDataSubscription:"+csub);
                boolean ret = (boolean)mcall.invoke(telephonyService,csub);
                if (ret) return ret;
                ret = (boolean)mcall.invoke(telephonyService,csub + 1);
                return ret;
            }
            //telephonyService.silenceRinger();
        } catch (Exception e) {
            appHelper.logToFile(">>>>>>>>>endCall>>>>>>>>can't end call");
            appHelper.showError(null, e);
        }
        return false;
    }

    protected String disableActionLocal(final AppHelper appHelper) {
        String methodEndCall = "false";
        try {
            if (!endCallByTMReflection(appHelper)) {
                setResultData(null);
                methodEndCall = "abortBroadcast";
            } else {
                methodEndCall = "true";
            }
        } catch (Exception ex) {
            appHelper.showError(null, ex);
        }
        return methodEndCall;
    }

    protected String filterCall(final Context context, final Intent intent, final AppHelper appHelper, final String phoneKey, final String msgTitle, final int mode) {
        PhoneActionProcessor processor = new PhoneActionProcessor() {
            @Override
            protected String disableAction(AppHelper appHelper) {
                return disableActionLocal(appHelper);
            }

            @Override
            protected AppHelper getAppHelper() {
                return appHelper;
            }
        };
        String msg = "";
        String phoneNumber = intent.getStringExtra(phoneKey);
        msg += msgTitle + phoneNumber;
        msg += processor.filterAction(phoneNumber, null, mode);
        return msg;
    }

    @Override
    public void onReceive(Context context, Intent intent){
        AppHelper appHelper = new AppHelper(context);

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String msg = "Phone state changed to " + state;

        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
            msg += filterCall(context, intent, appHelper, Intent.EXTRA_PHONE_NUMBER, ". Outgoing number is ", appHelper.getAppSettings().getModeOut());
        } else
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            msg += filterCall(context, intent, appHelper, TelephonyManager.EXTRA_INCOMING_NUMBER, ". Incoming number is ", appHelper.getAppSettings().getMode());
        }

        appHelper.userNotify(msg);
    }

}
