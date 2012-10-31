package com.github.jobs.templates.fetcher;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.github.jobs.R;
import com.github.jobs.adapter.AboutMeServiceAdapter;
import com.github.jobs.bean.AboutMeService;
import com.github.jobs.bean.AboutMeUser;
import com.github.util.HttpHandler;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author cristian
 * @version 1.0
 */
public class AboutMeFetcher {
    private static final String USER_LINK = "https://api.about.me/api/v2/json/user/view/%s?client_id=8c63fb1f3ee9fe2bf1d4e0f7888d992607ba7ad2&on_match=true&extended=true";

    public AboutMeUser getAboutMeUser(String user) {
        String url = String.format(USER_LINK, user);
        String response = HttpHandler.getInstance().getRequest(url);
        Gson gson = new Gson();
        AboutMeUser aboutMeUser = gson.fromJson(response, AboutMeUser.class);
        if (aboutMeUser == null) {
            return null;
        }
        if (aboutMeUser.getServices() != null) {
            // check if there is a service that should be removed
            List<AboutMeService> toKeep = new ArrayList<AboutMeService>();
            for (AboutMeService service : aboutMeUser.getServices()) {
                if (service.getServiceUrl() != null) {
                    toKeep.add(service);
                }
            }
            // recreate array with only complete services
            aboutMeUser.setServices(toKeep.toArray(new AboutMeService[toKeep.size()]));
        }
        return aboutMeUser;
    }

    public static void setupConfirmationView(final Context context, ViewGroup root, AboutMeUser aboutMeUser, final AboutMeServicesCallback callback) {
        // inflate view
        View view = LayoutInflater.from(context).inflate(R.layout.about_me_services_list, root);
        final AboutMeService[] services = aboutMeUser.getServices();

        // get views' references
        TextView lblInfo = (TextView) view.findViewById(R.id.lbl_about_me_info);
        final Button importServices = (Button) view.findViewById(R.id.btn_import_services);
        final ListView list = (ListView) view.findViewById(R.id.list_about_me_services);

        // setup views
        lblInfo.setText(aboutMeUser.getFirstName());
        importServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make sure we have at least one service selected
                if (list.getCheckedItemCount() == 0) {
                    Toast.makeText(context, R.string.no_services_checked, Toast.LENGTH_SHORT).show();
                    return;
                }

                // create a list with the selected services
                List<AboutMeService> selectedServices = new ArrayList<AboutMeService>();
                SparseBooleanArray checkedItemPositions = list.getCheckedItemPositions();
                for (int i = 0; i < checkedItemPositions.size(); i++) {
                    if (checkedItemPositions.valueAt(i)) {
                        selectedServices.add(services[i]);
                    }
                }
                callback.onServicesSelected(selectedServices);
            }
        });

        // setup list
        list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        AboutMeServiceAdapter adapter = new AboutMeServiceAdapter(context);
        adapter.updateItems(Arrays.asList(services));
        list.setAdapter(adapter);
        for (int i = 0, servicesLength = services.length; i < servicesLength; i++) {
            list.setItemChecked(i, true);
        }
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // make sure we don't allow importing services if none is selected
                importServices.setEnabled(false);
                SparseBooleanArray checkedItemPositions = list.getCheckedItemPositions();
                for (int i = 0; i < checkedItemPositions.size(); i++) {
                    if (checkedItemPositions.valueAt(i)) {
                        importServices.setEnabled(true);
                        break;
                    }
                }
            }
        });
    }

    public interface AboutMeServicesCallback {
        void onServicesSelected(List<AboutMeService> services);
    }
}
