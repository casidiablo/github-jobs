package com.github.jobs.bean;

import com.codeslap.persistence.pref.Preference;

/**
 * @author cristian
 * @version 1.0
 */
public class GeneralSettings {
    @Preference(value = "already_created_default_template", ignore = true, defaultValue = "false")
    private boolean alreadyCreatedDefaultTemplate;

    public boolean hasAlreadyCreatedDefaultTemplate() {
        return alreadyCreatedDefaultTemplate;
    }

    public void setAlreadyCreatedDefaultTemplate(boolean alreadyCreatedDefaultTemplate) {
        this.alreadyCreatedDefaultTemplate = alreadyCreatedDefaultTemplate;
    }
}
