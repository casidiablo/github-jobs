package com.github.jobs.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import com.github.jobs.R;
import com.github.jobs.ui.activity.EditTemplateActivity;

/**
 * @author cristian
 * @version 1.0
 */
public class DeleteTemplateDialog extends DialogFragment {

    public static final String TAG = DeleteTemplateDialog.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.are_you_sure)
                .setMessage(R.string.are_you_really_sure)
                .setPositiveButton(R.string.yes_i_am, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentActivity activity = getActivity();
                        if (activity instanceof EditTemplateActivity) {
                            EditTemplateActivity templateDetailsActivity = (EditTemplateActivity) activity;
                            templateDetailsActivity.doDelete();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }
}
