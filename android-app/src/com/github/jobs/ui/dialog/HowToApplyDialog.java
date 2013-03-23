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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.codeslap.persistence.SqlAdapter;
import com.github.jobs.GithubJobsApplication;
import com.github.jobs.R;
import com.github.jobs.adapter.ApplyChoicesAdapter;
import com.github.jobs.bean.Template;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.templates.TemplatesHelper;
import com.github.jobs.ui.activity.TemplatesActivity;
import com.github.jobs.utils.WebsiteHelper;
import in.uncod.android.bypass.Bypass;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static com.github.jobs.utils.AnalyticsHelper.NAME_HOW_TO_APPLY;
import static com.github.jobs.utils.AnalyticsHelper.getTracker;

/**
 * @author cristian
 */
public class HowToApplyDialog extends TrackDialog implements View.OnClickListener {
  public static final String EXTRA_HOW_TO_APPLY = "com.github.jobs.extra.how_to_apply";
  public static final String EXTRA_TITLE = "com.github.jobs.extra.title";
  private static final String EXTRA_TEMPLATE_ID = "com.github.jobs.extra.template_id";

  public static final int TYPE_EMAIL = 0;
  private static final int TYPE_WEBSITE = 1;

  private static final int APPLICATION_CHOICES = 884;

  private ArrayList<ApplyOption> mOptions;
  @Inject SqlAdapter adapter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((GithubJobsApplication) getApplication()).inject(this);
    getTracker(this).trackPageView(NAME_HOW_TO_APPLY);
    setContentView(R.layout.how_to_apply_dialog);

    Intent intent = getIntent();
    if (intent == null) {
      return;
    }
    String howToApplyStr = intent.getStringExtra(EXTRA_HOW_TO_APPLY);
    SpannableString application;
    Spanned html;
    if (howToApplyStr == null) {
      application = new SpannableString("");
      html = new SpannableString("");
    } else {
      html = Html.fromHtml(howToApplyStr);
      application = new SpannableString(html);
      Linkify.addLinks(application, Linkify.ALL);
    }

    TextView howToApply = (TextView) findViewById(R.id.lbl_how_to_apply);
    howToApply.setText(application);
    howToApply.setMovementMethod(LinkMovementMethod.getInstance());

    // try to extract emails and websites from within the application body
    String plain = html.toString();
    Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(plain);

    mOptions = new ArrayList<ApplyOption>();
    while (matcher.find()) {
      mOptions.add(new ApplyOption(TYPE_EMAIL, matcher.group(0)));
    }
    matcher = Patterns.WEB_URL.matcher(plain);
    while (matcher.find()) {
      String website = matcher.group(0);
      // consider this only if it has explicitly http:// prefix
      if (website.startsWith("http://") || website.startsWith("https://")) {
        mOptions.add(new ApplyOption(TYPE_WEBSITE, website));
      }
    }

    // show the apply button if there is at least one application option
    if (!mOptions.isEmpty()) {
      findViewById(R.id.btn_apply).setVisibility(View.VISIBLE);
      findViewById(R.id.btn_apply).setOnClickListener(this);
    }
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_apply:
        Intent templates = new Intent(this, TemplatesActivity.class);
        templates.putExtra(TemplatesActivity.EXTRA_PICK, true);
        startActivityForResult(templates, TemplatesActivity.REQUEST_CODE);
        break;
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_OK) {
      return;
    }
    switch (requestCode) {
      case TemplatesActivity.REQUEST_CODE:
        long templateId = data.getLongExtra(TemplatesActivity.EXTRA_TEMPLATE_ID, -1);
        if (templateId != -1) {
          Template template = findTemplate(templateId);
          applyForThisJob(template);
        }
        break;
    }
  }

  @Override protected Dialog onCreateDialog(int id, final Bundle args) {
    switch (id) {
      case APPLICATION_CHOICES:
        ApplyChoicesAdapter adapter = new ApplyChoicesAdapter(this);
        adapter.updateItems(mOptions);

        return new AlertDialog.Builder(this).setTitle(getString(R.string.apply_for_this_job_via)).setAdapter(adapter, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Template template = findTemplate(args.getLong(EXTRA_TEMPLATE_ID));
            applyForThisJob(mOptions.get(which), template);
            dismissDialog(APPLICATION_CHOICES);
          }
        }).create();
    }
    return super.onCreateDialog(id);
  }

  private void applyForThisJob(Template template) {
    if (mOptions.size() == 1) {
      applyForThisJob(mOptions.get(0), template);
    } else {
      Bundle args = new Bundle();
      args.putLong(EXTRA_TEMPLATE_ID, template.getId());
      showDialog(APPLICATION_CHOICES, args);
    }
  }

  private Template findTemplate(long templateId) {
    Template where = new Template();
    where.setId(templateId);
    return adapter.findFirst(where);
  }

  private void applyForThisJob(ApplyOption applyOption, Template template) {
    // build full markdown content
    String markdownContent = template.getContent();
    // add footer if necessary
    List<TemplateService> templateServices = template.getTemplateServices();
    if (templateServices != null && !templateServices.isEmpty()) {
      markdownContent += "\n--------\n";
      String separator = "";
      for (TemplateService service : templateServices) {
        markdownContent += separator + TemplatesHelper.getContent(this, service);
        separator = "\n\n";
      }
    }

    // build HTML email content
    CharSequence emailContent = new Bypass().markdownToSpannable(markdownContent);

    switch (applyOption.type) {
      case TYPE_EMAIL:
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{applyOption.data});
        intent.putExtra(Intent.EXTRA_SUBJECT, getIntent().getStringExtra(EXTRA_TITLE));
        intent.putExtra(Intent.EXTRA_TEXT, emailContent);

        try {
          startActivity(Intent.createChooser(intent, getString(R.string.apply_for_job_with)));
        } catch (Exception e) {
          Toast.makeText(this, getString(R.string.cannot_launch_email_app), Toast.LENGTH_SHORT).show();
        }
        break;
      case TYPE_WEBSITE:
        if (WebsiteHelper.launchWebsite(this, applyOption.data)) {
          // when applying via website, we will send plain content
          String templateContent = /*Html.fromHtml(*/emailContent/*)*/.toString();

          int sdk = android.os.Build.VERSION.SDK_INT;
          if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(templateContent);
          } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText(template.getName(), templateContent);
            clipboard.setPrimaryClip(clip);
          }

          Toast.makeText(this, R.string.template_content_copied_to_clipboard, Toast.LENGTH_LONG).show();
        }
        break;
    }

    // dismiss "dialog"
    finish();
  }

  public static class ApplyOption {
    public final int type;
    public final String data;

    private ApplyOption(int type, String data) {
      this.type = type;
      this.data = data;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ApplyOption that = (ApplyOption) o;

      if (type != that.type) return false;
      if (data != null ? !data.equals(that.data) : that.data != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = type;
      result = 31 * result + (data != null ? data.hashCode() : 0);
      return result;
    }
  }
}
