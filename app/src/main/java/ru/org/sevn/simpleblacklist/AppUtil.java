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

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by avn on 01.06.2016.
 */
public class AppUtil {
    private AppUtil() {}

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static String toNum(String s) {
        if (s != null) {
            s = s.trim();
            s = s.replace(" ", "");
            s = s.replace("-", "");
            s = s.replace("(", "");
            s = s.replace(")", "");
        }
        return s;
    }

    public static void importFile(final AppHelper appCtx, final String extDirName, final String fileName, final boolean mode) {
        File externalFile = null;
        if (AppUtil.isExternalStorageReadable()) {
            File file = Environment.getExternalStorageDirectory();
            externalFile = new File(file.getAbsolutePath()+File.separator + extDirName +File.separator + fileName);
            //showError("----"+externalFile.getAbsolutePath());
        }
        try {
            FileInputStream fisFileName = null;
            if (externalFile != null && externalFile.exists() && externalFile.canRead()) {
                fisFileName = new FileInputStream(externalFile);
            } else {
                fisFileName = appCtx.getContext().openFileInput(fileName);
            }
            try {
                BufferedReader br = null;
                br = new BufferedReader(new InputStreamReader(fisFileName, "UTF-8"));
                for (String lOrig=br.readLine(); lOrig != null;  lOrig=br.readLine()) {
                    AppDAO.updateOrInsertPhoneMode(appCtx.getSqLiteOpenHelper(), new PhoneMode().setTitle(AppUtil.toNum(lOrig)).setMode(mode));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } finally {
                if (fisFileName != null) {
                    try {
                        fisFileName.close();
                    } catch (Exception e) {}
                }
            }
        } catch (java.io.IOException ex) {
            appCtx.showError("Can't read file " + fileName + ":" + ex.getMessage(), ex);
        }
    }

    public static void appendFileExtOrInt(final Context context, final String extDirName, final String fileName, final String msg) {
        writeFileExtOrInt(Context.MODE_APPEND, context, extDirName, fileName, msg);
    }
    public static void writeFileExtOrInt(final Context context, final String extDirName, final String fileName, final String msg) {
        writeFileExtOrInt(-1, context, extDirName, fileName, msg);
    }
    public static void writeFileExtOrInt(final int modeWr, final Context context, final String extDirName, final String fileName, final String msg) {
        File externalFile = null;
        int mode = Context.MODE_WORLD_READABLE;
        if (modeWr >= 0) {
            mode = modeWr;
        }
        if (isExternalStorageWritable()) {
            File file = Environment.getExternalStorageDirectory();
            File dir = new File(file.getAbsolutePath()+File.separator + extDirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            externalFile = new File(file.getAbsolutePath()+File.separator + extDirName + File.separator + fileName);
            //showError("----"+externalFile.getAbsolutePath());
        }
        try {

            FileOutputStream outputStream = null;
            if (externalFile != null) {
                if (externalFile.exists() && modeWr < 0) {
                    externalFile.delete();
                }
                outputStream = new FileOutputStream(externalFile, true);
            } else {
                outputStream = context.openFileOutput(fileName, mode);
            }
            try {
                outputStream.write(msg.getBytes());
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Exception e) {}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static int deleteSms(Context context, long id) {
        Uri localUri = ContentUris.withAppendedId(Uri.parse("content://sms"), id);
        if (localUri != null) {
            try {
                return context.getContentResolver().delete(localUri, null, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return -1;
    }

    public static void anyToast(final Context context, final String msg) {
        anyToast(context, msg, Toast.LENGTH_SHORT);
    }
    public static void anyToast(final Context context, final String msg, final int length) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
            new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, length).show();
                }
            }
        );
    }
}
