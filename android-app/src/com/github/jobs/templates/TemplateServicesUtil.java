package com.github.jobs.templates;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.github.jobs.R;
import com.github.jobs.bean.Service;
import com.github.jobs.bean.TemplateService;
import com.github.jobs.templates.generator.*;
import com.github.jobs.ui.activity.SOUserPickerActivity;
import com.github.jobs.ui.dialog.ServiceChooserDialog;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.jobs.R.*;
import static com.github.jobs.bean.Service.from;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplateServicesUtil {
    private static final String TAG = "github:jobs:templates";

    private static final Map<String, Class<? extends ServiceGenerator>> MAP = new HashMap<String, Class<? extends ServiceGenerator>>();

    public static final String STACK_OVERFLOW = "stack_overflow";
    public static final String WEBSITE = "website";
    public static final String GITHUB = "github";
    public static final String SKYPE = "skype";
    public static final String LINKED_IN = "linked_in";

    static {
        MAP.put(STACK_OVERFLOW, StackOverflowGenerator.class);
        MAP.put(WEBSITE, WebsiteGenerator.class);
        MAP.put(GITHUB, GithubGenerator.class);
        MAP.put(SKYPE, SkypeGenerator.class);
        MAP.put(LINKED_IN, LinkedInGenerator.class);
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
            Service github = from(context, id.service_github, string.lbl_github, drawable.logo_github);
            Service so = from(context, id.service_so, string.lbl_stack_overflow, drawable.logo_so);
            Service linkedIn = from(context, id.service_linked_in, string.lbl_linked_in, drawable.logo_linked_in);
            Service skype = from(context, id.service_skype, string.lbl_skype, drawable.logo_skype);
            Service aboutMe = from(context, id.service_about_me, string.lbl_about_me, drawable.logo_about_me);
            sServices = Arrays.asList(github, so, linkedIn, skype, aboutMe);
        }
        return sServices;
    }

    /**
     * @param id service id
     * @return the service drawable resource
     */
    public static int getServiceDrawable(int id) {
        if (sServices != null) {
            for (Service service : sServices) {
                if (service.getId() == id) {
                    return service.getDrawable();
                }
            }
        }
        return 0;
    }

    /**
     * This is used to set a hint to the edit text when
     * configuring a service via {@link ServiceChooserDialog}
     *
     * @param id service id
     * @return the service hint string resource
     */
    public static int getHint(int id) {
        switch (id) {
            case R.id.service_about_me:
                return R.string.your_about_me_username;
            case R.id.service_github:
                return R.string.your_github_username;
            case R.id.service_skype:
                return R.string.your_skype_username;
            case R.id.service_linked_in:
                return string.your_linked_in_username;
        }
        return string.your_username;
    }

    /**
     * This is used to set a hint to the edit text when
     * configuring a service via {@link ServiceChooserDialog}
     *
     * @param id service id
     * @return the service hint string resource
     */
    public static int getAddServiceButtonLabel(int id) {
        switch (id) {
            case R.id.service_about_me:
                return string.get_about_me_user_info;
            case R.id.service_github:
                return string.get_github_user_info;
            case R.id.service_skype:
                return string.get_skype_user_info;
            case R.id.service_linked_in:
                return string.get_linked_in_user_info;
        }
        return string.get_user_info;
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
        String serviceType = templateService.getType();
        if (MAP.containsKey(serviceType)) {
            Class<? extends ServiceGenerator> theClass = MAP.get(serviceType);
            try {
                Constructor<? extends ServiceGenerator> constructor = theClass.getConstructor(Context.class);
                ServiceGenerator serviceGenerator = constructor.newInstance(context);
                return serviceGenerator.generate(templateService);
            } catch (Exception e) {
                Log.wtf(TAG, "Failed to generate template for: " + templateService, e);
            }
        }
        return context.getString(string.cover_letter_footer, templateService.getType(), templateService.getData());
    }

    public static TemplateService getTemplateFromResult(int serviceId, Intent result) {
        for (Service service : sServices) {
            if (service.getId() == serviceId) {
                switch (serviceId) {
                    case id.service_github:
                        return buildFrom(result, GITHUB);
                    case id.service_skype:
                        return buildFrom(result, SKYPE);
                    case id.service_linked_in:
                        return buildFrom(result, LINKED_IN);
                }
            }
        }
        return null;
    }

    private static TemplateService buildFrom(Intent result, String type) {
        TemplateService templateService = new TemplateService();
        templateService.setType(type);
        templateService.setData(result.getStringExtra(ServiceChooserDialog.RESULT_SERVICE_DATA));
        return templateService;
    }
}
