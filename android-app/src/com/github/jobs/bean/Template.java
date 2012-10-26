package com.github.jobs.bean;

import java.util.List;

/**
 * @author cristian
 * @version 1.0
 */
public class Template {
    private long id;
    private String name;
    private String content;
    private long lastUpdate;
    private List<TemplateService> templateServices;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<TemplateService> getTemplateServices() {
        return templateServices;
    }

    public void setTemplateServices(List<TemplateService> templateServices) {
        this.templateServices = templateServices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Template template = (Template) o;

        if (lastUpdate != template.lastUpdate) return false;
        if (id != template.id) return false;
        if (content != null ? !content.equals(template.content) : template.content != null) return false;
        if (name != null ? !name.equals(template.name) : template.name != null) return false;
        if (templateServices != null ? !templateServices.equals(template.templateServices) : template.templateServices != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (int) (lastUpdate ^ (lastUpdate >>> 32));
        result = 31 * result + (templateServices != null ? templateServices.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Template{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", templateServices=" + templateServices +
                '}';
    }
}
