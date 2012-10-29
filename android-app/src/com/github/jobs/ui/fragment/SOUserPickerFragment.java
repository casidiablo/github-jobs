package com.github.jobs.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.codeslap.groundy.Groundy;
import com.codeslap.groundy.ReceiverFragment;
import com.github.jobs.R;
import com.github.jobs.adapter.SOUsersAdapter;
import com.github.jobs.bean.SOUser;
import com.github.jobs.resolver.StackOverflowUserResolver;
import com.github.jobs.ui.activity.SOUserPickerActivity;
import com.github.jobs.ui.dialog.SOUserDialog;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * @author cristian
 * @version 1.0
 */
public class SOUserPickerFragment extends SherlockFragment implements AdapterView.OnItemClickListener {

    private SOUserFetcherReceiver mSOUserFetcherReceiver;
    private SOUsersAdapter mAdapter;
    private EditText mUserSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.so_user_picker, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        FragmentManager fm = getFragmentManager();
        mSOUserFetcherReceiver = (SOUserFetcherReceiver) fm.findFragmentByTag(ReceiverFragment.TAG);
        if (mSOUserFetcherReceiver == null) {
            mSOUserFetcherReceiver = new SOUserFetcherReceiver();
            fm.beginTransaction().add(mSOUserFetcherReceiver, ReceiverFragment.TAG).commit();
        }

        mUserSearch = (EditText) getView().findViewById(R.id.edit_user_search);
        mUserSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.KEYCODE_SEARCH) {
                    executeSearch(mUserSearch.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        ListView userList = (ListView) getView().findViewById(R.id.users_list);
        mAdapter = new SOUsersAdapter(getActivity());
        userList.setOnItemClickListener(this);
        userList.setAdapter(mAdapter);

        String search = getActivity().getIntent().getStringExtra(SOUserPickerActivity.EXTRA_SEARCH);
        if (search == null) {
            search = "";
        } else {
            mUserSearch.setText(search);
            mUserSearch.setSelection(search.length());
        }

        executeSearch(search);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.base_search_menu, menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SOUser soUser = mAdapter.getItem(position);
        Intent soUserDialog = new Intent(getActivity(), SOUserDialog.class);
        soUserDialog.putExtra(SOUserPickerActivity.EXTRA_USER, soUser);
        startActivityForResult(soUserDialog, SOUserDialog.REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentActivity activity = getActivity();
        if (activity == null || !isAdded()) {
            return;
        }
        if (requestCode == SOUserDialog.REQUEST_CODE && resultCode == RESULT_OK) {
            activity.setResult(RESULT_OK, data);
            activity.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                executeSearch(mUserSearch.getText().toString().trim());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void executeSearch(String search) {
        Bundle extras = new Bundle();
        extras.putString(StackOverflowUserResolver.EXTRA_SEARCH, search);
        Groundy.execute(getActivity(), StackOverflowUserResolver.class, mSOUserFetcherReceiver.getReceiver(), extras);
    }

    public IBinder getWindowToken() {
        return mUserSearch.getWindowToken();
    }

    public void updateItems(ArrayList<SOUser> users) {
        mAdapter.updateItems(users);
    }
}
