package com.github.jobs.utils;

import android.content.Context;
import android.widget.Toast;

public class ViewUtils {
  private final Context context;

  public ViewUtils(Context context) {
    this.context = context;
  }

  public void toast(String text) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
  }

  public void toast(int resId) {
    Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
  }
}
