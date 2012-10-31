package com.github.jobs.ui.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import com.codeslap.groundy.DetachableResultReceiver;
import com.codeslap.groundy.Groundy;
import com.github.jobs.R;
import com.github.jobs.adapter.ServicesAdapter;
import com.github.jobs.bean.AboutMeService;
import com.github.jobs.bean.AboutMeUser;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.resolver.AboutMeResolver;
import com.github.jobs.templates.fetcher.AboutMeFetcher;
import com.github.jobs.utils.AppUtils;

import java.util.List;

import static com.github.jobs.templates.TemplatesHelper.*;
import static com.github.jobs.templates.fetcher.AboutMeFetcher.AboutMeServicesCallback;

/**
 * @author cristian
 * @version 1.0
 */
public class ServiceChooserDialog extends TrackDialog implements AdapterView.OnItemClickListener, View.OnClickListener,
        DetachableResultReceiver.Receiver {
    public static final String RESULT_SERVICE_ID = "com.github.jobs.result.service_id";
    public static final String RESULT_SERVICE_DATA = "com.github.jobs.result.service_data";
    public static final String RESULT_SERVICES = "com.github.jobs.result.services";

    public static final int REQUEST_CODE = 623;

    private static final int SERVICE_CHOOSER = 0;
    private static final int SERVICE_DATA_RETRIEVAL = 1;
    private static final int SERVICE_LOADING = 2;
    private static final int SERVICE_CONFIRMATION = 3;

    private ViewFlipper mServicesFlipper;
    private ImageView mServiceImage;
    private EditText mServiceData;
    private Button mFetchServiceInfo;
    private FrameLayout mConfirmationContainer;

    /**
     * State held between configuration changes.
     */
    private State mState;
    private DetachableResultReceiver mDetachableReceiver = new DetachableResultReceiver(new Handler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_chooser_dialog);

        // initialize widgets references
        mServicesFlipper = (ViewFlipper) findViewById(R.id.flipper_choose_service);
        mServiceImage = (ImageView) findViewById(R.id.img_current_service);
        mServiceData = (EditText) findViewById(R.id.edit_service_data);
        mFetchServiceInfo = (Button) findViewById(R.id.btn_fetch_service_info);
        mConfirmationContainer = (FrameLayout) findViewById(R.id.service_confirmation_container);
        GridView grid = (GridView) findViewById(R.id.grid_services);

        // restore last state if any
        mState = (State) getLastNonConfigurationInstance();
        final boolean previousState = mState != null;
        if (previousState) {
            mState.receiver.setReceiver(this);
        } else {
            mState = new State();
            mState.receiver.setReceiver(this);
        }
        switch (mState.currentViewSwitcherView) {
            case SERVICE_DATA_RETRIEVAL:
                // update the views to show the service data retrieval form
                showServiceDataRetrieval();
                break;
            case SERVICE_CONFIRMATION:
                // let's try to recreate the confirmation view
                setupConfirmationView();
                break;
            default:
                mServicesFlipper.setDisplayedChild(mState.currentViewSwitcherView);
        }

        // prepare the services grid
        ServicesAdapter adapter = new ServicesAdapter(this);
        adapter.updateItems(getServices(this));
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(this);

        // setup click listeners
        mFetchServiceInfo.setOnClickListener(this);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        // Clear any strong references to this Activity, we'll reattach to handle events on the other side.
        mState.receiver.clearReceiver();
        mState.currentViewSwitcherView = mServicesFlipper.getDisplayedChild();
        return mState;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mState.lastService = (int) id;

        // if it was stack overflow, finish this activity and pass info to the parent
        // activity so that it can launch the pick stackoverflow user activity
        if (mState.lastService == R.id.service_so) {
            setResultAndFinish();
            return;
        }

        // update view so that we can retrieve info for the current service
        showServiceDataRetrieval();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fetch_service_info:
                if (!isServiceDataValid()) {
                    return;
                }
                fetchServicePayload(mServiceData.getText().toString().trim());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDetachableReceiver.clearReceiver();
    }

    @Override
    public void onBackPressed() {
        if (mServicesFlipper.getDisplayedChild() == SERVICE_CONFIRMATION) {
            mServicesFlipper.setDisplayedChild(SERVICE_DATA_RETRIEVAL);
        } else if (mServicesFlipper.getDisplayedChild() == SERVICE_LOADING) {
            // TODO stop groundy task
            mDetachableReceiver.clearReceiver();
            mServicesFlipper.setDisplayedChild(SERVICE_DATA_RETRIEVAL);
        } else if (mServicesFlipper.getDisplayedChild() == SERVICE_DATA_RETRIEVAL) {
            mServicesFlipper.setDisplayedChild(SERVICE_CHOOSER);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case Groundy.STATUS_FINISHED:
                setupPayload(resultData);
                break;
            case Groundy.STATUS_ERROR:
                mServicesFlipper.setDisplayedChild(SERVICE_DATA_RETRIEVAL);
                Toast.makeText(ServiceChooserDialog.this, R.string.nothing_found, Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Configures data retrieval form using the current service
     */
    private void showServiceDataRetrieval() {
        mFetchServiceInfo.setText(getAddServiceButtonLabel(mState.lastService));
        mServiceData.setHint(getHint(mState.lastService));
        mServiceImage.setImageResource(getServiceDrawable(mState.lastService));
        mServicesFlipper.setDisplayedChild(SERVICE_DATA_RETRIEVAL);
    }

    /**
     * Starts the current service payload fetching
     *
     * @param data used to retrieve service payload
     */
    private void fetchServicePayload(String data) {
        switch (mState.lastService) {
            case R.id.service_github:
            case R.id.service_skype:
            case R.id.service_linked_in:
                mState.servicePayload = data;
                setResultAndFinish();
                return;
            case R.id.service_about_me:
                Bundle extras = new Bundle();
                extras.putString(AboutMeResolver.PARAM_USERNAME, data);
                Groundy.execute(this, AboutMeResolver.class, mState.receiver, extras);
                AppUtils.hideKeyboard(this, mServiceData.getWindowToken());
                mServicesFlipper.setDisplayedChild(SERVICE_LOADING);
                break;
        }
    }

    /**
     * Setups state's payload for the current service
     *
     * @param resultData data returned by the service fetcher resolver
     */
    private void setupPayload(Bundle resultData) {
        switch (mState.lastService) {
            case R.id.service_about_me:
                mState.servicePayload = resultData.getParcelable(AboutMeResolver.RESULT_USER);
                setupConfirmationView();
                break;
        }
    }

    /**
     * Configures the confirmation view depending on the current service and its payload
     */
    private void setupConfirmationView() {
        switch (mState.lastService) {
            case R.id.service_about_me:
                if (mState.servicePayload instanceof AboutMeUser) {
                    AboutMeUser aboutMeUser = (AboutMeUser) mState.servicePayload;
                    AboutMeFetcher.setupConfirmationView(ServiceChooserDialog.this, mConfirmationContainer,
                            aboutMeUser, aboutMeCallback);
                    mServicesFlipper.setDisplayedChild(SERVICE_CONFIRMATION);
                }
                break;
        }
    }

    /**
     * Checks whether current service's data is correct
     *
     * @return true if data is correct
     */
    private boolean isServiceDataValid() {
        if (TextUtils.isEmpty(mServiceData.getText().toString().trim())) {
            mServiceData.setError(getString(R.string.this_shall_not_be_empty));
            mServiceData.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * State specific to {@link ServiceChooserDialog} that is held between configuration
     * changes. Any strong {@link android.app.Activity} references <strong>must</strong> be
     * cleared before {@link #onRetainNonConfigurationInstance()}, and this
     * class should remain {@code static class}.
     */
    private static class State {
        DetachableResultReceiver receiver;
        int currentViewSwitcherView = SERVICE_CHOOSER;
        int lastService;

        // this object will change depending on the last service used
        // it will contain the service payload information that is used
        // later in the confirmation screen.
        Object servicePayload;

        private State() {
            receiver = new DetachableResultReceiver(new Handler());
        }

    }

    /**
     * Finishes the activity and puts the last service into the result
     */
    private void setResultAndFinish() {
        Intent data = new Intent();
        data.putExtra(ServiceChooserDialog.RESULT_SERVICE_ID, mState.lastService);
        data.putExtra(ServiceChooserDialog.RESULT_SERVICE_DATA, String.valueOf(mState.servicePayload));
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * this will be called from within the about.me confirmation view to notify
     * that user has selected a set of services to include to the cover letter template
     */
    private final AboutMeServicesCallback aboutMeCallback = new AboutMeServicesCallback() {
        @Override
        public void onServicesSelected(List<AboutMeService> services) {
            Intent data = new Intent();
            TemplateService[] templateServices = new TemplateService[services.size()];
            for (int i = 0, servicesSize = services.size(); i < servicesSize; i++) {
                AboutMeService service = services.get(i);
                TemplateService templateService = new TemplateService();
                templateService.setType(service.getDisplayName());
                templateService.setData(service.getServiceUrl());
                templateServices[i] = templateService;
            }
            data.putExtra(ServiceChooserDialog.RESULT_SERVICES, templateServices);
            setResult(RESULT_OK, data);
            finish();
        }
    };
}
