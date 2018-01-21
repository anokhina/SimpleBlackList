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
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by avn on 01.06.2016.
 */
public class AppHelper {
    public static final String FILE_EXT_DIR = "SimpleBL";
    public static final String FILE_EXT_LOG = "SimpleBLLog.txt";
    public static final String FILE_EXT_PHONE_INFO = "SimpleBLPhoneInfo.txt";
    public static final String FILE_EXT_BLOCKED = "SimpleBLBlocked.txt";
    public static final String FILE_EXT_FAVORITE = "SimpleBLFavorite.txt";
    public static final String FILE_EXT_DB_EXPORT = "SimpleBLExport.txt";

    private final Context context;
    private final SQLiteOpenHelper sqLiteOpenHelper;
    private final AppSettings appSettings;

    public AppHelper(Context c) {
        this(c, new AppDAO.AppEntryReaderDbHelper(c));
    }
    private AppHelper(Context c, SQLiteOpenHelper h) {
        this.context = c;
        this.sqLiteOpenHelper = h;
        this.appSettings = new AppSettings(c);
    }
    public void userNotify(String msg) {
        toast(getContext(), msg, Toast.LENGTH_LONG);
    }
    public void showError(String s, Throwable tr) {
        if (s != null) {
            toast(getContext(), "ERROR>" + s, Toast.LENGTH_SHORT);
        }
        if (tr != null) {
            tr.printStackTrace();
        }
    }
    public void showWarning(String s, Throwable tr) {
        if (tr != null) {
            tr.printStackTrace();
        }
    }

    public Context getContext() {
        return context;
    }

    public SQLiteOpenHelper getSqLiteOpenHelper() {
        return sqLiteOpenHelper;
    }

    public AppSettings getAppSettings() {
        return appSettings;
    }

    public void logToFile(String msg) {
        //System.out.println("?????????????????????????>"+msg);
        AppUtil.appendFileExtOrInt(getContext(), FILE_EXT_DIR, FILE_EXT_LOG, msg);
    }

    public void clearInit() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
                dialog.dismiss();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Clear configuration").setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void reinit() {
        AppUtil.importFile(this, AppHelper.FILE_EXT_DIR, AppHelper.FILE_EXT_BLOCKED, !true);
        AppUtil.importFile(this, AppHelper.FILE_EXT_DIR, AppHelper.FILE_EXT_FAVORITE, true);
    }

    public void reinitialize() {
        toast(getContext(), "REINIT>", Toast.LENGTH_SHORT);
        reinit();
    }
    public void export(final StringBuilder sb, final boolean b) {
        SQLiteDatabase db = this.getSqLiteOpenHelper().getReadableDatabase();
        Cursor c = null;
        try {
            c = AppDAO.selectPhones(db, b);
            // TODO use output stream
            if (c.moveToFirst()) {
                do {
                    PhoneMode pm = new PhoneMode();
                    pm.setId(c.getLong(c.getColumnIndex(AppDAO.AppDBEntry._ID)))
                            .setTitle(c.getString(c.getColumnIndex(AppDAO.AppDBEntry.COLUMN_NAME_TITLE)))
                            .setMemo(c.getString(c.getColumnIndex(AppDAO.AppDBEntry.COLUMN_NAME_MEMO)))
                            .setMode(c.getInt(c.getColumnIndex(AppDAO.AppDBEntry.COLUMN_NAME_ENTRYMODE)));
                    sb.append(pm.toExportLine()).append("\n");
                } while (c.moveToNext());
            }
        } finally {
            if ( c != null ) {
                try {
                    c.close();
                } catch (Exception e) {}
            }
            if ( db != null ) {
                try {
                    db.close();
                } catch (Exception e) {}
            }
        }
    }

    public void showPhoneInfo(final ViewActivityWrapper view, final String txt) {
        TextView tv = (TextView) view.findViewById(R.id.textView2);
        if (tv != null) {
            tv.setText(txt);
        } else {
            userNotify(txt);
        }
    }

    public void exportDB(final ViewActivityWrapper view) {
        toast(getContext(), "Export db lists", Toast.LENGTH_SHORT);

        StringBuilder sb = new StringBuilder();
        this.export(sb, false);
        this.export(sb, true);
        showPhoneInfo(view, sb.toString());

        AppUtil.writeFileExtOrInt(getContext(), AppHelper.FILE_EXT_DIR, AppHelper.FILE_EXT_DB_EXPORT, sb.toString());
    }

    public void phoneInfo(final ViewActivityWrapper view) {
        toast(getContext(), "Export phone info", Toast.LENGTH_SHORT);
        {
            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
            String txt = telephonyInfo.getInfo() + TelephonyInfo.printTMmethods(getContext()) +"\n\n\n\n\n\n";
            showPhoneInfo(view, txt);
        }
        AppUtil.writeFileExtOrInt(getContext(), AppHelper.FILE_EXT_DIR, AppHelper.FILE_EXT_PHONE_INFO, ">>>>>>>TM>>>>>>>>>>"+TelephonyInfo.printTMmethods(getContext()));
    }

    private void toast(Context context, String msg, int length) {
        try {
            Toast.makeText(context, msg, length).show();
        } catch (Exception e) {
            AppUtil.anyToast(context, msg, length);
        }
    }
}
