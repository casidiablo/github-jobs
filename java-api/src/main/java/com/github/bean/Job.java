/*
 * Copyright 2012 CodeSlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * @author cristian
 */
public class Job implements Parcelable {
    private String id;
    private String title;
    @SerializedName("company_logo")
    private String companyLogo;
    private String location;
    private String description;
    private String company;
    @SerializedName("how_to_apply")
    private String howToApply;
    @SerializedName("created_at")
    private String createdAt;
    private String type;
    private String url;
    @SerializedName("company_url")
    private String companyUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getHowToApply() {
        return howToApply;
    }

    public void setHowToApply(String howToApply) {
        this.howToApply = howToApply;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCompanyUrl() {
        return companyUrl;
    }

    public void setCompanyUrl(String companyUrl) {
        this.companyUrl = companyUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Job.class != o.getClass()) return false;

        Job job = (Job) o;

        if (company != null ? !company.equals(job.company) : job.company != null) return false;
        if (companyLogo != null ? !companyLogo.equals(job.companyLogo) : job.companyLogo != null) return false;
        if (companyUrl != null ? !companyUrl.equals(job.companyUrl) : job.companyUrl != null) return false;
        if (createdAt != null ? !createdAt.equals(job.createdAt) : job.createdAt != null) return false;
        if (description != null ? !description.equals(job.description) : job.description != null) return false;
        if (howToApply != null ? !howToApply.equals(job.howToApply) : job.howToApply != null) return false;
        if (id != null ? !id.equals(job.id) : job.id != null) return false;
        if (location != null ? !location.equals(job.location) : job.location != null) return false;
        if (title != null ? !title.equals(job.title) : job.title != null) return false;
        if (type != null ? !type.equals(job.type) : job.type != null) return false;
        if (url != null ? !url.equals(job.url) : job.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (companyLogo != null ? companyLogo.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (howToApply != null ? howToApply.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (companyUrl != null ? companyUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", companyLogo='" + companyLogo + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", company='" + company + '\'' +
                ", howToApply='" + howToApply + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", companyUrl='" + companyUrl + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(title);
        dest.writeValue(companyLogo);
        dest.writeValue(location);
        dest.writeValue(description);
        dest.writeValue(company);
        dest.writeValue(howToApply);
        dest.writeValue(createdAt);
        dest.writeValue(type);
        dest.writeValue(url);
        dest.writeValue(companyUrl);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel source) {
            Job job = new Job();
            job.setId((String) source.readValue(null));
            job.setTitle((String) source.readValue(null));
            job.setCompanyLogo((String) source.readValue(null));
            job.setLocation((String) source.readValue(null));
            job.setDescription((String) source.readValue(null));
            job.setCompany((String) source.readValue(null));
            job.setHowToApply((String) source.readValue(null));
            job.setCreatedAt((String) source.readValue(null));
            job.setType((String) source.readValue(null));
            job.setUrl((String) source.readValue(null));
            job.setCompanyUrl((String) source.readValue(null));
            return job;
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };
}
