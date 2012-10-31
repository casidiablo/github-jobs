package com.github.jobs.templates;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.github.jobs.R;
import com.github.jobs.bean.Service;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.templates.services.*;
import com.github.jobs.ui.activity.SOUserPickerActivity;
import com.github.jobs.ui.dialog.ServiceChooserDialog;

import java.util.ArrayList;
import java.util.List;

import static com.github.jobs.bean.Service.from;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplatesHelper {
    private static final List<ServiceContract> SERVICES = new ArrayList<ServiceContract>();

    static {
        SERVICES.add(new AboutMeService());
        SERVICES.add(new GithubService());
        SERVICES.add(new StackOverflowService());
        SERVICES.add(new LinkedInService());
        SERVICES.add(new SkypeService());
    }

    private static List<Service> sServices;

    /**
     * Creates and returns a list of available services
     *
     * @param context used to get string resources
     * @return a list of available services
     */
    public static List<Service> getServices(Context context) {
        if (sServices == null) {
            sServices = new ArrayList<Service>();
            for (ServiceContract service : SERVICES) {
                sServices.add(from(context, service.getId(), service.getServiceLabel(), service.getDrawable()));
            }
        }
        return sServices;
    }

    /**
     * @param id service id
     * @return the service drawable resource
     */
    public static int getServiceDrawable(int id) {
        return findService(id).getDrawable();
    }

    /**
     * This is used to set a hint to the edit text when
     * configuring a service via {@link com.github.jobs.ui.dialog.ServiceChooserDialog}
     *
     * @param id service id
     * @return the service hint string resource
     */
    public static int getHint(int id) {
        return findService(id).getHint();
    }

    /**
     * This is used to set a hint to the edit text when
     * configuring a service via {@link com.github.jobs.ui.dialog.ServiceChooserDialog}
     *
     * @param id service id
     * @return the service hint string resource
     */
    public static int getAddServiceButtonLabel(int id) {
        return findService(id).getAddServiceLabel();
    }

    public static void resolve(Activity activity, int id) {
        switch (id) {
            case R.id.service_so:
                Intent intent = new Intent(activity, SOUserPickerActivity.class);
                activity.startActivityForResult(intent, SOUserPickerActivity.REQUEST_CODE);
                break;
        }
    }

    public static String getContent(Context context, TemplateService templateService) {
        if (templateService == null) {
            return null;
        }
        ServiceContract service = findService(templateService.getType());
        return service.getGenerator(context).generate(templateService);
    }

    public static TemplateService getTemplateFromResult(int serviceId, Intent result) {
        TemplateService templateService = new TemplateService();
        templateService.setType(findService(serviceId).getType());
        templateService.setData(result.getStringExtra(ServiceChooserDialog.RESULT_SERVICE_DATA));
        return templateService;
    }

    /**
     * Retrieves a service contract
     *
     * @param id service contract id
     * @return the found service contract
     */
    private static ServiceContract findService(int id) {
        for (ServiceContract service : SERVICES) {
            if (service.getId() == id) {
                return service;
            }
        }
        return null;
    }

    /**
     * Retrieves a service contract
     *
     * @param type service contract type
     * @return the found service contract
     */
    private static ServiceContract findService(String type) {
        for (ServiceContract service : SERVICES) {
            if (service.getType().equals(type)) {
                return service;
            }
        }
        return null;
    }

}
