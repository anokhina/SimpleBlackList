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
package ru.org.sevn.simpleblacklist.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ru.org.sevn.simpleblacklist.AppHelper;
import ru.org.sevn.simpleblacklist.AppSettings;
import ru.org.sevn.simpleblacklist.MainActivity;
import ru.org.sevn.simpleblacklist.R;
import ru.org.sevn.simpleblacklist.ViewActivityWrapper;

/**
 * Created by avn on 07.06.2016.
 */
public class SmsOutFragment extends Fragment implements Titled {
    private static final String title = "SMS Out";

    public String getTitle() {
        return title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_smsout, container, false);
        AppHelper appHelper = ((MainActivity)getActivity()).getAppHelper();
        ViewActivityWrapper view = new ViewActivityWrapper(rootView);
        initRadioButtons(appHelper, view);

        return rootView;
    }

    private void initRadioButtons(final AppHelper appHelper, final ViewActivityWrapper vw) {
        final RadioGroup radioGroup;
        final RadioButton radioAllowAll;
        final RadioButton radioAllowWList;
        final RadioButton radioDisableBList;
        radioGroup = (RadioGroup) vw.findViewById(R.id.radioGrpModeSmsOut);
        radioAllowAll = (RadioButton) vw.findViewById(R.id.radioAllowAllSmsOut);
        radioAllowWList = (RadioButton) vw.findViewById(R.id.radioAllowWListSmsOut);
        radioDisableBList = (RadioButton) vw.findViewById(R.id.radioDisableBListSmsOut);

        switch (appHelper.getAppSettings().getModeSmsOut()) {
            case AppSettings.radioAllowWList:
                radioAllowWList.setChecked(true);
                break;
            case AppSettings.radioDisableBList:
                radioDisableBList.setChecked(true);
                break;
            default:
                radioAllowAll.setChecked(true);
        }

        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    //Toast.makeText(getApplicationContext(), "RRRRRRRRRRR>"+checkedId, Toast.LENGTH_SHORT).show();
                    AppSettings appSettings = appHelper.getAppSettings();

                    if (radioAllowWList != null && radioAllowWList.getId() == checkedId) {
                        appSettings.setModeSmsOut(AppSettings.radioAllowWList);
                    } else if (radioDisableBList != null && radioDisableBList.getId() == checkedId) {
                        appSettings.setModeSmsOut(AppSettings.radioDisableBList);
                    } else {
                        appSettings.setModeSmsOut(AppSettings.radioAllowAll);
                    }
                    appSettings.commit(getContext());
                }
            });
        }
    }

}
