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

package com.github.jobs.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * @author cristian
 * @version 1.0
 */
public class AboutMeService implements Parcelable {
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("platform")
    private String platform;
    @SerializedName("service_url")
    private String serviceUrl;

    public AboutMeService(Parcel in) {
        displayName = (String) in.readValue(null);
        platform = (String) in.readValue(null);
        serviceUrl = (String) in.readValue(null);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AboutMeService that = (AboutMeService) o;

        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (platform != null ? !platform.equals(that.platform) : that.platform != null) return false;
        if (serviceUrl != null ? !serviceUrl.equals(that.serviceUrl) : that.serviceUrl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = displayName != null ? displayName.hashCode() : 0;
        result = 31 * result + (platform != null ? platform.hashCode() : 0);
        result = 31 * result + (serviceUrl != null ? serviceUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AboutMeService{" +
                "displayName='" + displayName + '\'' +
                ", platform='" + platform + '\'' +
                ", serviceUrl='" + serviceUrl + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(displayName);
        dest.writeValue(platform);
        dest.writeValue(serviceUrl);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static final Parcelable.Creator<AboutMeService> CREATOR = new Parcelable.Creator<AboutMeService>() {

        @Override
        public AboutMeService createFromParcel(Parcel source) {
            return new AboutMeService(source);
        }

        @Override
        public AboutMeService[] newArray(int size) {
            return new AboutMeService[size];
        }
    };
}
