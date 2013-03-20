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

package com.github.jobs.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.telly.groundy.adapter.Layout;
import com.telly.groundy.adapter.ListBaseAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.Template;
import com.github.jobs.utils.RelativeDate;
import com.github.jobs.utils.StringUtils;
import com.petebevin.markdown.MarkdownProcessor;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplatesAdapter extends ListBaseAdapter<Template, TemplatesAdapter.ViewHolder> {

    private static final MarkdownProcessor MARKDOWN = new MarkdownProcessor();

    public TemplatesAdapter(Context context) {
        super(context, ViewHolder.class);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public void populateHolder(int position, View view, ViewGroup parent, Template template, ViewHolder holder) {
        // set template name
        holder.title.setText(StringUtils.trim(template.getName()));

        // set template content
        String content = template.getContent();
        if (content != null) {
            if (content.length() > 150) {
                content = content.substring(0, 150);
            }
            String html = Html.fromHtml(MARKDOWN.markdown(content)).toString();
            html = html.replace("\n\n", " ").replace("\n", " ");
            holder.content.setText(StringUtils.trim(html));
        } else {
            holder.content.setText("");
        }

        // set date
        holder.date.setText(RelativeDate.getTimeAgo(getContext(), template.getLastUpdate()));
    }

    @Layout(R.layout.template_row)
    public static class ViewHolder {
        TextView title;
        TextView content;
        TextView date;
    }
}
