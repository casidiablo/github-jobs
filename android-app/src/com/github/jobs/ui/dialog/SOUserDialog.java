package com.github.jobs.ui.dialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.jobs.R;
import com.github.jobs.bean.SOUser;
import com.telly.wasp.BitmapHelper;
import com.telly.wasp.BitmapObserver;
import com.telly.wasp.BitmapUtils;

/**
 * @author cristian
 * @version 1.0
 */
public class SOUserDialog extends TrackDialog {

    public static final String EXTRA_USER = "com.github.jobs.extra.user";
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SOUser soUser = (SOUser) getIntent().getParcelableExtra(EXTRA_USER);
        if (soUser == null) {
            Toast.makeText(this, R.string.invalid_so_user, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        setContentView(R.layout.so_user_dialog);

        mTitle = soUser.getDisplayName();

        TextView reputation = (TextView) findViewById(R.id.lbl_reputation);
        reputation.setText(getString(R.string.reputation, soUser.getReputation()));

        TextView website = (TextView) findViewById(R.id.lbl_website);
        website.setText(getString(R.string.website, soUser.getWebsiteUrl()));

        TextView goldBadge = (TextView) findViewById(R.id.lbl_gold);
        TextView silverBadge = (TextView) findViewById(R.id.lbl_silver);
        TextView bronzeBadge = (TextView) findViewById(R.id.lbl_bronze);

        int gold = soUser.getBadgeCount().getGold();
        int silver = soUser.getBadgeCount().getSilver();
        int bronze = soUser.getBadgeCount().getBronze();
        if (gold > 0) {
            goldBadge.setVisibility(View.VISIBLE);
            goldBadge.setText(" " + String.valueOf(gold) + " ");
        }
        if (silver > 0) {
            silverBadge.setVisibility(View.VISIBLE);
            silverBadge.setText(" " + String.valueOf(silver) + " ");
        }
        if (bronze > 0) {
            bronzeBadge.setVisibility(View.VISIBLE);
            bronzeBadge.setText(" " + String.valueOf(bronze));
        }

        Linkify.addLinks(website, Linkify.WEB_URLS);

        ImageView userAvatar = (ImageView) findViewById(R.id.img_user_avatar);
        BitmapHelper bitmapHelper = BitmapHelper.getInstance();
        Bitmap avatar = bitmapHelper.getBitmap(soUser.getProfileImage());
        if (BitmapUtils.isBitmapValid(avatar)) {
            userAvatar.setImageBitmap(avatar);
        } else {
            userAvatar.setTag(soUser.getProfileImage());
            BitmapObserver observer = new BitmapObserver(userAvatar, soUser.getProfileImage(), new Handler());
            bitmapHelper.registerBitmapObserver(this, observer);
        }
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWebsite(soUser.getLink());
            }
        });
    }

    private void launchWebsite(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.could_not_launch_url, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(this.mTitle, color);
    }
}
