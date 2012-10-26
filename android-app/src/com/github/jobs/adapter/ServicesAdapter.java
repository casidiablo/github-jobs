package com.github.jobs.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.codeslap.groundy.adapter.Layout;
import com.codeslap.groundy.adapter.ListBaseAdapter;
import com.github.jobs.R;
import com.github.jobs.bean.Service;

/**
 * @author cristian
 * @version 1.0
 */
public class ServicesAdapter extends ListBaseAdapter<Service, ServicesAdapter.ViewHolder> {

    public ServicesAdapter(Context context) {
        super(context, ViewHolder.class);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public void populateHolder(int position, View view, ViewGroup parent, Service item, ViewHolder holder) {
        holder.item.setImageResource(item.getDrawable());
    }

    @Layout(R.layout.service_item)
    public static class ViewHolder {
        ImageView item;
    }
}
