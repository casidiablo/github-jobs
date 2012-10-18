package com.github.jobs.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.github.jobs.R;
import com.github.jobs.bean.Template;
import com.github.jobs.utils.RelativeDate;
import com.github.jobs.utils.StringUtils;
import com.petebevin.markdown.MarkdownProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplatesAdapter extends BaseAdapter {

    private static final MarkdownProcessor MARKDOWN = new MarkdownProcessor();
    private final LayoutInflater mInflater;
    private final List<Template> mTemplates = new ArrayList<Template>();
    private final Context mContext;

    public TemplatesAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mTemplates.size();
    }

    @Override
    public Template getItem(int position) {
        return mTemplates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.template_row, null);

            holder.title = (TextView) view.findViewById(com.github.jobs.R.id.title);
            holder.content = (TextView) view.findViewById(com.github.jobs.R.id.content);
            holder.date = (TextView) view.findViewById(com.github.jobs.R.id.date);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Template template = getItem(position);
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
        holder.date.setText(RelativeDate.getTimeAgo(mContext, template.getLastUpdate()));

        return view;
    }

    public void updateItems(List<Template> data) {
        mTemplates.clear();
        mTemplates.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        mTemplates.clear();
    }

    private static class ViewHolder {
        TextView title;
        TextView content;
        TextView date;
    }
}
