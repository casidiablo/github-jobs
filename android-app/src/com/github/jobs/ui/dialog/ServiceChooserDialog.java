package com.github.jobs.ui.dialog;

import android.os.Bundle;
import android.widget.GridView;
import com.github.jobs.R;
import com.github.jobs.adapter.ServicesAdapter;
import com.github.jobs.utils.TemplateServicesUtil;

/**
 * @author cristian
 * @version 1.0
 */
public class ServiceChooserDialog extends TrackDialog {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_chooser_dialog);

        ServicesAdapter adapter = new ServicesAdapter(this);
        adapter.updateItems(TemplateServicesUtil.getServices(this));

        GridView grid = (GridView) findViewById(R.id.services_grid);
        grid.setAdapter(adapter);
    }
}
