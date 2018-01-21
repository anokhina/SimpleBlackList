package ru.org.sevn.simpleblacklist;
import java.lang.reflect.Method;
import java.util.Collection;

import android.content.Context;
import android.telephony.TelephonyManager;

// because https://developer.android.com/about/versions/android-5.1.html#multisim

public final class TelephonyInfo {

    public static String printTMmethods(Context context) {
        StringBuilder sb = new StringBuilder();

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> telephonyClass;
        try {
            telephonyClass = Class.forName(telephony.getClass().getName());
            Method[] methods = telephonyClass.getDeclaredMethods();
            for (int idx = 0; idx < methods.length; idx++) {

                sb.append("\n\n[" + methods[idx] + "] declared by " + methods[idx].getDeclaringClass());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static TelephonyInfo instance;

    private String imsiSIM1;
    private String imsiSIM2;
    private boolean isSIM1Ready;
    private boolean isSIM2Ready;

    public String getImsiSIM1() {
        return imsiSIM1;
    }

    public String getImsiSIM2() {
        return imsiSIM2;
    }

    public boolean isSIM1Ready() {
        return isSIM1Ready;
    }

    public boolean isSIM2Ready() {
        return isSIM2Ready;
    }

    public boolean isDualSIM() {
        return imsiSIM2 != null;
    }

    private TelephonyInfo() {
    }

    public static TelephonyInfo getInstance(AppHelper appHelper){
        Context context = appHelper.getContext();

        if(instance == null) {

            instance = new TelephonyInfo();

            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

            instance.imsiSIM1 = telephonyManager.getDeviceId();
            instance.imsiSIM2 = null;

            try {
                instance.imsiSIM1 = (String)tryInvokeChain(context, new String[] {/*"getDeviceIdGemini",*/ "getDeviceId"}, new Class[] { int.class }, new Object[] { 0 }, null, null );
            } catch (TMMethodNotFoundException e) {
                appHelper.showWarning("Not found: " + e.getMessage(), e);
            }
            try {
                instance.imsiSIM2 = (String)tryInvokeChain(context, new String[] {/*"getDeviceIdGemini",*/ "getDeviceId"}, new Class[] { int.class }, new Object[] { 1 }, null, null );
            } catch (TMMethodNotFoundException e) {
                appHelper.showWarning("Not found: " + e.getMessage(), e);
            }

            instance.isSIM1Ready = (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY);
            instance.isSIM2Ready = false;

            try {
                instance.isSIM1Ready = toEquals(tryInvokeChain(context, new String[] {/*"getSimStateGemini",*/ "getSimState"}, new Class[] { int.class }, new Object[] { 0 }, null, null ), TelephonyManager.SIM_STATE_READY);
            } catch (TMMethodNotFoundException e) {
                appHelper.showWarning("Not found: " + e.getMessage(), e);
            }
            try {
                instance.isSIM2Ready = toEquals(tryInvokeChain(context, new String[] {/*"getSimStateGemini",*/ "getSimState"}, new Class[] { int.class }, new Object[] { 1 }, null, null ), TelephonyManager.SIM_STATE_READY);
            } catch (TMMethodNotFoundException e) {
                appHelper.showWarning("Not found: " + e.getMessage(), e);
            }
        }

        return instance;
    }

    public String getInfo() {
        return " IME1 : " + getImsiSIM1() + "\n" +
                " IME2 : " + getImsiSIM2() + "\n" +
                " IS DUAL SIM : " + isDualSIM() + "\n" +
                " IS SIM1 READY : " + isSIM1Ready() + "\n" +
                " IS SIM2 READY : " + isSIM2Ready() + "\n";
    }

    private static boolean toEquals(Object o1, Object o2) {
        if (o1 != null && o1.equals(o2) || o1 == null && o2 == null) {
            return true;
        }
        return false;
    }

    private static Object tryInvokeChain(Context context, String[] predictedMethodNames, Class<?>[] parametersMethod, Object[] parametersInvoke, Collection<Throwable> errors, Object defValue) throws TMMethodNotFoundException {
        TMMethodNotFoundException lastErr = null;
        for (String predictedMethodName : predictedMethodNames) {
            try {
                Object ret = tryInvoke(context, predictedMethodName, parametersMethod, parametersInvoke);
                return ret;
            } catch (TMMethodNotFoundException e) {
                lastErr = e;
                if (errors != null) {
                    errors.add(e);
                }
            }
        }
        if (defValue == null && lastErr != null) {
            throw lastErr;
        }
        return defValue;
    }

    private static Object tryInvoke(Context context, String predictedMethodName, Class<?>[] parametersMethod, Object[] parametersInvoke) throws TMMethodNotFoundException {

        Object ret = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parametersMethod);
            ret = getSimID.invoke(telephony, parametersInvoke);
        } catch (Exception e) {
            throw new TMMethodNotFoundException(predictedMethodName, e);
        }

        return ret;
    }

    private static class TMMethodNotFoundException extends Exception {
        public TMMethodNotFoundException(String info, Throwable tr) {
            super(info, tr);
        }
    }
}