/*
 * Copyright 2014 Some Dev
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ViewSwitcher;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.SOUser;
import com.github.jobs.bean.Template;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.events.ConfigureActionBar;
import com.github.jobs.events.DeleteServices;
import com.github.jobs.events.DeleteTemplate;
import com.github.jobs.events.DisableEditMode;
import com.github.jobs.events.FinishCurrentActivity;
import com.github.jobs.events.SelectEditorTab;
import com.github.jobs.events.ShowTemplateEditor;
import com.github.jobs.templates.TemplatesHelper;
import com.github.jobs.templates.services.StackOverflowService;
import com.github.jobs.templates.services.WebsiteService;
import com.github.jobs.ui.activity.SOUserPickerActivity;
import com.github.jobs.ui.dialog.DeleteTemplateDialog;
import com.github.jobs.ui.dialog.RemoveServicesDialog;
import com.github.jobs.ui.dialog.ServiceChooserDialog;
import com.github.jobs.utils.AppUtils;
import com.github.jobs.utils.GithubJobsJavascriptInterface;
import com.github.jobs.utils.ViewUtils;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import javax.inject.Inject;

import static com.github.jobs.templates.TemplatesHelper.getTemplateFromResult;
import static com.github.jobs.utils.GithubJobsJavascriptInterface.JS_INTERFACE;
import static com.github.jobs.utils.GithubJobsJavascriptInterface.PREVIEW_TEMPLATE_URL;

/**
 * @author cristian
 * @version 1.0
 */
public class EditTemplateFragment extends BusFragment {
  private static final String KEY_TEMPLATE_SERVICES = "com.github.jobs.key.template_services";

  public static final String ARG_TEMPLATE_ID = "com.github.jobs.arg.template_id";

  private static final String KEY_EDIT_MODE = "com.github.jobs.key.edit_mode";
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

  private MenuItem mMenuEditOrSave, mMenuAddService, mMenuRemoveService, mMenuDelete;

