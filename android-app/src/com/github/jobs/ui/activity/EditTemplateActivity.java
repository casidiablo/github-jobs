package com.github.jobs.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.Template;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.ui.dialog.DeleteTemplateDialog;
import com.github.jobs.ui.fragment.EditTemplateFragment;
import com.github.jobs.utils.AppUtils;
import com.github.jobs.utils.TabListenerAdapter;

/**
 * @author cristian
 * @version 1.0
 */
public class EditTemplateActivity extends TrackActivity {
    public static final int REQUEST_CODE = 7874;
    private static final String KEY_CURRENT_TAB = "com.github.jobs.key.current_tab";
    private static final String KEY_EDIT_MODE = "com.github.jobs.key.edit_mode";
    public static final String EXTRA_TEMPLATE_ID = "com.github.jobs.extra.template_id";
    private int mCurrentTab;
    private MenuItem mMenuEditOrSave;
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
        if (mEditModeEnabled) {
            mMenuEditOrSave.setIcon(R.drawable.ic_action_save);
            menu.findItem(R.id.menu_delete_template).setVisible(false);
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
                    adapter.delete(TemplateService.class, "template_id = ?", args);

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
        }
        return super.onOptionsItemSelected(item);
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
    }

    private void showEditor(boolean showEditor) {
        EditTemplateFragment fragment = findFragment(EditTemplateFragment.class);
        if (fragment == null || !fragment.isAdded()) {
            return;
        }
        fragment.showEditor(showEditor);
    }
}