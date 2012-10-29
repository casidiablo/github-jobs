package com.github.jobs.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author cristian
 * @version 1.0
 */
public class TemplateService implements Parcelable {
    private long id;
    private String type;
    private String data;

    public TemplateService() {
    }

    @SuppressWarnings("UnusedDeclaration")
    public TemplateService(Parcel in) {
        id = (Long) in.readValue(null);
        type = (String) in.readValue(null);
        data = (String) in.readValue(null);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemplateService service = (TemplateService) o;

        if (id != service.id) return false;
        if (data != null ? !data.equals(service.data) : service.data != null) return false;
        if (type != null ? !type.equals(service.type) : service.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TemplateService{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(type);
        dest.writeValue(data);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static final Parcelable.Creator<TemplateService> CREATOR = new Parcelable.Creator<TemplateService>() {
        public TemplateService createFromParcel(Parcel in) {
            return new TemplateService(in);
        }

        public TemplateService[] newArray(int size) {
            return new TemplateService[size];
        }
    };
}
