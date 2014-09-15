/*
 * Copyright 2014 Some Dev
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import com.github.jobs.R;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.events.DeleteServices;
import java.util.ArrayList;

/**
 * @author cristian
 * @version 1.0
 */
public class RemoveServicesDialog extends BusDialog {

  private static final String ARG_SERVICES = "com.github.templates.arg.services";
  public static final String TAG = RemoveServicesDialog.class.getSimpleName();

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    final ArrayList<Parcelable> services = getArguments().getParcelableArrayList(ARG_SERVICES);
    CharSequence[] items = new CharSequence[services.size()];
    for (int i = 0, servicesSize = services.size(); i < servicesSize; i++) {
      TemplateService service = (TemplateService) services.get(i);
      items[i] = getString(R.string.service_list_item, service.getType(), service.getData());
    }
    final SparseBooleanArray checked = new SparseBooleanArray();
    AlertDialog.Builder builder =
        new AlertDialog.Builder(getActivity()).setTitle(R.string.title_remove_services)
            .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checked.put(which, isChecked);
              }
            });
    builder.setPositiveButton(R.string.remove_selected_services,
        new DialogInterface.OnClickListener() {
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
            bus.post(new DeleteServices(toRemove));
          }
        });
    builder.setNegativeButton(R.string.cancel, null);
    return builder.create();
  }

  public static Fragment newInstance(ArrayList<TemplateService> templateServices) {
    // create a bundle with the current services
    Bundle args = new Bundle();
    args.putParcelableArrayList(RemoveServicesDialog.ARG_SERVICES, templateServices);

    RemoveServicesDialog fragment = new RemoveServicesDialog();
    fragment.setArguments(args);
    return fragment;
  }
}
