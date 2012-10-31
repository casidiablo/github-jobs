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
import com.github.jobs.templates.services.StackOverflowService;
import com.github.jobs.templates.services.WebsiteService;
import com.github.jobs.ui.dialog.DeleteTemplateDialog;
import com.github.jobs.ui.dialog.ServiceChooserDialog;
import com.github.jobs.ui.fragment.EditTemplateFragment;
import com.github.jobs.utils.AppUtils;
import com.github.jobs.utils.TabListenerAdapter;

import static com.github.jobs.templates.TemplatesHelper.getTemplateFromResult;
import static com.github.jobs.templates.TemplatesHelper.resolve;

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
    private MenuItem mMenuEditOrSave;
    private MenuItem mMenuAddService;
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
        mMenuEditOrSave = menu.findItem(R.id.menu_edit_or_save);
        mMenuAddService = menu.findItem(R.id.menu_add_service);
        if (mEditModeEnabled) {
            mMenuEditOrSave.setIcon(R.drawable.ic_action_save);
            menu.findItem(R.id.menu_delete_template).setVisible(false);
            mMenuAddService.setVisible(true);
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

                EditTemplateFragment fragment = findFragment(EditTemplateFragment.class);
                if (!fragment.isTemplateValid()) {
                    return true;
                }
                Template template = fragment.buildTemplate();
                SqlAdapter adapter = Persistence.getAdapter(this);
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
                setResult(RESULT_OK);
                finish();
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
                    EditTemplateFragment fragment = findFragment(EditTemplateFragment.class);
                    if (fragment == null) {
                        Log.wtf(TAG, "Fragment shall not be null here", new RuntimeException());
                        return;
                    }
                    fragment.addTemplateService(soService);

                    // add the website service if possible
                    if (soUser.getWebsiteUrl() != null) {
                        TemplateService webService = new TemplateService();
                        webService.setType(WebsiteService.TYPE);
                        webService.setData(soUser.getWebsiteUrl());
                        fragment.addTemplateService(webService);
                    }
                    Toast.makeText(this, getString(R.string.so_link_added), Toast.LENGTH_LONG).show();
                }
                break;
            case ServiceChooserDialog.REQUEST_CODE:
                int serviceId = data.getIntExtra(ServiceChooserDialog.RESULT_SERVICE_ID, -1);
                if (serviceId == -1) {
                    Parcelable[] templateServices = data.getParcelableArrayExtra(ServiceChooserDialog.RESULT_SERVICES);
                    if (templateServices != null) {
                        EditTemplateFragment fragment = findFragment(EditTemplateFragment.class);
                        for (Parcelable templateService : templateServices) {
                            fragment.addTemplateService((TemplateService) templateService);
                        }
                    }
                } else {
                    TemplateService templateService = getTemplateFromResult(serviceId, data);
                    if (templateService != null) {
                        EditTemplateFragment fragment = findFragment(EditTemplateFragment.class);
                        fragment.addTemplateService(templateService);
                    } else {
                        // we could not build a template from the result... let's try by using
                        // resolve method that will launch a retrieval activity if necessary
                        resolve(this, serviceId);
                    }
                }
                break;
        }
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
    }

    public void selectEditorTab() {
        if (getSupportActionBar().getSelectedTab().getPosition() != 0) {
            getSupportActionBar().getTabAt(0).select();
        }
    }

    private void showEditor(boolean showEditor) {
        EditTemplateFragment fragment = findFragment(EditTemplateFragment.class);
        if (fragment == null || !fragment.isAdded()) {
            return;
        }
        fragment.showEditor(showEditor);
    }
}