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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by avn on 30.05.2016.
 */
public class AppDAO {
    public static abstract class AppDBEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String TABLE_NAME_LOG = "entry_log";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_MEMO = "memo";
        public static final String COLUMN_NAME_ENTRYMODE = "entrymode";
        public static final String COLUMN_NAME_LOG_TIME = "logtime";
    }

    private static final String PHONE_TYPE = " TEXT ";
    private static final String TEXT_TYPE = " TEXT ";
    private static final String BOOLEAN_TYPE = " INTEGER ";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AppDBEntry.TABLE_NAME + " (" +
                    AppDBEntry._ID + " INTEGER PRIMARY KEY," +
                    AppDBEntry.COLUMN_NAME_TITLE + PHONE_TYPE + "UNIQUE" + COMMA_SEP +
                    AppDBEntry.COLUMN_NAME_MEMO + TEXT_TYPE + COMMA_SEP +
                    AppDBEntry.COLUMN_NAME_ENTRYMODE + BOOLEAN_TYPE +
                    " )";
    private static final String SQL_CREATE_LOG =
            "CREATE TABLE " + AppDBEntry.TABLE_NAME_LOG + " (" +
                    AppDBEntry._ID + " INTEGER PRIMARY KEY," +
                    AppDBEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    AppDBEntry.COLUMN_NAME_LOG_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AppDBEntry.TABLE_NAME;
    private static final String SQL_DELETE_LOG =
            "DROP TABLE IF EXISTS " + AppDBEntry.TABLE_NAME_LOG;

    public static class AppEntryReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 3;
        public static final String DATABASE_NAME = "Phones.db";

        public AppEntryReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
            db.execSQL(SQL_CREATE_LOG);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            db.execSQL(SQL_DELETE_LOG);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public static PhoneMode selectPhone(SQLiteOpenHelper mDbHelper, String title) {
        if (title == null) return null;
        PhoneMode pm = null;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            String[] projection = {
                    AppDBEntry._ID,
                    AppDBEntry.COLUMN_NAME_TITLE,
                    AppDBEntry.COLUMN_NAME_ENTRYMODE
            };

            String sortOrder =
                    AppDBEntry.COLUMN_NAME_TITLE + " ASC";

            String selection = AppDBEntry.COLUMN_NAME_TITLE + " = ? ";
            String[] selectionArgs = new String[]{title};

            c = db.query(
                    AppDBEntry.TABLE_NAME,
                    projection,                               // The columns receiver return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            if (c.moveToFirst()) {
                pm = new PhoneMode();
                pm.setId(c.getLong(c.getColumnIndex(AppDBEntry._ID))).setTitle(c.getString(c.getColumnIndex(AppDBEntry.COLUMN_NAME_TITLE))).setMode(c.getInt(c.getColumnIndex(AppDBEntry.COLUMN_NAME_ENTRYMODE)));
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
        return pm;
    }

    public static Cursor selectPhones(final SQLiteDatabase db, final boolean mode) {

        String[] projection = {
                AppDBEntry._ID,
                AppDBEntry.COLUMN_NAME_TITLE,
                AppDBEntry.COLUMN_NAME_MEMO,
                AppDBEntry.COLUMN_NAME_ENTRYMODE
        };

        String sortOrder =
                AppDBEntry.COLUMN_NAME_TITLE + " ASC";

        String selection = AppDBEntry.COLUMN_NAME_ENTRYMODE + " = ? ";
        String title = "0";
        if (mode) {
            title = "1";
        }
        String[] selectionArgs = new String[]{title};

        Cursor c = db.query(
                AppDBEntry.TABLE_NAME,
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        return c;
    }

    private static final boolean INSERTCALL = false;

    public static long insertCall(SQLiteOpenHelper mDbHelper, String title) {
        if (INSERTCALL) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            try {

                ContentValues values = new ContentValues();
                values.put(AppDBEntry.COLUMN_NAME_TITLE, title);

                long newRowId;
                newRowId = db.insert(
                        AppDBEntry.TABLE_NAME_LOG,
                        null,
                        values);
                return newRowId;
            } finally {
                if (db != null) {
                    try {
                        db.close();
                    } catch (Exception e) {}
                }
            }
        }
        return 0;
    }

    private static boolean isEmpty(final String s) {
        if (s == null || s.trim().length() == 0) {
            return true;
        }
        return false;
    }
    public static long insertPhoneMode(final SQLiteOpenHelper mDbHelper, final PhoneMode pm) {
        Log.d("zzz2", pm.getTitle());
        if (isEmpty(pm.getTitle())) {
            return 0;
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(AppDBEntry.COLUMN_NAME_TITLE, pm.getTitle());
            values.put(AppDBEntry.COLUMN_NAME_ENTRYMODE, pm.getMode());

            long newRowId;
            newRowId = db.insert(
                    AppDBEntry.TABLE_NAME,
                    null,
                    values);
            pm.setId(newRowId);
            return newRowId;
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {}
            }
        }
    }

    public static int updatePhoneMode(SQLiteOpenHelper mDbHelper, PhoneMode pm) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(AppDBEntry.COLUMN_NAME_ENTRYMODE, pm.getMode());
            return db.update(AppDBEntry.TABLE_NAME, values, AppDBEntry.COLUMN_NAME_TITLE + " = ? ", new String[]{pm.getTitle()});
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {}
            }
        }
    }

    public static int updateOrInsertPhoneMode(SQLiteOpenHelper mDbHelper, PhoneMode pm) {
        PhoneMode pmStored = selectPhone(mDbHelper, pm.getTitle());
        if (pmStored != null) {
            pm.setId(pmStored.getId());
            if (pm.isMode() != pmStored.isMode()) {
                return updatePhoneMode(mDbHelper, pm);
            }
        } else {
            if (insertPhoneMode(mDbHelper, pm) != 0) {
                return 1;
            }
        }
        return 0;
    }
}
