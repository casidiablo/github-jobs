package com.github.jobs.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.Template;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.ui.fragment.EditTemplateFragment;
import com.github.jobs.utils.TabListenerAdapter;

/**
 * @author cristian
 * @version 1.0
 */
public class EditTemplateActivity extends TrackActivity {
    public static final int EDIT_TEMPLATE_REQUEST = 7874;
    private static final String KEY_CURRENT_TAB = "com.github.jobs.key.current_tab";
    private int mCurrentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentTab = savedInstanceState.getInt(KEY_CURRENT_TAB);
        }
        setupActionBar();
        setupBaseFragment(R.id.base_container, EditTemplateFragment.class);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCurrentTab = getSupportActionBar().getSelectedTab().getPosition();
        outState.putInt(KEY_CURRENT_TAB, mCurrentTab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.edit_template_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_save:
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
                setResult(Activity.RESULT_OK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab editorTab = getSupportActionBar().newTab();
        editorTab.setText(R.string.lbl_editor);
        editorTab.setTabListener(new TabListenerAdapter() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                showEditor(true);
            }
        });

        ActionBar.Tab previewTab = getSupportActionBar().newTab();
        previewTab.setText(R.string.preview);
        previewTab.setTabListener(new TabListenerAdapter() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                showEditor(false);
            }
        });

        getSupportActionBar().addTab(editorTab);
        getSupportActionBar().addTab(previewTab);
        getSupportActionBar().getTabAt(mCurrentTab).select();
    }

    private void showEditor(boolean showEditor) {
        EditTemplateFragment fragment = findFragment(EditTemplateFragment.class);
        if (fragment == null || !fragment.isAdded()) {
            return;
        }
        fragment.showEditor(showEditor);
    }
}