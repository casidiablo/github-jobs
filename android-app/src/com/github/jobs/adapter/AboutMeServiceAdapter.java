package com.github.jobs.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import com.codeslap.groundy.adapter.Layout;
import com.codeslap.groundy.adapter.ListBaseAdapter;
import com.codeslap.groundy.adapter.ResourceId;
import com.github.jobs.R;
import com.github.jobs.bean.AboutMeService;

/**
 * @author cristian
 * @version 1.0
 */
public class AboutMeServiceAdapter extends ListBaseAdapter<AboutMeService, AboutMeServiceAdapter.ViewHolder> {

    private static final String FACEBOOK = "facebook";

    public AboutMeServiceAdapter(Context context) {
        super(context, ViewHolder.class);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public void populateHolder(int position, View view, ViewGroup parent, AboutMeService item, ViewHolder holder) {
        holder.label.setCheckMarkDrawable(R.drawable.btn_check_holo_light);
        if (FACEBOOK.equals(item.getPlatform())) {
            holder.label.setText(getContext().getString(R.string.lbl_facebook));
        } else {
            holder.label.setText(item.getDisplayName());
        }
    }

    @Layout(android.R.layout.simple_list_item_multiple_choice)
    public static class ViewHolder {
        @ResourceId(android.R.id.text1)
        CheckedTextView label;
    }
}
