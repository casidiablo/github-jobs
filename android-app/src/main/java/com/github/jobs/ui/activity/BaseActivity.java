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
package com.github.jobs.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import com.github.jobs.GithubJobsApplication;
import com.squareup.otto.Bus;
import javax.inject.Inject;

public abstract class BaseActivity extends FragmentActivity {
  private boolean mSetContentViewAlreadyCalled;
  @Inject Bus bus;

  @Override protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    super.onCreate(savedInstanceState);
    ((GithubJobsApplication) getApplication()).inject(this);
  }

  @Override public void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override public void onPause() {
    super.onPause();
    bus.unregister(this);
  }

  @Override public void setContentView(int layoutResId) {
    super.setContentView(layoutResId);
    onSetContentView();
  }

  @Override public void setContentView(View view) {
    super.setContentView(view);
    onSetContentView();
  }

  @Override public void setContentView(View view, ViewGroup.LayoutParams params) {
    super.setContentView(view, params);
    onSetContentView();
  }

  private void onSetContentView() {
    setProgressBarIndeterminateVisibility(false);
    mSetContentViewAlreadyCalled = true;
  }

  /**
   * Helper method that allows to initialize and add a fragment to activities that usually have
   * just one single fragment. Fragment is added using its class.getName() as tag.
   *
   * @param containerId resource id of the fragment container (must be created through android
   * resources)
   * @param fragmentClass the class of the fragment to setup
   */
  void setupBaseFragment(int containerId, Class<? extends Fragment> fragmentClass) {
    setupBaseFragment(containerId, fragmentClass, null);
  }

  /**
   * Helper method that allows to initialize and add a fragment to activities that usually have
   * just one single fragment. Fragment is added using its class.getName() as tag.
   *
   * @param containerId resource id of the fragment container (must be created through android
   * resources)
   * @param fragmentClass the class of the fragment to setup
   * @param args bundle with the arguments to pass to the fragment
   */
  void setupBaseFragment(int containerId, Class<? extends Fragment> fragmentClass, Bundle args) {
    if (mSetContentViewAlreadyCalled) {
      View view = findViewById(containerId);
      if (!(view instanceof ViewGroup)) {
        throw new IllegalStateException(
            "Since you already called setContentView, it must has a ViewGroup whose id is 'containerId'");
      }
    } else {
      FrameLayout container = new FrameLayout(this);
      container.setId(containerId);
      setContentView(container);
    }

    // let's check whether fragment is already added
    Fragment fragment = findFragment(fragmentClass);
    if (fragment == null) {
      // if not, let's create it and add it
      fragment = Fragment.instantiate(this, fragmentClass.getName(), args);

      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.add(containerId, fragment, fragmentClass.getSimpleName());
      ft.commit();
    }
  }

  /**
   * Allows to retrieve the instance of a previously added fragment. We
   * use fragmentClass.getName() to find by tag.
   *
   * @param fragmentClass the fragment class
   * @return the fragment or null if it was has not been added
   */
  <T> T findFragment(Class<? extends T> fragmentClass) {
    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentByTag(fragmentClass.getSimpleName());
    if (!fragmentClass.isInstance(fragment)) {
      return null;
    }
    //noinspection unchecked
    return (T) fragment;
  }
}
