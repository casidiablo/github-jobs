package com.github.jobs.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.codeslap.persistence.Persistence;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.Template;
import com.github.jobs.ui.dialog.DeleteTemplateDialog;
import com.github.jobs.ui.fragment.TemplateDetailsFragment;
import com.github.jobs.utils.AppUtils;

import static com.github.jobs.utils.AnalyticsHelper.NAME_EDIT_TEMPLATES;
import static com.github.jobs.utils.AnalyticsHelper.getTracker;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplateDetailsActivity extends TrackActivity {
    public static final String EXTRA_TEMPLATE_ID = "com.github.jobs.extra.templateId";
    private long mTemplateId;
    public static final int TEMPLATE_DETAILS_REQUEST = 6627;
    public static final int DATA_CHANGED = 4994;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTemplateId = getIntent().getLongExtra(EXTRA_TEMPLATE_ID, -1);
        if (mTemplateId == -1) {
            finish();
            Toast.makeText(this, R.string.invalid_template, Toast.LENGTH_LONG).show();
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupBaseFragment(R.id.base_container, TemplateDetailsFragment.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.template_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                AppUtils.goTemplatesList(this);
                return true;
            case R.id.menu_edit:
                getTracker(this).trackPageView(NAME_EDIT_TEMPLATES);
                Intent editTemplate = new Intent(this, EditTemplateActivity.class);
                editTemplate.putExtra(EXTRA_TEMPLATE_ID, mTemplateId);
                startActivityForResult(editTemplate, EditTemplateActivity.EDIT_TEMPLATE_REQUEST);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EditTemplateActivity.EDIT_TEMPLATE_REQUEST && resultCode == RESULT_OK) {
            TemplateDetailsFragment templateDetailsFragment = findFragment(TemplateDetailsFragment.class);
            templateDetailsFragment.onTemplateChanged();
            setResult(DATA_CHANGED);
        }
    }

    public void doDelete() {
        SqlAdapter adapter = Persistence.getAdapter(this);
        Template template = new Template();
        template.setId(mTemplateId);
        int delete = adapter.delete(template);
        if (delete > 0) {
            Toast.makeText(this, R.string.template_deleted_successfully, Toast.LENGTH_LONG).show();
        }

        setResult(DATA_CHANGED);
        finish();
    }
}
