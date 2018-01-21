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

import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 radioGroupWrapper = new RadioGroupWrapper((RadioGroup) findViewById(R.id.radioGrpMode));
 radioGroupWrapper.addOption((RadioButton)findViewById(R.id.radioAllowAll), AppSettings.radioAllowAll);
 radioGroupWrapper.addOption((RadioButton)findViewById(R.id.radioAllowWList), AppSettings.radioAllowWList);
 radioGroupWrapper.addOption((RadioButton)findViewById(R.id.radioDisableBList), AppSettings.radioDisableBList);

 *
 * Created by avn on 01.06.2016.
 */
public class RadioGroupWrapper {
    private final RadioGroup radioGroup;
    private final LinkedHashMap<Integer, RadioButton> optionsById = new LinkedHashMap<>();
    private final HashMap<Object, RadioButton> optionsByName = new HashMap<>();

    public RadioGroupWrapper(RadioGroup rg) {
        this.radioGroup = rg;
    }

    public RadioGroupWrapper addOption(RadioButton rb, Object id) {
        optionsById.put(rb.getId(), rb);
        optionsByName.put(id, rb);
        return this;
    }

    public RadioGroup getRadioGroup() {
        return radioGroup;
    }

    public RadioButton getRadioButtonById(int id) {
        return optionsById.get(id);
    }

    public RadioButton getRadioButton(Object k) {
        return optionsByName.get(k);
    }

    public Object getCheckedRadioButtonText() {
        RadioButton rb = optionsById.get(getRadioGroup().getCheckedRadioButtonId());
        if (rb != null) {
            return rb.getText();
        }
        return null;
    }
}
