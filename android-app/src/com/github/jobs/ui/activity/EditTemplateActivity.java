package com.github.jobs.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.Template;
import com.github.jobs.ui.fragment.EditTemplateFragment;

/**
 * @author cristian
 * @version 1.0
 */
public class EditTemplateActivity extends TrackActivity {
    public static final int EDIT_TEMPLATE_REQUEST = 7874;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.base_fragment_activity);

        EditTemplateFragment fragment = findEditTemplateFragment();
        if (fragment == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.container, new EditTemplateFragment(), EditTemplateFragment.TAG);
            ft.commit();
        }
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
                EditTemplateFragment fragment = findEditTemplateFragment();
                Template template = fragment.buildTemplate();
                SqlAdapter adapter = Persistence.getAdapter(this);
                if (template.getId() > 0) {
                    Template where = new Template();
                    where.setId(template.getId());
                    adapter.update(template, where);
                } else {
                    adapter.store(template);
                }
                setResult(Activity.RESULT_OK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private EditTemplateFragment findEditTemplateFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(EditTemplateFragment.TAG);
        if (!(fragment instanceof EditTemplateFragment)) {
            return null;
        }
        return (EditTemplateFragment) fragment;
    }
}
