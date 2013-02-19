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

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.github.jobs.resolver.StackOverflowUserTask;
import com.github.jobs.ui.activity.SOUserPickerActivity;
import com.github.jobs.ui.dialog.SOUserDialog;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * @author cristian
 * @version 1.0
 */
public class SOUserPickerFragment extends SherlockFragment implements AdapterView.OnItemClickListener {
    private static final String KEY_USERS = "com.github.jobs.key.users";
    private static final String KEY_SEARCH = "com.github.jobs.key.search";

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
            fm.beginTransaction().add(mSOUserFetcherReceiver, ReceiverFragment.TAG).commitAllowingStateLoss();
        }

        mUserSearch = (EditText) getView().findViewById(R.id.edit_user_search);
        mUserSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    executeSearch(mUserSearch.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        ListView userList = (ListView) getView().findViewById(R.id.users_list);
        mAdapter = new SOUsersAdapter(getActivity());
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_USERS)) {
            ArrayList<Parcelable> items = savedInstanceState.getParcelableArrayList(KEY_USERS);
            ArrayList<SOUser> users = new ArrayList<SOUser>();
            for (Parcelable item : items) {
                users.add((SOUser) item);
            }
            mAdapter.updateItems(users);
        }
        userList.setOnItemClickListener(this);
        userList.setAdapter(mAdapter);

        String search = getActivity().getIntent().getStringExtra(SOUserPickerActivity.EXTRA_SEARCH);
        if (search == null) {
            if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SEARCH)) {
                search = savedInstanceState.getString(KEY_SEARCH);
                mUserSearch.setText(search);
                mUserSearch.setSelection(search.length());
            } else {
                search = "";
            }
        }

        mUserSearch.setText(search);
        mUserSearch.setSelection(search.length());

        if (savedInstanceState == null) {
            // this will execute search only the first time the activity is open
            executeSearch(search);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_USERS, new ArrayList<SOUser>(mAdapter.getItems()));
        outState.putString(KEY_SEARCH, mUserSearch.getText().toString().trim());
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
        extras.putString(StackOverflowUserTask.EXTRA_SEARCH, search);
        Groundy.create(getActivity(), StackOverflowUserTask.class)
                .params(extras)
                .receiver(mSOUserFetcherReceiver.getReceiver())
                .execute();
    }

    public IBinder getWindowToken() {
        return mUserSearch.getWindowToken();
    }

    public void updateItems(ArrayList<SOUser> users) {
        mAdapter.updateItems(users);
    }
}
