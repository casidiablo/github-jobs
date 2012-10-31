package com.github.jobs.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.SparseBooleanArray;
import com.github.jobs.R;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.ui.activity.EditTemplateActivity;

import java.util.ArrayList;

/**
 * @author cristian
 * @version 1.0
 */
public class RemoveServicesDialog extends DialogFragment {

    public static final String ARG_SERVICES = "com.github.templates.arg.services";
    public static final String TAG = RemoveServicesDialog.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ArrayList<Parcelable> services = getArguments().getParcelableArrayList(ARG_SERVICES);
        CharSequence[] items = new CharSequence[services.size()];
        for (int i = 0, servicesSize = services.size(); i < servicesSize; i++) {
            TemplateService service = (TemplateService) services.get(i);
            items[i] = getString(R.string.service_list_item, service.getType(), service.getData());
        }
        final SparseBooleanArray checked = new SparseBooleanArray();
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_remove_services)
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checked.put(which, isChecked);
                    }
                })
                .setPositiveButton(R.string.remove_selected_services, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // get a list of the services to delete
                        ArrayList<TemplateService> toRemove = new ArrayList<TemplateService>();
                        for (int i = 0; i < checked.size(); i++) {
                            if (checked.valueAt(i)) {
                                int index = checked.keyAt(i);
                                toRemove.add((TemplateService) services.get(index));
                            }
                        }
                        // fire service deletion
                        FragmentActivity activity = getActivity();
                        if (activity instanceof EditTemplateActivity) {
                            EditTemplateActivity editTemplateActivity = (EditTemplateActivity) activity;
                            editTemplateActivity.removeServices(toRemove);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }
}
