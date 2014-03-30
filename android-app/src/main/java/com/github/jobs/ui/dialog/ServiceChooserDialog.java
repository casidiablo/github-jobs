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

package com.github.jobs.ui.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.github.jobs.R;
import com.github.jobs.adapter.ServicesAdapter;
import com.github.jobs.bean.AboutMeService;
import com.github.jobs.bean.AboutMeUser;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.resolver.AboutMeTask;
import com.github.jobs.templates.fetcher.AboutMeFetcher;
import com.github.jobs.utils.AppUtils;
import com.telly.groundy.CallbacksManager;
import com.telly.groundy.Groundy;
import com.telly.groundy.annotations.OnFailure;
import com.telly.groundy.annotations.OnSuccess;
import com.telly.groundy.annotations.Param;
import java.util.List;

import static com.github.jobs.templates.TemplatesHelper.getAddServiceButtonLabel;
import static com.github.jobs.templates.TemplatesHelper.getHint;
import static com.github.jobs.templates.TemplatesHelper.getServiceDrawable;
import static com.github.jobs.templates.TemplatesHelper.getServices;
import static com.github.jobs.templates.fetcher.AboutMeFetcher.AboutMeServicesCallback;

/**
 * @author cristian
 * @version 1.0
 */
public class ServiceChooserDialog extends TrackDialog
    implements AdapterView.OnItemClickListener, View.OnClickListener {
  public static final String RESULT_SERVICE_ID = "com.github.jobs.result.service_id";
  public static final String RESULT_SERVICE_TYPE = "com.github.jobs.result.service_type";
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
  private EditText mServiceType;
  private Button mFetchServiceInfo;
  private FrameLayout mConfirmationContainer;

  /** State held between configuration changes. */
  private CallbacksManager callbacksManager;
  private State mState;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.service_chooser_dialog);

    // initialize widgets references
    mServicesFlipper = (ViewFlipper) findViewById(R.id.flipper_choose_service);
    mServiceImage = (ImageView) findViewById(R.id.img_current_service);
    mServiceData = (EditText) findViewById(R.id.edit_service_data);
    mServiceType = (EditText) findViewById(R.id.edit_service_type);
    mFetchServiceInfo = (Button) findViewById(R.id.btn_fetch_service_info);
    mConfirmationContainer = (FrameLayout) findViewById(R.id.service_confirmation_container);
    GridView grid = (GridView) findViewById(R.id.grid_services);

    // restore last state if any
    mState = (State) getLastNonConfigurationInstance();
    final boolean previousState = mState != null;
    if (!previousState) {
      mState = new State();
    }
    callbacksManager = CallbacksManager.init(savedInstanceState, this);
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

  @Override public Object onRetainNonConfigurationInstance() {
    // Clear any strong references to this Activity, we'll reattach to handle events on the other side.
    mState.currentViewSwitcherView = mServicesFlipper.getDisplayedChild();
    return mState;
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    callbacksManager.onSaveInstanceState(outState);
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    mState.lastService = (int) id;

    // if it was stack overflow, finish this activity and pass info to the parent
    // activity so that it can launch the pick stackoverflow user activity
    if (mState.lastService == R.id.service_so) {
      if (!AppUtils.isOnline(this)) {
        Toast.makeText(this, R.string.you_must_have_network_connection, Toast.LENGTH_SHORT).show();
        return;
      }
      setResultAndFinish();
      return;
    }

    // update view so that we can retrieve info for the current service
    showServiceDataRetrieval();
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_fetch_service_info:
        if (!isServiceDataValid()) {
          return;
        }
        fetchServicePayload(mServiceData.getText().toString().trim());
        break;
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    callbacksManager.onDestroy();
  }

  @Override public void onBackPressed() {
    if (mServicesFlipper.getDisplayedChild() == SERVICE_CONFIRMATION) {
      mServicesFlipper.setDisplayedChild(SERVICE_DATA_RETRIEVAL);
    } else if (mServicesFlipper.getDisplayedChild() == SERVICE_LOADING) {
      // TODO stop Groundy task
      mServicesFlipper.setDisplayedChild(SERVICE_DATA_RETRIEVAL);
    } else if (mServicesFlipper.getDisplayedChild() == SERVICE_DATA_RETRIEVAL) {
      mServicesFlipper.setDisplayedChild(SERVICE_CHOOSER);
    } else {
      super.onBackPressed();
    }
  }

  @OnFailure(AboutMeTask.class) public void onAboutMeFailed() {
    mServicesFlipper.setDisplayedChild(SERVICE_DATA_RETRIEVAL);
    Toast.makeText(ServiceChooserDialog.this, R.string.nothing_found, Toast.LENGTH_LONG).show();
  }

  /** Configures data retrieval form using the current service */
  private void showServiceDataRetrieval() {
    mFetchServiceInfo.setText(getAddServiceButtonLabel(mState.lastService));
    mServiceType.setVisibility(
        mState.lastService == R.id.service_custom ? View.VISIBLE : View.GONE);
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
      case R.id.service_custom:
        mState.serviceType = mServiceType.getText().toString().trim();
        mState.servicePayload = data;
        setResultAndFinish();
        return;
      case R.id.service_github:
      case R.id.service_skype:
      case R.id.service_linked_in:
        mState.serviceType = null;
        mState.servicePayload = data;
        setResultAndFinish();
        return;
      case R.id.service_about_me:
        Groundy.create(AboutMeTask.class)
            .callback(this)
            .callbackManager(callbacksManager)
            .arg(AboutMeTask.PARAM_USERNAME, data)
            .executeUsing(this);
        AppUtils.hideKeyboard(this, mServiceData.getWindowToken());
        mServicesFlipper.setDisplayedChild(SERVICE_LOADING);
        break;
    }
  }

  /** Setups state's payload for the current service */
  @OnSuccess(AboutMeTask.class)
  public void setupPayload(@Param(AboutMeTask.RESULT_USER) Parcelable payload) {
    switch (mState.lastService) {
      case R.id.service_about_me:
        mState.servicePayload = payload;
        setupConfirmationView();
        break;
    }
  }

  /** Configures the confirmation view depending on the current service and its payload */
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
    if (mState.lastService == R.id.service_custom && TextUtils.isEmpty(
        mServiceType.getText().toString().trim())) {
      mServiceType.setError(getString(R.string.this_shall_not_be_empty));
      mServiceType.requestFocus();
      return false;
    }
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
    int currentViewSwitcherView = SERVICE_CHOOSER;
    int lastService;

    // this object will change depending on the last service used
    // it will contain the service payload information that is used
    // later in the confirmation screen.
    Object servicePayload;
    String serviceType;
  }

  /** Finishes the activity and puts the last service into the result */
  private void setResultAndFinish() {
    Intent data = new Intent();
    data.putExtra(ServiceChooserDialog.RESULT_SERVICE_ID, mState.lastService);
    data.putExtra(ServiceChooserDialog.RESULT_SERVICE_TYPE, String.valueOf(mState.serviceType));
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
