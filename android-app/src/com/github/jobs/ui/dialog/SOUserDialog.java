package com.github.jobs.ui.dialog;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SOUser soUser = (SOUser) getIntent().getParcelableExtra(EXTRA_USER);
        if (soUser == null) {
            Toast.makeText(this, R.string.invalid_so_user, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        setContentView(R.layout.so_user_dialog);

        TextView displayName = (TextView) findViewById(R.id.lbl_username);
        displayName.setText(soUser.getDisplayName());

        TextView reputation = (TextView) findViewById(R.id.lbl_reputation);
        reputation.setText(getString(R.string.reputation, soUser.getDisplayName()));


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
    }
}
