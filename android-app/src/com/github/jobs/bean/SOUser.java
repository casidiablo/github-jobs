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
public class SOUser implements Parcelable {
    @SerializedName("user_id")
    private long id;
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("profile_image")
    private String profileImage;
    private int reputation;
    private String link;
    @SerializedName("website_url")
    private String websiteUrl;
    @SerializedName("account_id")
    private int accountId;
    @SerializedName("badge_counts")
    private BadgeCount badgeCount;

    public SOUser(Parcel in) {
        id = (Long) in.readValue(null);
        displayName = (String) in.readValue(null);
        profileImage = (String) in.readValue(null);
        reputation = (Integer) in.readValue(null);
        link = (String) in.readValue(null);
        websiteUrl = (String) in.readValue(null);
        accountId = (Integer) in.readValue(null);
        badgeCount = (BadgeCount) in.readValue(BadgeCount.class.getClassLoader());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public BadgeCount getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(BadgeCount badgeCount) {
        this.badgeCount = badgeCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SOUser that = (SOUser) o;

        if (accountId != that.accountId) return false;
        if (id != that.id) return false;
        if (reputation != that.reputation) return false;
        if (badgeCount != null ? !badgeCount.equals(that.badgeCount) : that.badgeCount != null) return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (link != null ? !link.equals(that.link) : that.link != null) return false;
        if (profileImage != null ? !profileImage.equals(that.profileImage) : that.profileImage != null) return false;
        if (websiteUrl != null ? !websiteUrl.equals(that.websiteUrl) : that.websiteUrl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (profileImage != null ? profileImage.hashCode() : 0);
        result = 31 * result + reputation;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (websiteUrl != null ? websiteUrl.hashCode() : 0);
        result = 31 * result + accountId;
        result = 31 * result + (badgeCount != null ? badgeCount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SOUser{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", reputation=" + reputation +
                ", link='" + link + '\'' +
                ", websiteUrl='" + websiteUrl + '\'' +
                ", accountId=" + accountId +
                ", badgeCount=" + badgeCount +
                '}';
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(displayName);
        dest.writeValue(profileImage);
        dest.writeValue(reputation);
        dest.writeValue(link);
        dest.writeValue(websiteUrl);
        dest.writeValue(accountId);
        dest.writeValue(badgeCount);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static final Parcelable.Creator<SOUser> CREATOR = new Parcelable.Creator<SOUser>() {
        public SOUser createFromParcel(Parcel in) {
            return new SOUser(in);
        }

        public SOUser[] newArray(int size) {
            return new SOUser[size];
        }
    };
}
