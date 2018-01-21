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

/**
 * Created by avn on 01.06.2016.
 */
public class PhoneMode {
    private long id;
    private String title;
    private String memo;
    private boolean mode;

    public long getId() {
        return id;
    }

    public PhoneMode setId(long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PhoneMode setTitle(String title) {
        this.title = title;
        return this;
    }

    public boolean isMode() {
        return mode;
    }

    public PhoneMode setMode(int mode) {
        if (mode > 0) {
            return setMode(true);
        }
        return setMode(false);
    }

    public int getMode() {
        if (this.isMode()) {
            return 1;
        }
        return 0;
    }

    public PhoneMode setMode(boolean mode) {
        this.mode = mode;
        return this;
    }

    public String getMemo() {
        return memo;
    }

    public PhoneMode setMemo(final String memo) {
        if (memo != null && memo.trim().length() == 0) {
            this.memo = null;
        } else {
            this.memo = memo;
        }
        return this;
    }

    public static final String TAG_MODE_B = "";
    public static final String TAG_MODE_E = " ";
    public static final String TAG_MEMO_B = "<MEMO>";
    public static final String TAG_MEMO_E = "</MEMO>";
    public static final String TAG_PHONE_B = ""; // "<PHONE>";
    public static final String TAG_PHONE_E = ""; // "</PHONE>";
    public String toExportLine() {
        return TAG_MODE_B + getMode() + TAG_MODE_E + TAG_PHONE_B+checkNull(getTitle())+TAG_PHONE_E+TAG_MEMO_B+checkNull(getMemo())+TAG_MEMO_E;
    }

    public void initFromExportLine(final String s) {
        setId(0);
        setMode(0);
        setTitle(null);
        setMemo(null);
        int idx = s.indexOf(TAG_MODE_E) + TAG_MODE_E.length();
        if (TAG_MODE_B.length() > 0) {
            if (s.indexOf(TAG_MODE_B) >= 0) {
                setTitle(tr(s.substring(s.indexOf(TAG_MODE_B) + TAG_MODE_B.length(), s.indexOf(TAG_MODE_E))));
            }
        } else {
            setMode(Integer.parseInt(tr(s.substring(0, s.indexOf(TAG_MODE_E)))));
        }
        int midx = s.length();
        if (TAG_MEMO_B.length() > 0) {
            if (s.indexOf(TAG_MEMO_B) >= 0) {
                midx = s.indexOf(TAG_MEMO_E) + TAG_MEMO_E.length();
                setMemo(tr(s.substring(s.indexOf(TAG_MEMO_B) + TAG_MEMO_B.length(), s.indexOf(TAG_MEMO_E))));
            }
        } else {
            // no memo
        }

        if (TAG_PHONE_B.length() > 0) {
            if (s.indexOf(TAG_PHONE_B) >= 0) {
                setTitle(tr(s.substring(s.indexOf(TAG_PHONE_B) + TAG_PHONE_B.length(), s.indexOf(TAG_PHONE_E))));
            }
        } else {
            setTitle(tr(s.substring(idx, midx)));
        }
    }

    private String tr(final String o) {
        return o.trim();
    }
    private Object checkNull(final Object o) {
        if (o == null) return "";
        return o;
    }
}
