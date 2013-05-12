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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.jobs.R;
import com.github.jobs.bean.SOUser;
import com.github.jobs.ui.activity.SOUserPickerActivity;
import com.github.jobs.utils.WebsiteHelper;
import com.telly.wasp.BitmapHelper;
import com.telly.wasp.BitmapObserver;
import com.telly.wasp.BitmapUtils;

/**
 * @author cristian
 * @version 1.0
 */
public class SOUserDialog extends TrackDialog implements View.OnClickListener {

  public static final int REQUEST_CODE = 12;

  private String mTitle;
  private SOUser mSoUser;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mSoUser = getIntent().getParcelableExtra(SOUserPickerActivity.EXTRA_USER);
    if (mSoUser == null) {
      Toast.makeText(this, R.string.invalid_so_user, Toast.LENGTH_LONG).show();
      finish();
      return;
    }
    setContentView(R.layout.so_user_dialog);

    String websiteUrl = mSoUser.getWebsiteUrl();

    findViewById(R.id.btn_choose_user).setOnClickListener(this);
    findViewById(R.id.btn_choose_user_and_website).setOnClickListener(this);
    mTitle = mSoUser.getDisplayName();

    TextView reputation = (TextView) findViewById(R.id.lbl_reputation);
    reputation.setText(getString(R.string.reputation, mSoUser.getReputation()));

    TextView website = (TextView) findViewById(R.id.lbl_website);
    if (websiteUrl == null) {
      website.setVisibility(View.GONE);
      findViewById(R.id.btn_choose_user_and_website).setVisibility(View.GONE);
    } else {
      website.setText(getString(R.string.website, websiteUrl));
    }

    TextView goldBadge = (TextView) findViewById(R.id.lbl_gold);
    TextView silverBadge = (TextView) findViewById(R.id.lbl_silver);
    TextView bronzeBadge = (TextView) findViewById(R.id.lbl_bronze);

    int gold = mSoUser.getBadgeCount().getGold();
    int silver = mSoUser.getBadgeCount().getSilver();
    int bronze = mSoUser.getBadgeCount().getBronze();
    if (gold > 0) {
      goldBadge.setVisibility(View.VISIBLE);
      goldBadge.setText(String.valueOf(gold));
    }
    if (silver > 0) {
      silverBadge.setVisibility(View.VISIBLE);
      silverBadge.setText(String.valueOf(silver));
    }
    if (bronze > 0) {
      bronzeBadge.setVisibility(View.VISIBLE);
      bronzeBadge.setText(String.valueOf(bronze));
    }

    Linkify.addLinks(website, Linkify.WEB_URLS);

    ImageView userAvatar = (ImageView) findViewById(R.id.img_user_avatar);
    BitmapHelper bitmapHelper = BitmapHelper.getInstance();
    Bitmap avatar = bitmapHelper.getBitmap(mSoUser.getProfileImage());
    if (BitmapUtils.isBitmapValid(avatar)) {
      userAvatar.setImageBitmap(avatar);
    } else {
      BitmapObserver observer =
          new BitmapObserver(userAvatar, mSoUser.getProfileImage(), new Handler());
      bitmapHelper.registerBitmapObserver(this, observer);
    }
    userAvatar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        WebsiteHelper.launchWebsite(SOUserDialog.this, mSoUser.getLink());
      }
    });
  }

  @Override protected void onTitleChanged(CharSequence title, int color) {
    super.onTitleChanged(this.mTitle, color);
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_choose_user:
        mSoUser.setWebsiteUrl(null);
        sendResultBack();
        break;
      case R.id.btn_choose_user_and_website:
        sendResultBack();
        break;
    }
  }

  private void sendResultBack() {
    Intent data = new Intent();
    data.putExtra(SOUserPickerActivity.EXTRA_USER, mSoUser);
    setResult(RESULT_OK, data);
    finish();
  }
}
