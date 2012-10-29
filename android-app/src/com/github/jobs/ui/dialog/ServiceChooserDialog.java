package com.github.jobs.ui.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.github.jobs.R;
import com.github.jobs.adapter.ServicesAdapter;
import com.github.jobs.utils.TemplateServicesUtil;

/**
 * @author cristian
 * @version 1.0
 */
public class ServiceChooserDialog extends TrackDialog implements AdapterView.OnItemClickListener {
    public static final String EXTRA_SERVICE_ID = "com.github.jobs.EXTRA_SERVICE_ID";
    public static final int REQUEST_CODE = 623;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_chooser_dialog);

        ServicesAdapter adapter = new ServicesAdapter(this);
        adapter.updateItems(TemplateServicesUtil.getServices(this));

        GridView grid = (GridView) findViewById(R.id.services_grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent data = new Intent();
        data.putExtra(ServiceChooserDialog.EXTRA_SERVICE_ID, (int) id);
        setResult(RESULT_OK, data);
        finish();
    }
}
