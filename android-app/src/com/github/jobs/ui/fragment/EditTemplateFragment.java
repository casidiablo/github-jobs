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

package com.github.jobs.ui.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ViewSwitcher;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.Template;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.events.*;
import com.github.jobs.templates.TemplatesHelper;
import com.github.jobs.ui.dialog.RemoveServicesDialog;
import com.github.jobs.utils.AppUtils;
import com.github.jobs.utils.GithubJobsJavascriptInterface;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;

import static com.github.jobs.utils.GithubJobsJavascriptInterface.JS_INTERFACE;
import static com.github.jobs.utils.GithubJobsJavascriptInterface.PREVIEW_TEMPLATE_URL;


/**
 * @author cristian
 * @version 1.0
 */
public class EditTemplateFragment extends BusFragment {
  private static final String KEY_TEMPLATE_SERVICES = "com.github.jobs.key.template_services";

  public static final String ARG_TEMPLATE_ID = "com.github.jobs.arg.template_id";
  public static final String ARG_EDIT_MODE = "com.github.jobs.arg.edit_mode";

  private static final int EDITOR_MODE = 0;
  private static final int PREVIEW_MODE = 1;
  private static final String TAG = EditTemplateFragment.class.getName();

  private EditText mTemplateContent;
  private EditText mTemplateName;

  private long mTemplateId;
  private GithubJobsJavascriptInterface mJavascriptInterface;
  private ArrayList<TemplateService> mTemplateServices;
  private ViewSwitcher mViewSwitcher;
  private boolean mShowEditor = false;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mTemplateServices = new ArrayList<TemplateService>();
    if (savedInstanceState != null) {
      ArrayList<Parcelable> list = savedInstanceState.getParcelableArrayList(KEY_TEMPLATE_SERVICES);
      if (list != null) {
        for (Parcelable parcelable : list) {
          mTemplateServices.add((TemplateService) parcelable);
        }
      }
    }
    return inflater.inflate(R.layout.edit_template, null, false);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelableArrayList(KEY_TEMPLATE_SERVICES, mTemplateServices);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // init argument fields
    Bundle arguments = getArguments();
    mShowEditor = arguments.getBoolean(ARG_EDIT_MODE, false);
    mTemplateId = arguments.getLong(ARG_TEMPLATE_ID, -1);

    // prepare ui
    View root = getView();
    mViewSwitcher = (ViewSwitcher) root.findViewById(R.id.switcher_edit_mode);
    if (!mShowEditor) {
      showEditor(mShowEditor);
    }

    WebView templatePreview = (WebView) root.findViewById(R.id.lbl_cover_letter_preview);
    AppUtils.setupWebView(templatePreview);
    mJavascriptInterface = new GithubJobsJavascriptInterface(getActivity(), templatePreview, null);
    templatePreview.addJavascriptInterface(mJavascriptInterface, JS_INTERFACE);
    templatePreview.loadUrl(PREVIEW_TEMPLATE_URL);

    mTemplateName = (EditText) root.findViewById(R.id.edit_cover_letter_name);
    mTemplateContent = (EditText) root.findViewById(R.id.edit_cover_letter_content);
    mTemplateContent.addTextChangedListener(mTextWatcher);

    if (mTemplateId != -1) {
      // retrieve template from database
      SqlAdapter adapter = Persistence.getAdapter(getActivity());
      Template template = new Template();
      template.setId(mTemplateId);
      template = adapter.findFirst(template);
      ArrayList<TemplateService> templateServices = new ArrayList<TemplateService>(template.getTemplateServices());
      for (TemplateService savedService : mTemplateServices) {
        if (!templateServices.contains(savedService)) {
          templateServices.add(savedService);
        }
      }
      mTemplateServices = templateServices;

      // set name and raw content
      mTemplateName.setText(template.getName());
      getActivity().setTitle(template.getName());
      String content = template.getContent();
      mTemplateContent.setText(content);
      updatePreview();
    }
  }

  private final TextWatcher mTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
      updatePreview();
    }
  };

  private void updatePreview() {
    if (mJavascriptInterface == null) {
      return;
    }
    String markdownContent = mTemplateContent.getText().toString().trim();
    if (mTemplateServices != null && !mTemplateServices.isEmpty()) {
      markdownContent += "\n\n---\n";
      for (TemplateService service : mTemplateServices) {
        markdownContent += TemplatesHelper.getContent(getActivity(), service) + "\n\n";
      }

    }
    mJavascriptInterface.setContent(markdownContent);
    mJavascriptInterface.onLoaded();
  }

  public Template buildTemplate() {
    // build the template
    Template template = new Template();
    if (mTemplateId > 0) {
      template.setId(mTemplateId);
    }
    template.setName(mTemplateName.getText().toString().trim());
    template.setContent(mTemplateContent.getText().toString().trim());
    template.setLastUpdate(System.currentTimeMillis());
    template.setTemplateServices(mTemplateServices);
    return template;
  }

  // TODO fix this!
  public boolean isTemplateValid() {
    if (TextUtils.isEmpty(mTemplateName.getText().toString().trim())) {
      bus.post(new SelectEditorTab());
      mTemplateName.setError(getString(R.string.cover_letter_name_is_empty));
      mTemplateName.requestFocus();
      return false;
    }
    if (TextUtils.isEmpty(mTemplateContent.getText().toString().trim())) {
      bus.post(new SelectEditorTab());
      mTemplateContent.setError(getString(R.string.cover_letter_content_is_empty));
      mTemplateContent.requestFocus();
      return false;
    }
    return true;
  }

  @Subscribe public void saveTemplate(SaveTemplateEvent saveTemplateEvent) {
    if (!isTemplateValid()) {
      return;
    }
    Template template = buildTemplate();
    SqlAdapter adapter = Persistence.getAdapter(getActivity());
    if (template.getId() > 0) {
      String[] args = {String.valueOf(template.getId())};
      int deleted = adapter.delete(TemplateService.class, "template_id = ?", args);
      Log.d(TAG, "Deleted " + deleted + " templates");

      Template where = new Template();
      where.setId(template.getId());
      adapter.update(template, where);

      if (template.getTemplateServices() != null) {
        adapter.storeCollection(template.getTemplateServices(), template, null);
      }
    } else {
      adapter.store(template);
    }
    bus.post(new SaveTemplateDone());
  }

  public void showEditor(boolean showEditor) {
    mShowEditor = showEditor;
    if (mViewSwitcher == null) {
      return;
    }
    mViewSwitcher.setDisplayedChild(showEditor ? EDITOR_MODE : PREVIEW_MODE);
  }

  public void addTemplateService(TemplateService templateService) {
    mTemplateServices.add(templateService);
    updatePreview();
  }

  @Subscribe public void removeServices(DeleteServices deleteServices) {
    mTemplateServices.removeAll(deleteServices.toDelete);
    updatePreview();
    bus.post(new ServicesDeleted(mTemplateServices.isEmpty()));
  }

  @Subscribe public void onRemoveServicesClicked(RemoveServicesClicked removeServicesClicked) {
    // this should never happen but I don't trust anyone, nor even my grandmother
    if (mTemplateServices.isEmpty()) {
      bus.post(new ServicesDeleted(false));
      return;
    }

    // show a dialog to allow users to remove current services
    Fragment fragment = RemoveServicesDialog.newInstance(mTemplateServices);
    getFragmentManager().beginTransaction().add(fragment, RemoveServicesDialog.TAG).commitAllowingStateLoss();
  }
}
