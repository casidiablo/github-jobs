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

package com.github.jobs.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.SOUser;
import com.github.jobs.bean.Template;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.events.SaveTemplateDone;
import com.github.jobs.events.SaveTemplateEvent;
import com.github.jobs.events.SelectEditorTab;
import com.github.jobs.templates.services.StackOverflowService;
import com.github.jobs.templates.services.WebsiteService;
import com.github.jobs.ui.dialog.DeleteTemplateDialog;
import com.github.jobs.ui.dialog.RemoveServicesDialog;
import com.github.jobs.ui.dialog.ServiceChooserDialog;
import com.github.jobs.ui.fragment.EditTemplateFragment;
import com.github.jobs.utils.AppUtils;
import com.github.jobs.utils.TabListenerAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import static com.github.jobs.templates.TemplatesHelper.getTemplateFromResult;

/**
 * @author cristian
 * @version 1.0
 */
public class EditTemplateActivity extends TrackActivity {
  private static final String TAG = "github:jobs:templates";

  // used to start this activity with startActivityForResult
  public static final int REQUEST_CODE = 7874;

  // used to save the state of this activity
  private static final String KEY_CURRENT_TAB = "com.github.jobs.key.current_tab";
  private static final String KEY_EDIT_MODE = "com.github.jobs.key.edit_mode";

  // used to pass a template id to edit
  public static final String EXTRA_TEMPLATE_ID = "com.github.jobs.extra.template_id";

  private int mCurrentTab;
  private MenuItem mMenuEditOrSave, mMenuAddService, mMenuRemoveService, mMenuDelete;
  private boolean mEditModeEnabled;
  private long mTemplateId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    if (savedInstanceState != null) {
      mCurrentTab = savedInstanceState.getInt(KEY_CURRENT_TAB);
      mEditModeEnabled = savedInstanceState.getBoolean(KEY_EDIT_MODE);
    }

    mTemplateId = getIntent().getLongExtra(EditTemplateActivity.EXTRA_TEMPLATE_ID, -1);
    boolean enableEditMode = mEditModeEnabled || mTemplateId == -1;
    if (enableEditMode) {
      enableEditMode();
    }

