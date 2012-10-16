package com.github.jobs.ui.dialog;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.github.jobs.R;

import static com.github.jobs.utils.AnalyticsHelper.*;

/**
 * @author cristian
 * @version 1.0
 */
public class AboutDialog extends TrackDialog implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        Typeface font = Typeface.createFromAsset(getAssets(), "droid_font.ttf");

        ((TextView) findViewById(R.id.lbl_about_me)).setTypeface(font);
        ((TextView) findViewById(R.id.lbl_about_this_app)).setTypeface(font);
        ((TextView) findViewById(R.id.lbl_app_libraries)).setTypeface(font);

        findViewById(R.id.btn_tweet).setOnClickListener(this);
        findViewById(R.id.btn_github).setOnClickListener(this);
        findViewById(R.id.btn_stackoverflow).setOnClickListener(this);
        findViewById(R.id.btn_feedback).setOnClickListener(this);
        findViewById(R.id.library_abs).setOnClickListener(this);
        findViewById(R.id.library_maven).setOnClickListener(this);
        findViewById(R.id.library_wasp).setOnClickListener(this);
        findViewById(R.id.library_groundy).setOnClickListener(this);
        findViewById(R.id.library_persistence).setOnClickListener(this);
        findViewById(R.id.lbl_about_me).setOnClickListener(this);

        TextView content = (TextView) findViewById(R.id.about_this_app_content);
        Linkify.addLinks(content, Linkify.WEB_URLS);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_tweet:
                launchWebsite("http://twitter.com/casidiablo");
                break;
            case R.id.btn_github:
                launchWebsite("http://github.com/casidiablo");
                break;
            case R.id.btn_stackoverflow:
                launchWebsite("http://stackoverflow.com/users/244296/cristian");
                break;
            case R.id.btn_feedback:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"cristian@elhacker.net"});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject_about_app));
                getTracker(this).trackEvent(CATEGORY_ABOUT, ACTION_EMAIL, LABEL_CONTACT);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, getString(R.string.cannot_launch_email_app), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.library_maven:
                launchWebsite("http://maven.apache.org/");
                break;
            case R.id.library_abs:
                launchWebsite("http://actionbarsherlock.com/");
                break;
            case R.id.library_wasp:
                launchWebsite("https://github.com/twitvid/wasp");
                break;
            case R.id.library_groundy:
                launchWebsite("https://github.com/casidiablo/groundy");
                break;
            case R.id.library_persistence:
                launchWebsite("https://github.com/casidiablo/persistence");
                break;
            case R.id.lbl_about_me:
                findViewById(R.id.about_author_container).setVisibility(View.VISIBLE);
                findViewById(R.id.contact_buttons_container).setVisibility(View.VISIBLE);
                findViewById(R.id.lbl_about_me).setOnClickListener(null);
                break;
        }
    }

    private void launchWebsite(String url) {
        try {
            getTracker(this).trackEvent(CATEGORY_ABOUT, ACTION_OPEN, url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.could_not_launch_url, Toast.LENGTH_LONG).show();
        }
    }
}
