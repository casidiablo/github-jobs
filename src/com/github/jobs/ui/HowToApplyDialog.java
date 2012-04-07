package com.github.jobs.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;
import com.github.jobs.R;

/**
 * @author cristian
 */
public class HowToApplyDialog extends DialogFragment {

    public static final String TAG = HowToApplyDialog.class.getSimpleName();
    public static final String HOW_TO_APPLY = "how_to_apply";
    private AlertDialog alertDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SpannableString application = new SpannableString(Html.fromHtml(getArguments().getString(HOW_TO_APPLY)));
        Linkify.addLinks(application, Linkify.ALL);
        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.how_to_apply)
                .setMessage(application)
                .create();
        return alertDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (alertDialog != null) {
            ((TextView)alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static DialogFragment getInstance(String howToApply) {
        HowToApplyDialog howToApplyDialog = new HowToApplyDialog();
        Bundle args = new Bundle();
        args.putString(HOW_TO_APPLY, howToApply);
        howToApplyDialog.setArguments(args);
        return howToApplyDialog;
    }
}