    Bundle args = new Bundle();
    args.putLong(EditTemplateFragment.ARG_TEMPLATE_ID, mTemplateId);
    args.putBoolean(EditTemplateFragment.ARG_EDIT_MODE, enableEditMode);
    setupBaseFragment(R.id.base_container, EditTemplateFragment.class, args);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    ActionBar.Tab selectedTab = getSupportActionBar().getSelectedTab();
    if (selectedTab != null) {
      mCurrentTab = selectedTab.getPosition();
      outState.putInt(KEY_CURRENT_TAB, mCurrentTab);
      outState.putBoolean(KEY_EDIT_MODE, mEditModeEnabled);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getSupportMenuInflater().inflate(R.menu.edit_template_menu, menu);
    // get menu actions instance
    mMenuEditOrSave = menu.findItem(R.id.menu_edit_or_save);
    mMenuAddService = menu.findItem(R.id.menu_add_service);
    mMenuDelete = menu.findItem(R.id.menu_delete_template);
    mMenuRemoveService = menu.findItem(R.id.menu_remove_service);

    if (mEditModeEnabled) {
      mMenuEditOrSave.setIcon(R.drawable.ic_action_save);
      mMenuDelete.setVisible(false);
      mMenuAddService.setVisible(true);

      // if we are editing an existing template, let's see if it has services
      showRemoveServiceBtnIfNecessary();
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    switch (itemId) {
      case android.R.id.home:
        if (mEditModeEnabled && mTemplateId != -1) {
          disableEditMode();
        } else {
          AppUtils.goTemplatesList(this);
        }
        return true;
      case R.id.menu_edit_or_save:
        if (!mEditModeEnabled) {
          enableEditMode();
          return true;
        }
        bus.post(new SaveTemplateEvent());
        break;
      case R.id.menu_delete_template:
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
          new DeleteTemplateDialog().show(fm, DeleteTemplateDialog.TAG);
        }
        break;
      case R.id.menu_add_service:
        Intent serviceChooser = new Intent(this, ServiceChooserDialog.class);
        startActivityForResult(serviceChooser, ServiceChooserDialog.REQUEST_CODE);
        return true;
      case R.id.menu_remove_service:
        EditTemplateFragment editTemplateFragment = findFragment(EditTemplateFragment.class);
        if (editTemplateFragment != null) {
          // this should never happen but I don't trust anyone, nor even my grandmother
          if (editTemplateFragment.getTemplateServices().isEmpty()) {
            if (mMenuRemoveService != null) {
              mMenuRemoveService.setVisible(false);
            }
            return true;
          }

          // create an bundle with the current services
          ArrayList<TemplateService> templateServices = editTemplateFragment.getTemplateServices();
          Bundle args = new Bundle();
          args.putParcelableArrayList(RemoveServicesDialog.ARG_SERVICES, templateServices);

          // show a dialog to allow users to remove current services
          RemoveServicesDialog dialog = new RemoveServicesDialog();
          dialog.setArguments(args);
          getSupportFragmentManager().beginTransaction().add(dialog, RemoveServicesDialog.TAG).commitAllowingStateLoss();
        }
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
          if (!addTemplateService(soService)) return;

          // add the website service if possible
          if (soUser.getWebsiteUrl() != null) {
            TemplateService webService = new TemplateService();
            webService.setType(WebsiteService.TYPE);
            webService.setData(soUser.getWebsiteUrl());
            if (!addTemplateService(webService)) return;
          }
          Toast.makeText(this, getString(R.string.so_link_added), Toast.LENGTH_LONG).show();
        }
        break;
      case ServiceChooserDialog.REQUEST_CODE:
        int serviceId = data.getIntExtra(ServiceChooserDialog.RESULT_SERVICE_ID, -1);
        if (serviceId == -1) {
          Parcelable[] templateServices = data.getParcelableArrayExtra(ServiceChooserDialog.RESULT_SERVICES);
          if (templateServices != null) {
            for (Parcelable templateService : templateServices) {
              addTemplateService((TemplateService) templateService);
            }
          }
        } else {
          if (serviceId == R.id.service_so) {
            Intent intent = new Intent(this, SOUserPickerActivity.class);
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

  public void removeServices(ArrayList<TemplateService> services) {
    EditTemplateFragment editTemplateFragment = findFragment(EditTemplateFragment.class);
    if (editTemplateFragment != null) {
      editTemplateFragment.removeServices(services);
      mMenuRemoveService.setVisible(!editTemplateFragment.getTemplateServices().isEmpty());
    }
  }

  private void showRemoveServiceBtnIfNecessary() {
    if (mTemplateId != -1) {
      SqlAdapter adapter = Persistence.getAdapter(this);
      int count = adapter.count(TemplateService.class, "template_id = ?", new String[]{String.valueOf(mTemplateId)});
      if (count > 0) {
        mMenuRemoveService.setVisible(true);
      }
    }
  }

  private boolean addTemplateService(TemplateService service) {
    EditTemplateFragment fragment = findFragment(EditTemplateFragment.class);
    if (fragment == null) {
      Log.wtf(TAG, "Fragment shall not be null here", new RuntimeException());
      return false;
    }
    fragment.addTemplateService(service);
    // since we added a template service, show the remove action button
    mMenuRemoveService.setVisible(true);
    return true;
  }

  public void doDelete() {
    SqlAdapter adapter = Persistence.getAdapter(this);
    Template template = new Template();
    template.setId(mTemplateId);
    int delete = adapter.delete(template);
    if (delete > 0) {
      Toast.makeText(this, R.string.cover_letter_deleted_successfully, Toast.LENGTH_LONG).show();
    }
    setResult(RESULT_OK);
    finish();
  }

  private void enableEditMode() {
    // let's create edit tabs!
    ActionBar actionBar = getSupportActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    ActionBar.Tab editorTab = actionBar.newTab();
    editorTab.setText(R.string.lbl_editor);
    editorTab.setTabListener(new TabListenerAdapter() {
      @Override
      public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        showEditor(true);
      }
    });

    ActionBar.Tab previewTab = actionBar.newTab();
    previewTab.setText(R.string.preview);
    previewTab.setTabListener(new TabListenerAdapter() {
      @Override
      public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        showEditor(false);
      }
    });

    // add tabs to the activity and select the current tab
    actionBar.addTab(editorTab);
    actionBar.addTab(previewTab);
    actionBar.getTabAt(mCurrentTab).select();

    // change action icon
    if (mMenuEditOrSave != null) {
      mMenuEditOrSave.setIcon(R.drawable.ic_action_save);
    }
    mEditModeEnabled = true;
    setTitle(mTemplateId != -1 ? R.string.edit_cover_letter : R.string.new_cover_letter);
    if (mMenuAddService != null) {
      mMenuAddService.setVisible(true);
    }
    if (mMenuDelete != null) {
      mMenuDelete.setVisible(false);
    }
    showRemoveServiceBtnIfNecessary();
  }

  private void disableEditMode() {
    // let's create edit tabs!
    ActionBar actionBar = getSupportActionBar();
    actionBar.removeAllTabs();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    showEditor(false);

    // change action icon
    if (mMenuEditOrSave != null) {
      mMenuEditOrSave.setIcon(R.drawable.ic_action_edit);
    }
    mEditModeEnabled = false;
    if (mMenuAddService != null) {
      mMenuAddService.setVisible(false);
    }
    if (mMenuDelete != null) {
      mMenuDelete.setVisible(true);
    }
  }

  @Subscribe public void selectEditorTab(SelectEditorTab selectEditorTab) {
    if (getSupportActionBar().getSelectedTab().getPosition() != 0) {
      getSupportActionBar().getTabAt(0).select();
    }
  }

  @Subscribe public void onSaveTemplateDone(SaveTemplateDone saveTemplateDone) {
    setResult(RESULT_OK);
    finish();
  }

  private void showEditor(boolean showEditor) {
    EditTemplateFragment fragment = findFragment(EditTemplateFragment.class);
    if (fragment == null || !fragment.isAdded()) {
      return;
    }
    fragment.showEditor(showEditor);
  }
}