package com.github.jobs.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.codeslap.groundy.adapter.Layout;
import com.codeslap.groundy.adapter.ListBaseAdapter;
import com.codeslap.groundy.adapter.ResourceId;
import com.github.jobs.R;
import com.github.jobs.ui.dialog.HowToApplyDialog;

/**
 * @author cristian
 * @version 1.0
 */
public class ApplyChoicesAdapter extends ListBaseAdapter<HowToApplyDialog.ApplyOption, ApplyChoicesAdapter.ViewHolder> {
    public ApplyChoicesAdapter(Context context) {
        super(context, ViewHolder.class);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public void populateHolder(int position, View view, ViewGroup parent, HowToApplyDialog.ApplyOption item, ViewHolder holder) {
        if (item.type == HowToApplyDialog.TYPE_EMAIL) {
            holder.label.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0, 0, 0);
        } else {
            holder.label.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_website, 0, 0, 0);
        }
        holder.label.setText(item.data);
    }

    @Layout(R.layout.apply_option_item)
    public static class ViewHolder {
        @ResourceId(R.id.lbl_apply_option)
        TextView label;
    }
}
