package com.github.jobs.ui;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.github.jobs.R;

/**
 * @author cristian
 */
public class HowToApplyDialog extends SherlockActivity {

    public static final String EXTRA_HOW_TO_APPLY = "how_to_apply";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_to_apply_dialog);

        Spanned html = Html.fromHtml(getIntent().getStringExtra(EXTRA_HOW_TO_APPLY));
        SpannableString application = new SpannableString(html);
        Linkify.addLinks(application, Linkify.ALL);

        TextView howToApply = (TextView) findViewById(R.id.lbl_how_to_apply);
        howToApply.setText(application);
        howToApply.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
