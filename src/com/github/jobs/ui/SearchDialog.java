package com.github.jobs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockActivity;
import com.github.jobs.R;

public class SearchDialog extends SherlockActivity implements View.OnClickListener {

    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_FULL_TIME = "full_time";

    private EditText mDescription, mLocation;
    private CheckBox mFullTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_settings);

        mDescription = (EditText) findViewById(R.id.edit_description);
        mLocation = (EditText) findViewById(R.id.edit_location);
        mFullTime = (CheckBox) findViewById(R.id.checkbox_full_time);

        findViewById(R.id.btn_search).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                // validate data
                if (TextUtils.isEmpty(mDescription.getText())) {
                    mDescription.setError(getString(R.string.must_specify_description));
                    return;
                }
                // everything looks ok, let's search
                Intent data = new Intent()
                        .putExtra(EXTRA_DESCRIPTION, mDescription.getText().toString())
                        .putExtra(EXTRA_LOCATION, mLocation.getText().toString())
                        .putExtra(EXTRA_FULL_TIME, mFullTime.isChecked());
                setResult(RESULT_OK, data);
                break;
            case R.id.btn_cancel:
                setResult(RESULT_CANCELED);
                break;
        }
        finish();
    }
}
