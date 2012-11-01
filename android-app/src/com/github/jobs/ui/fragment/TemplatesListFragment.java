package com.github.jobs.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.github.jobs.R;
import com.github.jobs.adapter.TemplatesAdapter;
import com.github.jobs.bean.Template;
import com.github.jobs.loader.TemplatesLoader;
import com.github.jobs.ui.activity.EditTemplateActivity;
import com.github.jobs.ui.activity.LoginActivity;
import com.github.jobs.ui.activity.TemplatesActivity;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.github.jobs.ui.activity.EditTemplateActivity.REQUEST_CODE;
import static com.github.jobs.utils.AnalyticsHelper.NAME_EDIT_TEMPLATES;
import static com.github.jobs.utils.AnalyticsHelper.getTracker;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplatesListFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<List<Template>> {
    private static final int TEMPLATES_LOADER_ID = 8432;
    private TemplatesAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new TemplatesAdapter(getActivity());
        setListAdapter(mAdapter);
        queryList();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FragmentActivity activity = getActivity();
        if (!(activity instanceof TemplatesActivity)) {
            return;
        }
        if (isPickMode()) {
            TemplatesActivity templatesActivity = (TemplatesActivity) activity;
            templatesActivity.setTemplateResultAndFinish(id);
        } else {
            Intent templateDetails = new Intent(activity, EditTemplateActivity.class);
            templateDetails.putExtra(EditTemplateActivity.EXTRA_TEMPLATE_ID, id);
            startActivityForResult(templateDetails, EditTemplateActivity.REQUEST_CODE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.templates_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_template:
                getTracker(getActivity()).trackPageView(NAME_EDIT_TEMPLATES);
                Intent editTemplate = new Intent(getActivity(), EditTemplateActivity.class);
                startActivityForResult(editTemplate, REQUEST_CODE);
                break;
            case R.id.menu_cloud:
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            queryList();
        }
    }

    @Override
    public Loader<List<Template>> onCreateLoader(int id, Bundle args) {
        FragmentActivity activity = getActivity();
        if (activity == null || !isAdded()) {
            return null;
        }
        return new TemplatesLoader(activity);
    }

    @Override
    public void onLoadFinished(Loader<List<Template>> loader, List<Template> data) {
        mAdapter.updateItems(data);
        if (data.isEmpty()) {
            setEmptyText(getString(R.string.empty_cover_letters_list));
        } else {
            setEmptyText(null);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Template>> loader) {
        mAdapter.clear();
    }

    private void queryList() {
        try {
            FragmentActivity activity = getActivity();
            if (activity == null || !isAdded()) {
                return;
            }
            LoaderManager loaderManager = activity.getSupportLoaderManager();
            Loader<Object> loader = loaderManager.getLoader(TEMPLATES_LOADER_ID);
            if (loader == null) {
                loaderManager.initLoader(TEMPLATES_LOADER_ID, null, this);
            } else {
                loaderManager.restartLoader(TEMPLATES_LOADER_ID, null, this);
            }
        } catch (IllegalStateException e) {
            // happens when activity is closed. We can't use isResumed since it will be false when the activity is
            // not being shown, thus it will cause problems if user loads another screen while this is still loading
        }
    }

    private boolean isPickMode() {
        return getActivity().getIntent().getBooleanExtra(TemplatesActivity.EXTRA_PICK, false);
    }
}
