/*
 * Copyright 2012 CodeSlap
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
 */

package com.github.jobs.loader;

import android.content.Context;
import android.util.Log;
import com.codeslap.groundy.ListLoader;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.PreferencesAdapter;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.bean.GeneralSettings;
import com.github.jobs.bean.Template;
import com.github.jobs.utils.AppUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author cristian
 */
public class TemplatesLoader extends ListLoader<Template> {

    public TemplatesLoader(Context context) {
        super(context);
    }

    @Override
    protected List<Template> getData() {
        SqlAdapter sqlAdapter = Persistence.getAdapter(getContext());
        List<Template> templates = sqlAdapter.findAll(Template.class);
        if (templates.isEmpty()) {
            PreferencesAdapter preferenceAdapter = Persistence.getPreferenceAdapter(getContext());
            GeneralSettings generalSettings = preferenceAdapter.retrieve(GeneralSettings.class);
            if (!generalSettings.hasAlreadyCreatedDefaultTemplate()) {
                // create default template and save it
                Template defaultTemplate = AppUtils.getDefaultTemplate(getContext());
                templates.add(defaultTemplate);
                sqlAdapter.store(defaultTemplate);

                // make sure this is not created again
                generalSettings.setAlreadyCreatedDefaultTemplate(true);
                preferenceAdapter.store(generalSettings);
            }
        }
        return sort(templates);
    }

    private List<Template> sort(List<Template> jobs) {
        if (jobs == null) {
            return null;
        }
        try {
            Collections.sort(jobs, TEMPLATE_COMPARATOR);
        } catch (Exception e) {
            Log.wtf("jobs:listLoader", "General contract should not be wrong :-/", e);
        }
        return jobs;
    }

    private static final Comparator<Template> TEMPLATE_COMPARATOR = new Comparator<Template>() {
        @Override
        public int compare(Template templateA, Template templateB) {
            if (templateA == null) {
                return -1;
            }
            if (templateB == null) {
                return 1;
            }
            Date dateA = new Date(templateA.getLastUpdate());
            Date dateB = new Date(templateB.getLastUpdate());
            return dateB.compareTo(dateA);
        }
    };
}
