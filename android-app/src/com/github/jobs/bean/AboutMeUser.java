package com.github.jobs.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * @author cristian
 * @version 1.0
 */
public class AboutMeUser implements Parcelable {
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    private String header;
    private String profile;
    private String bio;
    @SerializedName("websites")
    private AboutMeService[] services;

    public AboutMeUser(Parcel in) {
        firstName = (String) in.readValue(null);
        lastName = (String) in.readValue(null);
        header = (String) in.readValue(null);
        profile = (String) in.readValue(null);
        bio = (String) in.readValue(null);
        services = (AboutMeService[]) in.readParcelableArray(AboutMeService.class.getClassLoader());
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public AboutMeService[] getServices() {
        return services;
    }

    public void setServices(AboutMeService[] services) {
        this.services = services;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AboutMeUser that = (AboutMeUser) o;

        if (bio != null ? !bio.equals(that.bio) : that.bio != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (header != null ? !header.equals(that.header) : that.header != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (profile != null ? !profile.equals(that.profile) : that.profile != null) return false;
        if (services != null ? !Arrays.equals(services, that.services) : that.services != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (header != null ? header.hashCode() : 0);
        result = 31 * result + (profile != null ? profile.hashCode() : 0);
        result = 31 * result + (bio != null ? bio.hashCode() : 0);
        result = 31 * result + (services != null ? Arrays.hashCode(services) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AboutMeUser{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", header='" + header + '\'' +
                ", profile='" + profile + '\'' +
                ", bio='" + bio + '\'' +
                ", services='" + Arrays.toString(services) + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(firstName);
        dest.writeValue(lastName);
        dest.writeValue(header);
        dest.writeValue(profile);
        dest.writeValue(bio);
        dest.writeParcelableArray(services, flags);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static final Parcelable.Creator<AboutMeUser> CREATOR = new Parcelable.Creator<AboutMeUser>() {

        @Override
        public AboutMeUser createFromParcel(Parcel source) {
            return new AboutMeUser(source);
        }

        @Override
        public AboutMeUser[] newArray(int size) {
            return new AboutMeUser[size];
        }
    };
}
