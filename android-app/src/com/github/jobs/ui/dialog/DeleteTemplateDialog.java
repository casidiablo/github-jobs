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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.github.jobs.R;
import com.github.jobs.events.DeleteTemplate;

/**
 * @author cristian
 * @version 1.0
 */
public class DeleteTemplateDialog extends BusDialog {

  public static final String TAG = DeleteTemplateDialog.class.getSimpleName();

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity())
        .setTitle(R.string.are_you_sure)
        .setMessage(R.string.are_you_really_sure)
        .setPositiveButton(R.string.yes_i_am, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            bus.post(new DeleteTemplate());
          }
        })
        .setNegativeButton(R.string.cancel, null)
        .create();
  }
}
