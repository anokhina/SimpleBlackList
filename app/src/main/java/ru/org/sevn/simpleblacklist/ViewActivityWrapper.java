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

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by avn on 07.06.2016.
 */
public class ViewActivityWrapper {
    private final View view;
    private final Activity activity;

    public ViewActivityWrapper(View v) {
        this.view = v;
        this.activity = null;
    }
    public ViewActivityWrapper(Activity a) {
        this.view = null;
        this.activity = a;
    }

    public View getView() {
        return view;
    }

    public Activity getActivity() {
        return activity;
    }
    public final View findViewById(int id) {
        if (activity != null) {
            return activity.findViewById(id);
        }
        return view.findViewById(id);
    }
    public Context getContext() {
        if (activity != null) {
            return activity;
        }
        return view.getContext();
    }
}