  @Inject SqlAdapter adapter;
  @Inject ViewUtils viewUtils;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      mShowEditor = savedInstanceState.getBoolean(KEY_EDIT_MODE);
    }
    setHasOptionsMenu(true);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
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
    outState.putBoolean(KEY_EDIT_MODE, mShowEditor);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // init argument fields
    Bundle arguments = getArguments();
    mTemplateId = arguments.getLong(ARG_TEMPLATE_ID, -1);

    mShowEditor = mShowEditor || mTemplateId == -1;

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
      Template template = new Template();
      template.setId(mTemplateId);
      template = adapter.findFirst(template);
      ArrayList<TemplateService> templateServices =
          new ArrayList<TemplateService>(template.getTemplateServices());
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

  @Override public void onResume() {
    super.onResume();
    if (mShowEditor) {
      enableEditMode();
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.edit_template_menu, menu);
    // get menu actions instance
    mMenuEditOrSave = menu.findItem(R.id.menu_edit_or_save);
    mMenuAddService = menu.findItem(R.id.menu_add_service);
    mMenuDelete = menu.findItem(R.id.menu_delete_template);
    mMenuRemoveService = menu.findItem(R.id.menu_remove_service);

    if (mShowEditor) {
      mMenuEditOrSave.setIcon(R.drawable.ic_action_save);
      mMenuDelete.setVisible(false);
      mMenuAddService.setVisible(true);

      // if we are editing an existing template, let's see if it has services
      showRemoveServiceBtnIfNecessary();
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    switch (itemId) {
      case R.id.menu_edit_or_save:
        if (!mShowEditor) {
          enableEditMode();
          return true;
        }
        saveTemplate();
        break;
      case R.id.menu_delete_template:
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
          // TODO fix this
          new DeleteTemplateDialog().show(fm, DeleteTemplateDialog.TAG);
        }
        break;
      case R.id.menu_add_service:
        Intent serviceChooser = new Intent(getActivity(), ServiceChooserDialog.class);
        startActivityForResult(serviceChooser, ServiceChooserDialog.REQUEST_CODE);
        return true;
      case R.id.menu_remove_service:
        onRemoveServicesClicked();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) {
      return;
    }
    switch (requestCode) {
      case SOUserPickerActivity.REQUEST_CODE:
        if (data == null) {
          // meh... there was no data
          return;
        }
        Parcelable userParcel = data.getParcelableExtra(SOUserPickerActivity.EXTRA_USER);
        if (userParcel instanceof SOUser) {
          SOUser soUser = (SOUser) userParcel;
          // create new cover letter service
          TemplateService soService = new TemplateService();
          soService.setType(StackOverflowService.TYPE);
          soService.setData(soUser.getLink());

          // push cover letter to the fragment holding template information
          addTemplateService(soService);

          // add the website service if possible
          if (soUser.getWebsiteUrl() != null) {
            TemplateService webService = new TemplateService();
            webService.setType(WebsiteService.TYPE);
            webService.setData(soUser.getWebsiteUrl());
            addTemplateService(webService);
          }
          viewUtils.toast(R.string.so_link_added);
        }
        break;
      case ServiceChooserDialog.REQUEST_CODE:
        int serviceId = data.getIntExtra(ServiceChooserDialog.RESULT_SERVICE_ID, -1);
        if (serviceId == -1) {
          Parcelable[] templateServices =
              data.getParcelableArrayExtra(ServiceChooserDialog.RESULT_SERVICES);
          if (templateServices != null) {
            for (Parcelable templateService : templateServices) {
              addTemplateService((TemplateService) templateService);
            }
          }
        } else {
          if (serviceId == R.id.service_so) {
            Intent intent = new Intent(getActivity(), SOUserPickerActivity.class);
            startActivityForResult(intent, SOUserPickerActivity.REQUEST_CODE);
          } else {
            TemplateService templateService = getTemplateFromResult(serviceId, data);
            if (templateService != null) {
              addTemplateService(templateService);
            }
          }
        }
        break;
    }
  }

  @Subscribe public void showEditor(ShowTemplateEditor showTemplateEditor) {
    showEditor(showTemplateEditor.showEditor);
  }

  @Subscribe public void removeServices(DeleteServices deleteServices) {
    mTemplateServices.removeAll(deleteServices.toDelete);
    updatePreview();
    mMenuRemoveService.setVisible(!mTemplateServices.isEmpty());
  }

  @Subscribe public void doDelete(DeleteTemplate deleteTemplate) {
    Template template = new Template();
    template.setId(mTemplateId);
    int delete = adapter.delete(template);
    if (delete > 0) {
      viewUtils.toast(R.string.cover_letter_deleted_successfully);
    }
    bus.post(new FinishCurrentActivity());
  }

  private void onRemoveServicesClicked() {
    // this should never happen but I don't trust anyone, nor even my grandmother
    if (mTemplateServices.isEmpty()) {
      mMenuRemoveService.setVisible(false);
      return;
    }

    // show a dialog to allow users to remove current services
    Fragment fragment = RemoveServicesDialog.newInstance(mTemplateServices);
    getFragmentManager().beginTransaction()
        .add(fragment, RemoveServicesDialog.TAG)
        .commitAllowingStateLoss();
  }

  private void saveTemplate() {
    if (!isTemplateValid()) {
      return;
    }
    Template template = buildTemplate();
    if (template.getId() > 0) {
      String[] args = { String.valueOf(template.getId()) };
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
    bus.post(new FinishCurrentActivity());
  }

  private void showRemoveServiceBtnIfNecessary() {
    if (mTemplateId != -1 && mMenuRemoveService != null) {
      int count = adapter.count(TemplateService.class, "template_id = ?",
          new String[] { String.valueOf(mTemplateId) });
      if (count > 0) {
        mMenuRemoveService.setVisible(true);
      }
    }
  }

  private void enableEditMode() {
    bus.post(new ConfigureActionBar());
    // change action icon
    if (mMenuEditOrSave != null) {
      mMenuEditOrSave.setIcon(R.drawable.ic_action_save);
    }
    mShowEditor = true;
    if (mMenuAddService != null) {
      mMenuAddService.setVisible(true);
    }
    if (mMenuDelete != null) {
      mMenuDelete.setVisible(false);
    }
    showRemoveServiceBtnIfNecessary();
  }

  @SuppressWarnings("UnusedDeclaration")// TODO use it via onBackPressed somehow...
  private void disableEditMode() {
    bus.post(new DisableEditMode());
    showEditor(false);

    // change action icon
    if (mMenuEditOrSave != null) {
      mMenuEditOrSave.setIcon(R.drawable.ic_action_edit);
    }
    mShowEditor = false;
    if (mMenuAddService != null) {
      mMenuAddService.setVisible(false);
    }
    if (mMenuDelete != null) {
      mMenuDelete.setVisible(true);
    }
  }

  private void addTemplateService(TemplateService templateService) {
    mTemplateServices.add(templateService);
    updatePreview();
    // since we added a template service, show the remove action button
    mMenuRemoveService.setVisible(true);
  }

  private void showEditor(boolean showEditor) {
    mShowEditor = showEditor;
    if (mViewSwitcher == null) {
      return;
    }
    mViewSwitcher.setDisplayedChild(showEditor ? EDITOR_MODE : PREVIEW_MODE);
  }

  private boolean isTemplateValid() {
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

  private Template buildTemplate() {
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
}
